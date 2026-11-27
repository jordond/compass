package dev.jordond.compass.test.fixtures

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place

/**
 * A Mapbox geocoding response and the values the library maps it to.
 */
public object MapboxFixtures {

    public val coordinates: Coordinates = Coordinates(38.897, -77.036)

    public val place: Place = Place(
        coordinates = coordinates,
        name = "123 Main Street",
        street = "Main Street",
        isoCountryCode = "US",
        country = "United States",
        postalCode = "20500",
        administrativeArea = "District of Columbia",
        subAdministrativeArea = "Ward 2",
        locality = "Georgetown",
        subLocality = "Foggy Bottom",
        thoroughfare = "Main Street",
        subThoroughfare = null,
    )

    public val geocodeJson: String = """
        {"type":"FeatureCollection","features":[{
          "type":"Feature",
          "id":"dXJuOm1ieGFkcjo",
          "geometry":{"type":"Point","coordinates":[-77.036,38.897]},
          "properties":{
            "mapbox_id":"dXJuOm1ieGFkcjo",
            "feature_type":"address",
            "name":"123 Main Street",
            "coordinates":{"longitude":-77.036,"latitude":38.897,"accuracy":"rooftop"},
            "context":{
              "address":{"name":"123 Main Street","address_number":"123","street_name":"Main Street"},
              "street":{"name":"Main Street"},
              "neighbourhood":{"name":"Foggy Bottom"},
              "postcode":{"name":"20500"},
              "locality":{"name":"Georgetown"},
              "place":{"name":"Washington"},
              "district":{"name":"Ward 2"},
              "region":{"name":"District of Columbia","region_code":"DC"},
              "country":{"name":"United States","country_code":"US"}
            }
          }
        }],"attribution":"NOTICE: (c) 2024 Mapbox"}
    """.trimIndent()
}
