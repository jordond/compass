package dev.jordond.compass.geocoder

import dev.jordond.compass.Coordinates
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExtensionsTest {

    private val places = listOf(place("first"), place("second"))
    private val coordinates = listOf(Coordinates(1.0, 2.0), Coordinates(3.0, 4.0))

    private fun geocoder(available: Boolean = true, empty: Boolean = false) = Geocoder(
        FakePlatformGeocoder(
            available = available,
            onForward = { if (empty) emptyList() else coordinates },
            onReverse = { _, _ -> if (empty) emptyList() else places },
        ),
    )

    @Test
    fun placesOrNullReturnsAllPlaces() = runTest {
        assertEquals(places, geocoder().placesOrNull(Coordinates(1.0, 2.0)))
        assertEquals(places, geocoder().placesOrNull(1.0, 2.0))
    }

    @Test
    fun placeOrNullReturnsFirstPlace() = runTest {
        assertEquals(places.first(), geocoder().placeOrNull(Coordinates(1.0, 2.0)))
        assertEquals(places.first(), geocoder().placeOrNull(1.0, 2.0))
    }

    @Test
    fun coordinatesOrNullReturnsFirst() = runTest {
        assertEquals(coordinates.first(), geocoder().coordinatesOrNull("x"))
    }

    @Test
    fun coordinatesListOrNullReturnsAll() = runTest {
        assertEquals(coordinates, geocoder().coordinatesListOrNull("x"))
    }

    @Test
    fun extensionsReturnNullOnEmptyResults() = runTest {
        val geocoder = geocoder(empty = true)
        assertNull(geocoder.placesOrNull(1.0, 2.0))
        assertNull(geocoder.placeOrNull(Coordinates(1.0, 2.0)))
        assertNull(geocoder.coordinatesOrNull("x"))
        assertNull(geocoder.coordinatesListOrNull("x"))
    }

    @Test
    fun extensionsReturnNullWhenUnavailable() = runTest {
        val geocoder = geocoder(available = false)
        assertNull(geocoder.placesOrNull(1.0, 2.0))
        assertNull(geocoder.coordinatesOrNull("x"))
    }
}
