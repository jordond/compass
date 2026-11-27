package dev.jordond.compass.geocoder.mobile.internal

import android.location.Address
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddressMapperTest {

    @Test
    fun toPlaceMapsEveryAddressField() {
        val address = mockk<Address> {
            every { latitude } returns 43.65
            every { longitude } returns -79.38
            every { featureName } returns "CN Tower"
            every { getAddressLine(0) } returns "290 Bremner Blvd"
            every { countryCode } returns "CA"
            every { countryName } returns "Canada"
            every { postalCode } returns "M5V 3L9"
            every { adminArea } returns "Ontario"
            every { subAdminArea } returns "Toronto Division"
            every { locality } returns "Toronto"
            every { subLocality } returns "Entertainment District"
            every { thoroughfare } returns "Bremner Blvd"
            every { subThoroughfare } returns "290"
        }

        assertEquals(
            Place(
                coordinates = Coordinates(43.65, -79.38),
                name = "CN Tower",
                street = "290 Bremner Blvd",
                isoCountryCode = "CA",
                country = "Canada",
                postalCode = "M5V 3L9",
                administrativeArea = "Ontario",
                subAdministrativeArea = "Toronto Division",
                locality = "Toronto",
                subLocality = "Entertainment District",
                thoroughfare = "Bremner Blvd",
                subThoroughfare = "290",
            ),
            address.toPlace(),
        )
    }

    @Test
    fun toPlaceKeepsMissingFieldsNull() {
        val address = mockk<Address> {
            every { latitude } returns 1.0
            every { longitude } returns 2.0
            every { featureName } returns null
            every { getAddressLine(0) } returns null
            every { countryCode } returns null
            every { countryName } returns null
            every { postalCode } returns null
            every { adminArea } returns null
            every { subAdminArea } returns null
            every { locality } returns null
            every { subLocality } returns null
            every { thoroughfare } returns null
            every { subThoroughfare } returns null
        }

        val place = address.toPlace()

        assertEquals(Coordinates(1.0, 2.0), place.coordinates)
        assertTrue(place.isEmpty)
    }

    @Test
    fun toPlacesPreservesOrder() {
        val addresses = listOf(10.0, 20.0, 30.0).map { lat -> emptyAddress(lat, 0.0) }

        assertEquals(listOf(10.0, 20.0, 30.0), addresses.toPlaces().map { it.coordinates.latitude })
    }

    @Test
    fun toCoordinatesMapsLatitudeAndLongitude() {
        val addresses = listOf(emptyAddress(1.5, 2.5), emptyAddress(-3.5, 4.5))

        assertEquals(
            listOf(Coordinates(1.5, 2.5), Coordinates(-3.5, 4.5)),
            addresses.toCoordinates(),
        )
        assertEquals(Coordinates(1.5, 2.5), addresses.first().toCoordinates())
    }

    @Test
    fun emptyListsMapToEmptyLists() {
        assertEquals(emptyList(), emptyList<Address>().toPlaces())
        assertEquals(emptyList(), emptyList<Address>().toCoordinates())
    }

    private fun emptyAddress(lat: Double, lon: Double): Address = mockk(relaxed = true) {
        every { latitude } returns lat
        every { longitude } returns lon
    }
}
