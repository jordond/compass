package dev.jordond.compass

import dev.jordond.compass.test.place
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlaceTest {

    @Test
    fun isEmptyWhenEveryFieldIsNull() {
        assertTrue(place().isEmpty)
    }

    @Test
    fun isNotEmptyWhenAnySingleFieldIsSet() {
        val places = mapOf(
            "name" to place(name = "x"),
            "street" to place(street = "x"),
            "isoCountryCode" to place(isoCountryCode = "x"),
            "country" to place(country = "x"),
            "postalCode" to place(postalCode = "x"),
            "administrativeArea" to place(administrativeArea = "x"),
            "subAdministrativeArea" to place(subAdministrativeArea = "x"),
            "locality" to place(locality = "x"),
            "subLocality" to place(subLocality = "x"),
            "thoroughfare" to place(thoroughfare = "x"),
            "subThoroughfare" to place(subThoroughfare = "x"),
        )

        places.forEach { (field, place) ->
            assertFalse(place.isEmpty, "a place with only $field set")
        }
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
}
