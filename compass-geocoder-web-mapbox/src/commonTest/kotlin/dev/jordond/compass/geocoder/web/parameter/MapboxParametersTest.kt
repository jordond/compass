package dev.jordond.compass.geocoder.web.parameter

import dev.jordond.compass.geocoder.Geocoder
import kotlin.test.Test
import kotlin.test.assertEquals

class MapboxParametersTest {

    @Test
    fun defaultParametersSendPermanentAndAutocompleteOnly() {
        val params = MapboxParameters()

        assertEquals(
            mapOf("permanent" to "false", "autocomplete" to "true"),
            params.parameters,
        )
    }

    @Test
    fun allParametersAreSerialized() {
        val params = MapboxParameters(
            permanent = true,
            autocomplete = false,
            limit = 3,
            bbox = MapboxBoundingBox(-79.6, 43.5, -79.1, 43.9),
            country = "ca,us",
            language = "fr",
            proximity = "-79.38,43.65",
            types = MapboxTypesList(MapboxTypes.Place, MapboxTypes.Address, MapboxTypes.Postcode),
            worldView = MapboxWorldView.JP,
        )

        assertEquals(
            mapOf(
                "permanent" to "true",
                "autocomplete" to "false",
                "limit" to "3",
                "bbox" to "-79.6,43.5,-79.1,43.9",
                "country" to "ca,us",
                "language" to "fr",
                "proximity" to "-79.38,43.65",
                "types" to "place,address,postcode",
                "worldview" to "jp",
            ),
            params.parameters,
        )
    }

    @Test
    fun typesAreLowercaseNames() {
        val expected = mapOf(
            MapboxTypes.Country to "country",
            MapboxTypes.Region to "region",
            MapboxTypes.Postcode to "postcode",
            MapboxTypes.District to "district",
            MapboxTypes.Place to "place",
            MapboxTypes.Locality to "locality",
            MapboxTypes.Neighborhood to "neighborhood",
            MapboxTypes.Street to "street",
            MapboxTypes.Address to "address",
        )

        expected.forEach { (type, value) -> assertEquals(value, type.value) }
        assertEquals(expected.size, MapboxTypes.entries.size)
    }

    @Test
    fun worldViewsAreLowercaseCodes() {
        assertEquals(
            listOf("ar", "cn", "in", "jp", "ma", "ru", "tr", "us"),
            MapboxWorldView.entries.map { it.value },
        )
    }

    @Test
    fun typesListUsesCommaSeparator() {
        val list = MapboxTypesList(listOf(MapboxTypes.Street, MapboxTypes.Region))

        assertEquals("street,region", list.value)
    }

    @Test
    fun boundingBoxValueIsMinLonMinLatMaxLonMaxLat() {
        val box = MapboxBoundingBox(1.5, 2.5, 3.5, 4.5)

        assertEquals("1.5,2.5,3.5,4.5", box.value)
    }

    @Test
    fun boundingBoxCopyOverridesOnlyGivenValues() {
        val box = MapboxBoundingBox(1.5, 2.5, 3.5, 4.5).copy(maxLatitude = 9.5)

        assertEquals(MapboxBoundingBox(1.5, 2.5, 3.5, 9.5), box)
    }

    @Test
    fun builderDefaultsToDefaultMaxResultsLimit() {
        val params = mapboxParameters { }

        assertEquals(Geocoder.DefaultMaxResults, params.limit)
        assertEquals(
            mapOf("permanent" to "false", "autocomplete" to "true", "limit" to "5"),
            params.parameters,
        )
    }

    @Test
    fun builderPassesEveryValueThrough() {
        val params = mapboxParameters {
            limit = 2
            permanent = true
            autocomplete = false
            boundingBox(1.5, 2.5, 3.5, 4.5)
            country = "ca"
            language = "en"
            proximity = "ip"
            types = MapboxTypesList(MapboxTypes.Country)
            worldView = MapboxWorldView.US
        }

        assertEquals(
            mapOf(
                "permanent" to "true",
                "autocomplete" to "false",
                "limit" to "2",
                "bbox" to "1.5,2.5,3.5,4.5",
                "country" to "ca",
                "language" to "en",
                "proximity" to "ip",
                "types" to "country",
                "worldview" to "us",
            ),
            params.parameters,
        )
    }
}
