package dev.jordond.compass.autocomplete.mobile

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteOptions
import dev.jordond.compass.autocomplete.AutocompleteResult
import dev.jordond.compass.geocoder.mobile.MobilePlatformGeocoder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
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

    private val place = Place(
        coordinates = Coordinates(48.8566, 2.3522),
        name = "Paris",
        street = null,
        isoCountryCode = "FR",
        country = "France",
        postalCode = null,
        administrativeArea = null,
        subAdministrativeArea = null,
        locality = "Paris",
        subLocality = null,
        thoroughfare = null,
        subThoroughfare = null,
    )

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
    fun searchReturnsEmptyListWhenGeocoderFindsNothing() = runTest {
        val service = MobileAutocompleteService(platformGeocoder = FakeMobilePlatformGeocoder())

        assertEquals(emptyList(), service.search("zzzzzz"))
    }

    @Test
    fun searchPropagatesGeocoderFailure() = runTest {
        val geocoder = FakeMobilePlatformGeocoder(failure = IllegalStateException("no network"))
        val service = MobileAutocompleteService(platformGeocoder = geocoder)

        val error = assertFailsWith<IllegalStateException> { service.search("Paris") }

        assertEquals("no network", error.message)
    }

    @Test
    fun autocompleteReturnsPlaces() = runTest {
        val geocoder = FakeMobilePlatformGeocoder(places = listOf(place))
        val autocomplete = Autocomplete(
            service = MobileAutocompleteService(platformGeocoder = geocoder),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(AutocompleteResult.Success(listOf(place)), autocomplete.search("Paris"))
    }

    @Test
    fun autocompleteIsNotSupportedWhenGeocoderUnavailable() = runTest {
        val geocoder = FakeMobilePlatformGeocoder(available = false, places = listOf(place))
        val autocomplete = Autocomplete(
            service = MobileAutocompleteService(platformGeocoder = geocoder),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertSame(AutocompleteResult.NotSupported, autocomplete.search("Paris"))
        assertTrue(geocoder.addresses.isEmpty())
    }

    @Test
    fun autocompleteMapsGeocoderFailureToFailed() = runTest {
        val geocoder = FakeMobilePlatformGeocoder(failure = IllegalStateException("denied"))
        val autocomplete = Autocomplete(
            service = MobileAutocompleteService(platformGeocoder = geocoder),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(AutocompleteResult.Failed("denied"), autocomplete.search("Paris"))
    }

    @Test
    fun autocompleteHonoursMinimumQuery() = runTest {
        val geocoder = FakeMobilePlatformGeocoder(places = listOf(place))
        val autocomplete = Autocomplete(
            service = MobileAutocompleteService(platformGeocoder = geocoder),
            options = AutocompleteOptions(minimumQuery = 6),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(AutocompleteResult.Success(emptyList<Place>()), autocomplete.search("Par"))
        assertTrue(geocoder.addresses.isEmpty())
    }
}
