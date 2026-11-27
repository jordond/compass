package dev.jordond.compass.geocoder

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class GeocoderResultTest {

    private val errors: List<GeocoderResult.Error> = listOf(
        GeocoderResult.NotFound,
        GeocoderResult.NotSupported,
        GeocoderResult.InvalidCoordinates,
        GeocoderResult.GeocodeFailed("failed"),
    )

    @Test
    fun successIsNotError() {
        val result = GeocoderResult.Success(listOf(1))
        assertFalse(result.isError)
        assertNull(result.errorOrNull())
    }

    @Test
    fun errorsAreErrors() {
        errors.forEach { error ->
            assertTrue(error.isError)
            assertSame(error, error.errorOrNull())
            assertNull(error.getOrNull())
            assertNull(error.getFirstOrNull())
        }
    }

    @Test
    fun getOrNullReturnsData() {
        assertEquals(listOf(1, 2), GeocoderResult.Success(listOf(1, 2)).getOrNull())
    }

    @Test
    fun getFirstOrNullReturnsFirstElement() {
        assertEquals(1, GeocoderResult.Success(listOf(1, 2)).getFirstOrNull())
    }

    @Test
    fun getFirstOrNullOnEmptySuccessIsNull() {
        assertNull(GeocoderResult.Success(emptyList<Int>()).getFirstOrNull())
    }

    @Test
    fun onSuccessRunsOnlyForSuccessAndReturnsSelf() {
        var received: List<Int>? = null
        val success = GeocoderResult.Success(listOf(7))
        assertSame(success, success.onSuccess { received = it })
        assertEquals(listOf(7), received)

        var called = false
        val error: GeocoderResult<Int> = GeocoderResult.NotFound
        assertSame(error, error.onSuccess { called = true })
        assertFalse(called)
    }

    @Test
    fun onFailedRunsOnlyForErrorAndReturnsSelf() {
        var received: GeocoderResult.Error? = null
        val error: GeocoderResult<Int> = GeocoderResult.NotSupported
        assertSame(error, error.onFailed { received = it })
        assertSame(GeocoderResult.NotSupported, received)

        var called = false
        val success: GeocoderResult<Int> = GeocoderResult.Success(listOf(1))
        assertSame(success, success.onFailed { called = true })
        assertFalse(called)
    }

    @Test
    fun successAndGeocodeFailedHaveStructuralEquality() {
        assertEquals(GeocoderResult.Success(listOf(1)), GeocoderResult.Success(listOf(1)))
        assertEquals(GeocoderResult.GeocodeFailed("a"), GeocoderResult.GeocodeFailed("a"))
        assertTrue(GeocoderResult.GeocodeFailed("a") != GeocoderResult.GeocodeFailed("b"))
    }
}
