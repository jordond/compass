package dev.jordond.compass.geocoder.web.opencage.internal

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GeocodeResponse(
    @SerialName("results")
    val results: List<ResultResponse>,
)

internal fun GeocodeResponse.toCoordinates(): List<Coordinates> {
    return results.map { result -> result.toCoordinates() }
}

internal fun GeocodeResponse.toPlaces(): List<Place> = results.mapNotNull { result ->
    val components = result.components
    Place(
        coordinates = result.toCoordinates(),
        name = components.restaurant?.takeIf { it.isNotBlank() },
        street = components.road?.takeIf { it.isNotBlank() },
        isoCountryCode = components.countryCode?.takeIf { it.isNotBlank() },
        country = components.country?.takeIf { it.isNotBlank() },
        postalCode = components.postcode?.takeIf { it.isNotBlank() },
        administrativeArea = components.state?.takeIf { it.isNotBlank() },
        subAdministrativeArea = null,
        locality = components.normalizedCity?.takeIf { it.isNotBlank() },
        subLocality = components.suburb?.takeIf { it.isNotBlank() },
        thoroughfare = components.road?.takeIf { it.isNotBlank() },
        subThoroughfare = null,
    ).takeIf { it.isEmpty.not() }
}

private fun ResultResponse.toCoordinates(): Coordinates {
    return Coordinates(
        latitude = geometry.lat,
        longitude = geometry.lng,
    )
}