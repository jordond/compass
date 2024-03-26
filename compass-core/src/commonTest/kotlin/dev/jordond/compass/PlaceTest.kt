package dev.jordond.compass

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlaceTest {

    @Test
    fun isEmptyReturnsTrueWhenAllPropertiesAreNull() {
        val place = Place(
            name = null,
            street = null,
            isoCountryCode = null,
            country = null,
            postalCode = null,
            administrativeArea = null,
            subAdministrativeArea = null,
            locality = null,
            subLocality = null,
            thoroughfare = null,
            subThoroughfare = null
        )
        assertTrue { place.isEmpty }
    }

    @Test
    fun isEmptyReturnsFalseWhenAnyPropertyIsNotNull() {
        val place = Place(
            name = "name",
            street = null,
            isoCountryCode = null,
            country = null,
            postalCode = null,
            administrativeArea = null,
            subAdministrativeArea = null,
            locality = null,
            subLocality = null,
            thoroughfare = null,
            subThoroughfare = null
        )
        assertFalse { place.isEmpty }

        place.nullCopy(street = "street").isEmpty.shouldBeFalse()
        place.nullCopy(isoCountryCode = "isoCountryCode").isEmpty.shouldBeFalse()
        place.nullCopy(country = "country").isEmpty.shouldBeFalse()
        place.nullCopy(postalCode = "postalCode").isEmpty.shouldBeFalse()
        place.nullCopy(administrativeArea = "administrativeArea").isEmpty.shouldBeFalse()
        place.nullCopy(subAdministrativeArea = "subAdministrativeArea").isEmpty.shouldBeFalse()
        place.nullCopy(locality = "locality").isEmpty.shouldBeFalse()
        place.nullCopy(subLocality = "subLocality").isEmpty.shouldBeFalse()
        place.nullCopy(thoroughfare = "thoroughfare").isEmpty.shouldBeFalse()
        place.nullCopy(subThoroughfare = "subThoroughfare").isEmpty.shouldBeFalse()
    }

    private fun Boolean.shouldBeFalse() {
        assertFalse { this }
    }
}