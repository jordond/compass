package dev.jordond.compass.geocoder.web.google.internal

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.web.google.internal.AddressComponentType.AdministrativeAreaLevel1
import dev.jordond.compass.geocoder.web.google.internal.AddressComponentType.AdministrativeAreaLevel2
import dev.jordond.compass.geocoder.web.google.internal.AddressComponentType.AdministrativeAreaLevel3
import dev.jordond.compass.geocoder.web.google.internal.AddressComponentType.Country
import dev.jordond.compass.geocoder.web.google.internal.AddressComponentType.Locality
import dev.jordond.compass.geocoder.web.google.internal.AddressComponentType.Name
import dev.jordond.compass.geocoder.web.google.internal.AddressComponentType.Neighborhood
import dev.jordond.compass.geocoder.web.google.internal.AddressComponentType.PostalCode
import dev.jordond.compass.geocoder.web.google.internal.AddressComponentType.Thoroughfare
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal typealias ForwardGeocodeResponse = List<ResultResponse>

@Serializable
internal data class ReverseGeocodeResponse(
    @SerialName("results")
    val results: List<ResultResponse> = emptyList(),
)


internal fun ForwardGeocodeResponse.toLocations(): List<Location> {
    return mapNotNull { response ->
        val location = response.geometry?.location ?: return@mapNotNull null
        Location(location.lat, location.lng)
    }
}

internal fun ReverseGeocodeResponse.toPlaces(): List<Place> {
    return results.mapNotNull { response ->
        val components = response.addressComponents
        val country = components.find(Country)

        Place(
            name = components.find(Name)?.long,
            street = response.formattedAddress,
            isoCountryCode = country?.short,
            country = country?.long,
            postalCode = components.find(PostalCode)?.long,
            administrativeArea = components.find(AdministrativeAreaLevel1)?.long,
            subAdministrativeArea = components.find(AdministrativeAreaLevel2)?.long,
            locality = components.find(Locality)?.long
                ?: components.find(AdministrativeAreaLevel3)?.long,
            subLocality = components.find(Neighborhood)?.long,
            thoroughfare = components.find(Thoroughfare)?.long,
            subThoroughfare = null,
        ).takeIf { !it.isEmpty }
    }
}