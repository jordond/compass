package dev.jordond.compass.autocomplete

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AutocompleteResultTest {

    @Test
    fun successExposesItsDataAndOnlyRunsOnSuccess() {
        val success: AutocompleteResult<String> = AutocompleteResult.Success(listOf("a", "b"))
        var received: List<String>? = null
        var failed = false

        assertFalse(success.isError)
        assertEquals(listOf("a", "b"), success.getOrNull())
        assertNull(success.errorOrNull())
        assertSame(success, success.onSuccess { data -> received = data })
        assertSame(success, success.onFailed { failed = true })
        assertEquals(listOf("a", "b"), received)
        assertFalse(failed)
    }

    @Test
    fun errorsExposeThemselvesAndOnlyRunOnFailed() {
        val errors: List<AutocompleteResult.Error> = listOf(
            AutocompleteResult.Failed("boom"),
            AutocompleteResult.NotSupported,
        )

        errors.forEach { error ->
            val result: AutocompleteResult<String> = error
            var received: AutocompleteResult.Error? = null
            var succeeded = false

            assertTrue(result.isError, "$error")
            assertNull(result.getOrNull(), "$error")
            assertSame(error, result.errorOrNull(), "$error")
            assertSame(result, result.onFailed { cause -> received = cause })
            assertSame(result, result.onSuccess { succeeded = true })
            assertSame(error, received, "$error")
            assertFalse(succeeded, "$error")
        }
    }
}
