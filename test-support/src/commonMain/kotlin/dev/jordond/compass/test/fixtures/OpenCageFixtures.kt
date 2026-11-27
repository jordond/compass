package dev.jordond.compass.test.fixtures

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place

/**
 * An OpenCage geocoding response and the values the library maps it to.
 */
public object OpenCageFixtures {

    public val coordinates: Coordinates = Coordinates(52.5200066, 13.404954)

    public val place: Place = Place(
        coordinates = coordinates,
        name = "Zur letzten Instanz",
        street = "Friedrichstrasse",
        isoCountryCode = "de",
        country = "Germany",
        postalCode = "10117",
        administrativeArea = "Berlin",
        subAdministrativeArea = null,
        locality = "Berlin",
        subLocality = "Mitte",
        thoroughfare = "Friedrichstrasse",
        subThoroughfare = null,
    )

    public val geocodeJson: String = """
        {
          "documentation":"https://opencagedata.com/api",
          "rate":{"limit":2500,"remaining":2499,"reset":1700000000},
          "results":[{
            "components":{
              "ISO_3166-1_alpha-2":"DE",
              "_category":"commerce",
              "_normalized_city":"Berlin",
              "_type":"restaurant",
              "city":"Berlin",
              "continent":"Europe",
              "country":"Germany",
              "country_code":"de",
              "postcode":"10117",
              "restaurant":"Zur letzten Instanz",
              "road":"Friedrichstrasse",
              "state":"Berlin",
              "suburb":"Mitte"
            },
            "confidence":9,
            "formatted":"Friedrichstrasse, 10117 Berlin, Germany",
            "geometry":{"lat":52.5200066,"lng":13.404954}
          }],
          "status":{"code":200,"message":"OK"},
          "total_results":1
        }
    """.trimIndent()
}
