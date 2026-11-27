package dev.jordond.compass

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class ModelsTest {

    private fun place(
        name: String? = null,
        street: String? = null,
        isoCountryCode: String? = null,
        country: String? = null,
        postalCode: String? = null,
        administrativeArea: String? = null,
        subAdministrativeArea: String? = null,
        locality: String? = null,
        subLocality: String? = null,
        thoroughfare: String? = null,
        subThoroughfare: String? = null,
    ) = Place(
        Coordinates(0.0, 0.0), name, street, isoCountryCode, country, postalCode,
        administrativeArea, subAdministrativeArea, locality, subLocality, thoroughfare,
        subThoroughfare,
    )

    @Test
    fun coordinatesHaveStructuralEquality() {
        assertEquals(Coordinates(1.0, 2.0), Coordinates(1.0, 2.0))
        assertEquals(Coordinates(1.0, 2.0).hashCode(), Coordinates(1.0, 2.0).hashCode())
        assertNotEquals(Coordinates(1.0, 2.0), Coordinates(2.0, 1.0))
    }

    @Test
    fun placeEqualityIncludesAllFields() {
        assertEquals(place(name = "a"), place(name = "a"))
        assertNotEquals(place(name = "a"), place(name = "b"))
        assertNotEquals(place(name = "a"), place(name = "a", subThoroughfare = "1"))
    }

    @Test
    fun firstValueFollowsPriorityOrder() {
        assertEquals("n", place(name = "n", street = "s", isoCountryCode = "i").firstValue)
        assertEquals("s", place(street = "s", thoroughfare = "t").firstValue)
        assertEquals("t", place(thoroughfare = "t", subThoroughfare = "st").firstValue)
        assertEquals("st", place(subThoroughfare = "st", subLocality = "sl").firstValue)
        assertEquals("sl", place(subLocality = "sl", locality = "l").firstValue)
        assertEquals("l", place(locality = "l", subAdministrativeArea = "sa").firstValue)
        assertEquals("sa", place(subAdministrativeArea = "sa", administrativeArea = "a").firstValue)
        assertEquals("a", place(administrativeArea = "a", postalCode = "p").firstValue)
        assertEquals("p", place(postalCode = "p", country = "c").firstValue)
        assertEquals("c", place(country = "c", isoCountryCode = "i").firstValue)
        assertEquals("i", place(isoCountryCode = "i").firstValue)
    }

    @Test
    fun firstValueThrowsWhenEmpty() {
        assertFailsWith<IllegalStateException> { place().firstValue }
    }

    @Test
    fun nullCopyClearsEveryFieldButKeepsCoordinates() {
        val full = place("n", "s", "iso", "c", "p", "a", "sa", "l", "sl", "t", "st")
        val copy = full.nullCopy()
        assertEquals(full.coordinates, copy.coordinates)
        assertEquals(true, copy.isEmpty)
    }

    @Test
    fun nullCopyKeepsOnlyProvidedFields() {
        val copy = place("n", "s").nullCopy(name = "only")
        assertEquals("only", copy.name)
        assertEquals(null, copy.street)
    }

    @Test
    fun locationEqualityIncludesNestedValues() {
        fun location(speed: Float) = Location(
            coordinates = Coordinates(1.0, 2.0),
            accuracy = 3.0,
            azimuth = Azimuth(90f, null),
            speed = Speed(speed, 0.5f),
            mslAltitude = Altitude(10.0, null),
            ellipsoidalAltitude = null,
            timestampMillis = 100L,
        )
        assertEquals(location(1f), location(1f))
        assertNotEquals(location(1f), location(2f))
    }

    @Test
    fun priorityHasAllExpectedValues() {
        assertEquals(
            listOf("Balanced", "HighAccuracy", "LowPower", "Passive"),
            Priority.entries.map { it.name },
        )
    }
}
