package dev.jordond.compass.geocoder

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class GeocoderResultTest {

    @Test
    fun successExposesItsDataAndOnlyRunsOnSuccess() {
        val success: GeocoderResult<Int> = GeocoderResult.Success(listOf(1, 2))
        var received: List<Int>? = null
        var failed = false

        assertFalse(success.isError)
        assertNull(success.errorOrNull())
        assertEquals(listOf(1, 2), success.getOrNull())
        assertEquals(1, success.getFirstOrNull())
        assertSame(success, success.onSuccess { data -> received = data })
        assertSame(success, success.onFailed { failed = true })
        assertEquals(listOf(1, 2), received)
        assertFalse(failed)
    }

    @Test
    fun getFirstOrNullIsNullForAnEmptySuccess() {
        assertNull(GeocoderResult.Success(emptyList<Int>()).getFirstOrNull())
    }

    @Test
    fun errorsExposeThemselvesAndOnlyRunOnFailed() {
        val errors: List<GeocoderResult.Error> = listOf(
            GeocoderResult.NotFound,
            GeocoderResult.NotSupported,
            GeocoderResult.InvalidCoordinates,
            GeocoderResult.GeocodeFailed("failed"),
        )

        errors.forEach { error ->
            val result: GeocoderResult<Int> = error
            var received: GeocoderResult.Error? = null
            var succeeded = false

            assertTrue(result.isError, "$error")
            assertSame(error, result.errorOrNull(), "$error")
            assertNull(result.getOrNull(), "$error")
            assertNull(result.getFirstOrNull(), "$error")
            assertSame(result, result.onFailed { cause -> received = cause })
            assertSame(result, result.onSuccess { succeeded = true })
            assertSame(error, received, "$error")
            assertFalse(succeeded, "$error")
        }
    }
}
