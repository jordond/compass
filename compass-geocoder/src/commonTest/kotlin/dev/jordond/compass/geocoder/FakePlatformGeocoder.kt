package dev.jordond.compass.geocoder

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place

internal fun place(name: String? = "Place", coordinates: Coordinates = Coordinates(1.0, 2.0)) = Place(
    coordinates = coordinates,
    name = name,
    street = null,
    isoCountryCode = null,
    country = null,
    postalCode = null,
    administrativeArea = null,
    subAdministrativeArea = null,
    locality = null,
    subLocality = null,
    thoroughfare = null,
    subThoroughfare = null,
)

internal class FakePlatformGeocoder(
    var available: Boolean = true,
    var onForward: suspend (String) -> List<Coordinates> = { emptyList() },
    var onReverse: suspend (Double, Double) -> List<Place> = { _, _ -> emptyList() },
) : PlatformGeocoder {

    var forwardCalls = 0
    var reverseCalls = 0
    var lastAddress: String? = null
    var lastReverse: Pair<Double, Double>? = null

    override fun isAvailable(): Boolean = available

    override suspend fun forward(address: String): List<Coordinates> {
        forwardCalls++
        lastAddress = address
        return onForward(address)
    }

    override suspend fun reverse(latitude: Double, longitude: Double): List<Place> {
        reverseCalls++
        lastReverse = latitude to longitude
        return onReverse(latitude, longitude)
    }
}
