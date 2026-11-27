package dev.jordond.compass.geocoder

import dev.jordond.compass.Coordinates
import dev.jordond.compass.test.place
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExtensionsTest {

    private val places = listOf(place(name = "first"), place(name = "second"))
    private val coordinates = listOf(Coordinates(1.0, 2.0), Coordinates(3.0, 4.0))

    private val extensions: Map<String, suspend Geocoder.() -> Any?> = mapOf(
        "placesOrNull(Coordinates)" to { placesOrNull(Coordinates(1.0, 2.0)) },
        "placesOrNull(lat, lng)" to { placesOrNull(1.0, 2.0) },
        "placeOrNull(Coordinates)" to { placeOrNull(Coordinates(1.0, 2.0)) },
        "placeOrNull(lat, lng)" to { placeOrNull(1.0, 2.0) },
        "coordinatesListOrNull" to { coordinatesListOrNull("x") },
        "coordinatesOrNull" to { coordinatesOrNull("x") },
    )

    private fun TestScope.geocoder(available: Boolean = true, empty: Boolean = false) = geocoder(
        FakePlatformGeocoder(
            available = available,
            onForward = { if (empty) emptyList() else coordinates },
            onReverse = { _, _ -> if (empty) emptyList() else places },
        ),
    )

    @Test
    fun listExtensionsReturnEveryResult() = runTest {
        assertEquals(places, geocoder().placesOrNull(Coordinates(1.0, 2.0)))
        assertEquals(places, geocoder().placesOrNull(1.0, 2.0))
        assertEquals(coordinates, geocoder().coordinatesListOrNull("x"))
    }

    @Test
    fun singleExtensionsReturnTheFirstResult() = runTest {
        assertEquals(places.first(), geocoder().placeOrNull(Coordinates(1.0, 2.0)))
        assertEquals(places.first(), geocoder().placeOrNull(1.0, 2.0))
        assertEquals(coordinates.first(), geocoder().coordinatesOrNull("x"))
    }

    @Test
    fun everyExtensionReturnsNullWhenNothingIsFound() = runTest {
        val geocoder = geocoder(empty = true)

        extensions.forEach { (name, extension) ->
            assertNull(geocoder.extension(), name)
        }
    }

    @Test
    fun everyExtensionReturnsNullWhenUnavailable() = runTest {
        val geocoder = geocoder(available = false)

        extensions.forEach { (name, extension) ->
            assertNull(geocoder.extension(), name)
        }
    }
}
