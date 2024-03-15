package dev.jordond.compass.internal

import android.location.Address
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
