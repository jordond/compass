package dev.jordond.compass.autocomplete

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AutocompleteResultTest {

    private val success: AutocompleteResult<String> = AutocompleteResult.Success(listOf("a", "b"))
    private val failed: AutocompleteResult<String> = AutocompleteResult.Failed("boom")

    @Test
    fun successIsNotAnError() {
        assertFalse(success.isError)
        assertEquals(listOf("a", "b"), success.getOrNull())
        assertNull(success.errorOrNull())
    }

    @Test
    fun failedIsAnErrorWithMessage() {
        assertTrue(failed.isError)
        assertNull(failed.getOrNull())
        assertEquals("boom", failed.errorOrNull()?.message)
    }

    @Test
    fun notSupportedIsAnErrorWithMessage() {
        val result: AutocompleteResult<String> = AutocompleteResult.NotSupported

        assertTrue(result.isError)
        assertNull(result.getOrNull())
        assertEquals("Device does not support autocomplete.", result.errorOrNull()?.message)
    }

    @Test
    fun onSuccessOnlyRunsForSuccess() {
        val received = mutableListOf<List<String>>()

        assertSame(success, success.onSuccess { received += it })
        assertSame(failed, failed.onSuccess { received += it })

        assertEquals(listOf(listOf("a", "b")), received)
    }

    @Test
    fun onFailedOnlyRunsForErrors() {
        val received = mutableListOf<String>()

        assertSame(success, success.onFailed { received += it.message })
        assertSame(failed, failed.onFailed { received += it.message })
        AutocompleteResult.NotSupported.onFailed { received += it.message }

        assertEquals(listOf("boom", "Device does not support autocomplete."), received)
    }

    @Test
    fun successAndFailedHaveValueEquality() {
        assertEquals(AutocompleteResult.Success(listOf(1)), AutocompleteResult.Success(listOf(1)))
        assertEquals(AutocompleteResult.Failed("x"), AutocompleteResult.Failed("x"))
        assertFalse(AutocompleteResult.Failed("x") == AutocompleteResult.Failed("y"))
    }
}
