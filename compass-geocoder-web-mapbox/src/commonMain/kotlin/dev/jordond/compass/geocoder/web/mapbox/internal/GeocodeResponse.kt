package dev.jordond.compass.geocoder.web.mapbox.internal

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GeocodeResponse(
    @SerialName("features")
    val features: List<FeatureResponse>,
)

internal fun GeocodeResponse.toCoordinates(): List<Coordinates> {
    return features.mapNotNull { response ->
        val coordinates = response.properties?.coordinates ?: return@mapNotNull null
        Coordinates(
            latitude = coordinates.latitude,
            longitude = coordinates.longitude,
        )
    }
}

internal fun GeocodeResponse.toPlaces(): List<Place> {
    return features.mapNotNull { response ->
        val data = response.properties?.context ?: return@mapNotNull null
        Place(
            name = response.properties.name,
            street = data.street?.name,
            isoCountryCode = data.country?.countryCode,
            country = data.country?.name,
            postalCode = data.postcode?.name,
            administrativeArea = data.region?.name,
            subAdministrativeArea = data.district?.name,
            locality = data.locality?.name ?: data.place?.name,
            subLocality = data.neighborhood?.name,
            thoroughfare = data.street?.name,
            subThoroughfare = null,
        )
    }
}