package dev.jordond.compass.autocomplete

import dev.jordond.compass.Place
import dev.jordond.compass.test.place
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class AutocompleteTest {

    private class FakeService<T>(
        var available: Boolean = true,
        var onSearch: suspend (String) -> List<T>,
    ) : AutocompleteService<T> {

        val queries = mutableListOf<String>()

        override fun isAvailable(): Boolean = available

        override suspend fun search(query: String): List<T> {
            queries += query
            return onSearch(query)
        }
    }

    private fun echoService(available: Boolean = true) = FakeService(
        available = available,
        onSearch = { query -> listOf("$query-1", "$query-2") },
    )

    private fun <T> TestScope.autocomplete(
        service: AutocompleteService<T>,
        options: AutocompleteOptions = AutocompleteOptions(),
    ): Autocomplete<T> = Autocomplete(service, options, StandardTestDispatcher(testScheduler))

    @Test
    fun searchReturnsServiceResultsForTheUntouchedQuery() = runTest {
        val service = echoService()
        val query = "  Rue de l'Église, 75001 "

        val result = autocomplete(service).search(query)

        assertEquals(AutocompleteResult.Success(listOf("$query-1", "$query-2")), result)
        assertEquals(listOf(query), service.queries)
    }

    @Test
    fun searchReturnsEmptySuccessWhenServiceHasNoResults() = runTest {
        val service = FakeService<String>(onSearch = { emptyList() })

        val result = autocomplete(service).search("nowhere")

        assertEquals(AutocompleteResult.Success(emptyList<String>()), result)
    }

    @Test
    fun searchReturnsNotSupportedWithoutSearchingWhenServiceIsUnavailable() = runTest {
        val service = echoService(available = false)
        val autocomplete = autocomplete(service)

        // A query too short to search is included, being unavailable takes priority over it
        listOf("paris", "a").forEach { query ->
            assertSame(AutocompleteResult.NotSupported, autocomplete.search(query), "query '$query'")
        }
        assertEquals(emptyList(), service.queries)
    }

    @Test
    fun searchOnlyReachesTheServiceOnceTheQueryHasTheMinimumLength() = runTest {
        val cases = listOf(
            "" to false,
            "abcd" to false,
            "abcde" to true,
            "abcdef" to true,
        )

        cases.forEach { (query, searches) ->
            val service = echoService()
            val autocomplete = autocomplete(service, AutocompleteOptions(minimumQuery = 5))

            val result = autocomplete.search(query)

            val expected = if (searches) listOf("$query-1", "$query-2") else emptyList()
            assertEquals(AutocompleteResult.Success(expected), result, "query '$query'")
            assertEquals(if (searches) listOf(query) else emptyList(), service.queries, "query '$query'")
        }
    }

    @Test
    fun searchMapsServiceFailuresToFailedWithTheirMessage() = runTest {
        val cases = listOf(
            IllegalStateException("quota exceeded") to "quota exceeded",
            IllegalStateException() to "An unknown error occurred",
        )

        cases.forEach { (failure, message) ->
            val service = FakeService<String>(onSearch = { throw failure })

            val result = autocomplete(service).search("paris")

            assertEquals(AutocompleteResult.Failed(message), result, "search with $failure")
        }
    }

    @Test
    fun searchRethrowsCancellationInsteadOfReportingFailed() = runTest {
        val service = FakeService<String>(onSearch = { throw CancellationException("stop") })

        assertFailsWith<CancellationException> { autocomplete(service).search("paris") }
    }

    @Test
    fun placeAutocompleteUsesTheGivenServiceAndOptions() = runTest {
        val service = FakeService(onSearch = { listOf(place(name = "Paris")) })
        val options = AutocompleteOptions(minimumQuery = 1)

        val autocomplete: Autocomplete<Place> =
            PlaceAutocomplete(service, options, StandardTestDispatcher(testScheduler))

        assertSame(options, autocomplete.options)
        assertEquals(AutocompleteResult.Success(listOf(place(name = "Paris"))), autocomplete.search("a"))
    }
}
