package dev.jordond.compass.geocoder.internal

import android.location.Address
import dev.jordond.compass.Location
import dev.jordond.compass.Place

internal fun Address.toPlace(): Place = Place(
    name = featureName,
    street = getAddressLine(0),
    isoCountryCode = countryCode,
    country = countryName,
    postalCode = postalCode,
    administrativeArea = adminArea,
    subAdministrativeArea = subAdminArea,
    locality = locality,
    subLocality = subLocality,
    thoroughfare = thoroughfare,
    subThoroughfare = subThoroughfare
)

internal fun List<Address>.toPlaces(): List<Place> = map { it.toPlace() }

internal fun Address.toLocation(): Location = Location(latitude = latitude, longitude = longitude)

internal fun List<Address>.toLocations(): List<Location> = map { it.toLocation() }