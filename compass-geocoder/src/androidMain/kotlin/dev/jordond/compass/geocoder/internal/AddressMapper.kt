package dev.jordond.compass.geocoder.internal

import android.location.Address
import dev.jordond.compass.geocoder.Place

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
