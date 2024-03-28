package dev.jordond.compass.geocoder.mobile.internal

import android.location.Address
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place

internal fun Address.toPlace(): Place = Place(
    coordinates = Coordinates(latitude, longitude),
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
    subThoroughfare = subThoroughfare,
)

internal fun List<Address>.toPlaces(): List<Place> = map { it.toPlace() }

internal fun Address.toCoordinates(): Coordinates =
    Coordinates(latitude = latitude, longitude = longitude)

internal fun List<Address>.toCoordinates(): List<Coordinates> = map { it.toCoordinates() }