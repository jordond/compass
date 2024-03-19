package dev.jordond.compass.geocoder.web.internal

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GeocodeResponse(
    @SerialName("features")
    val features: List<FeatureResponse>,
)

internal fun GeocodeResponse.toLocations(): List<Location> {
    return features.map { response ->
        val coordinates = response.properties.coordinates
        Location(
            latitude = coordinates.latitude,
            longitude = coordinates.longitude,
        )
    }
}

internal fun GeocodeResponse.toPlaces(): List<Place> {
    TODO("Implement")
}