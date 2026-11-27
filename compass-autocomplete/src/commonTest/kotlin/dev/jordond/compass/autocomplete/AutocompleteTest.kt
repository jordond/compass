package dev.jordond.compass.autocomplete

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AutocompleteTest {

    private class FakeService(
        var available: Boolean = true,
        var onSearch: suspend (String) -> List<String> = { listOf("$it-1", "$it-2") },
    ) : AutocompleteService<String> {

        val queries = mutableListOf<String>()

        override fun isAvailable(): Boolean = available

        override suspend fun search(query: String): List<String> {
            queries += query
            return onSearch(query)
        }
    }

    private fun TestScope.autocomplete(
        service: AutocompleteService<String>,
        options: AutocompleteOptions = AutocompleteOptions(),
    ): Autocomplete<String> =
        Autocomplete(service, options, UnconfinedTestDispatcher(testScheduler))

    @Test
    fun searchReturnsServiceResults() = runTest {
        val service = FakeService()

        val result = autocomplete(service).search("paris")

        assertEquals(AutocompleteResult.Success(listOf("paris-1", "paris-2")), result)
        assertEquals(listOf("paris"), service.queries)
    }

    @Test
    fun searchReturnsEmptySuccessWhenServiceHasNoResults() = runTest {
        val service = FakeService(onSearch = { emptyList() })

        val result = autocomplete(service).search("nowhere")

        assertEquals(AutocompleteResult.Success(emptyList<String>()), result)
    }

    @Test
    fun searchReturnsNotSupportedWhenServiceIsUnavailable() = runTest {
        val service = FakeService(available = false)

        val result = autocomplete(service).search("paris")

        assertSame(AutocompleteResult.NotSupported, result)
        assertTrue(service.queries.isEmpty())
    }

    @Test
    fun searchChecksAvailabilityOnEveryCall() = runTest {
        val service = FakeService(available = false)
        val autocomplete = autocomplete(service)

        assertSame(AutocompleteResult.NotSupported, autocomplete.search("paris"))
        service.available = true
        assertIs<AutocompleteResult.Success<String>>(autocomplete.search("paris"))
    }

    @Test
    fun unavailableServiceTakesPriorityOverShortQuery() = runTest {
        val service = FakeService(available = false)

        assertSame(AutocompleteResult.NotSupported, autocomplete(service).search("a"))
    }

    @Test
    fun searchSkipsServiceForQueryShorterThanMinimum() = runTest {
        val service = FakeService()
        val autocomplete = autocomplete(service, AutocompleteOptions(minimumQuery = 5))

        val result = autocomplete.search("abc")

        assertEquals(AutocompleteResult.Success(emptyList<String>()), result)
        assertTrue(service.queries.isEmpty())
    }

    @Test
    fun searchSkipsServiceForEmptyQuery() = runTest {
        val service = FakeService()

        val result = autocomplete(service).search("")

        assertEquals(AutocompleteResult.Success(emptyList<String>()), result)
        assertTrue(service.queries.isEmpty())
    }

    @Test
    fun searchCallsServiceForQueryLongerThanMinimum() = runTest {
        val service = FakeService()
        val autocomplete = autocomplete(service, AutocompleteOptions(minimumQuery = 5))

        autocomplete.search("abcdef")

        assertEquals(listOf("abcdef"), service.queries)
    }

    @Test
    fun searchPassesQueryThroughUntouched() = runTest {
        val service = FakeService()

        autocomplete(service).search("  Rue de l'Église, 75001 ")

        assertEquals(listOf("  Rue de l'Église, 75001 "), service.queries)
    }

    @Test
    fun searchMapsExceptionToFailedWithMessage() = runTest {
        val service = FakeService(onSearch = { throw IllegalStateException("quota exceeded") })

        val result = autocomplete(service).search("paris")

        assertEquals(AutocompleteResult.Failed("quota exceeded"), result)
    }

    @Test
    fun searchMapsExceptionWithoutMessageToUnknownError() = runTest {
        val service = FakeService(onSearch = { throw IllegalStateException() })

        val result = autocomplete(service).search("paris")

        assertEquals(AutocompleteResult.Failed("An unknown error occurred"), result)
    }

    @Test
    fun searchMapsNonExceptionThrowableToFailed() = runTest {
        val service = FakeService(onSearch = { throw AssertionError("bad state") })

        val result = autocomplete(service).search("paris")

        assertEquals(AutocompleteResult.Failed("bad state"), result)
    }

    @Test
    fun searchRecoversAfterFailure() = runTest {
        var fail = true
        val service = FakeService(onSearch = {
            if (fail) throw IllegalStateException("down") else listOf("ok")
        })
        val autocomplete = autocomplete(service)

        assertIs<AutocompleteResult.Failed>(autocomplete.search("paris"))
        fail = false
        assertEquals(AutocompleteResult.Success(listOf("ok")), autocomplete.search("paris"))
    }

    @Test
    fun searchRethrowsCancellationFromService() = runTest {
        val service = FakeService(onSearch = { throw CancellationException("stop") })

        val error = assertFailsWith<CancellationException> {
            autocomplete(service).search("paris")
        }

        assertEquals("stop", error.message)
    }

    @Test
    fun cancellingSearchDoesNotProduceFailedResult() = runTest {
        val started = CompletableDeferred<Unit>()
        val service = FakeService(onSearch = {
            started.complete(Unit)
            awaitCancellation()
        })
        val autocomplete = autocomplete(service)
        var result: AutocompleteResult<String>? = null

        val job: Job = launch { result = autocomplete.search("paris") }
        started.await()
        job.cancel()
        job.join()

        assertTrue(job.isCancelled)
        assertNull(result)
    }

    @Test
    fun optionsAreExposed() = runTest {
        val options = AutocompleteOptions(minimumQuery = 9)

        assertSame(options, autocomplete(FakeService(), options).options)
    }

    @Test
    fun placeAutocompleteUsesServiceAndOptions() = runTest {
        val service = object : AutocompleteService<dev.jordond.compass.Place> {
            override fun isAvailable(): Boolean = true
            override suspend fun search(query: String) = emptyList<dev.jordond.compass.Place>()
        }
        val options = AutocompleteOptions(minimumQuery = 1)

        val autocomplete = PlaceAutocomplete(service, options, UnconfinedTestDispatcher(testScheduler))

        assertSame(options, autocomplete.options)
        assertEquals(
            AutocompleteResult.Success(emptyList<dev.jordond.compass.Place>()),
            autocomplete.search("ab"),
        )
    }
}
