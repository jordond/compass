package dev.jordond.compass.geocoder

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame

class PlatformGeocoderTest {

    @Test
    fun notSupportedPlatformGeocoderIsUnavailableAndEmpty() = runTest {
        assertFalse(NotSupportedPlatformGeocoder.isAvailable())
        assertEquals(emptyList(), NotSupportedPlatformGeocoder.forward("x"))
        assertEquals(emptyList(), NotSupportedPlatformGeocoder.reverse(1.0, 2.0))
    }

    @Test
    fun geocoderWithNotSupportedPlatformReturnsNotSupported() = runTest {
        val geocoder = Geocoder(NotSupportedPlatformGeocoder)
        assertFalse(geocoder.isAvailable())
        assertSame(GeocoderResult.NotSupported, geocoder.forward("x"))
        assertIs<GeocoderResult.NotSupported>(geocoder.reverse(1.0, 2.0))
    }
}
