package dev.jordond.compass.geocoder.mobile.internal

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.mobile.address
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddressMapperTest {

    @Test
    fun toPlaceMapsEveryAddressField() {
        val address = address(
            latitude = 43.65,
            longitude = -79.38,
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
        )

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
        val place = address(1.0, 2.0).toPlace()

        assertEquals(Coordinates(1.0, 2.0), place.coordinates)
        assertTrue(place.isEmpty)
    }

    @Test
    fun toCoordinatesMapsLatitudeAndLongitude() {
        val addresses = listOf(address(1.5, 2.5), address(-3.5, 4.5))

        assertEquals(
            listOf(Coordinates(1.5, 2.5), Coordinates(-3.5, 4.5)),
            addresses.toCoordinates(),
        )
        assertEquals(Coordinates(1.5, 2.5), addresses.first().toCoordinates())
    }
}
