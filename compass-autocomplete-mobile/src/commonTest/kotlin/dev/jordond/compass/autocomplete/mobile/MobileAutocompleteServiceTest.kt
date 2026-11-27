package dev.jordond.compass.autocomplete.mobile

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.mobile.MobilePlatformGeocoder
import dev.jordond.compass.test.place
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MobileAutocompleteServiceTest {

    private class FakeMobilePlatformGeocoder(
        var available: Boolean = true,
        var places: List<Place> = emptyList(),
        var failure: Throwable? = null,
    ) : MobilePlatformGeocoder {

        val addresses = mutableListOf<String>()

        override fun isAvailable(): Boolean = available

        override suspend fun placeFromAddress(address: String): List<Place> {
            addresses += address
            failure?.let { throw it }
            return places
        }

        override suspend fun forward(address: String): List<Coordinates> =
            error("forward must not be used by autocomplete")

        override suspend fun reverse(latitude: Double, longitude: Double): List<Place> =
            error("reverse must not be used by autocomplete")
    }

    private val place = place(name = "Paris", country = "France")

    @Test
    fun isAvailableFollowsPlatformGeocoder() {
        val geocoder = FakeMobilePlatformGeocoder(available = true)
        val service = MobileAutocompleteService(platformGeocoder = geocoder)

        assertTrue(service.isAvailable())
        geocoder.available = false
        assertFalse(service.isAvailable())
    }

    @Test
    fun searchDelegatesQueryToPlaceFromAddress() = runTest {
        val geocoder = FakeMobilePlatformGeocoder(places = listOf(place))
        val service = MobileAutocompleteService(platformGeocoder = geocoder)

        val result = service.search("Paris")

        assertEquals(listOf(place), result)
        assertEquals(listOf("Paris"), geocoder.addresses)
    }

    @Test
    fun searchPropagatesGeocoderFailure() = runTest {
        val geocoder = FakeMobilePlatformGeocoder(failure = IllegalStateException("no network"))
        val service = MobileAutocompleteService(platformGeocoder = geocoder)

        val error = assertFailsWith<IllegalStateException> { service.search("Paris") }

        assertEquals("no network", error.message)
    }
}
