package dev.jordond.compass.test.fixtures

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place

/**
 * A Google Maps geocoding response and the values the library maps it to.
 */
public object GoogleMapsFixtures {

    public val coordinates: Coordinates = Coordinates(37.4224764, -122.0842499)

    public val place: Place = Place(
        coordinates = coordinates,
        name = "Amphitheatre Parkway",
        street = "1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA",
        isoCountryCode = "US",
        country = "United States",
        postalCode = "94043",
        administrativeArea = "California",
        subAdministrativeArea = "Santa Clara County",
        locality = "Mountain View",
        subLocality = null,
        thoroughfare = "Amphitheatre Parkway",
        subThoroughfare = null,
    )

    public val geocodeJson: String = """
        {"results":[{
          "address_components":[
            {"long_name":"1600","short_name":"1600","types":["street_number"]},
            {"long_name":"Amphitheatre Parkway","short_name":"Amphitheatre Pkwy","types":["route"]},
            {"long_name":"Mountain View","short_name":"Mountain View","types":["locality","political"]},
            {"long_name":"Santa Clara County","short_name":"Santa Clara County","types":["administrative_area_level_2","political"]},
            {"long_name":"California","short_name":"CA","types":["administrative_area_level_1","political"]},
            {"long_name":"United States","short_name":"US","types":["country","political"]},
            {"long_name":"94043","short_name":"94043","types":["postal_code"]}
          ],
          "formatted_address":"1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA",
          "geometry":{
            "location":{"lat":37.4224764,"lng":-122.0842499},
            "location_type":"ROOFTOP",
            "viewport":{"northeast":{"lat":37.4238,"lng":-122.0829},"southwest":{"lat":37.4211,"lng":-122.0856}}
          },
          "place_id":"ChIJ2eUgeAK6j4ARbn5u_wAGqWA",
          "types":["street_address"]
        }],"status":"OK"}
    """.trimIndent()
}
