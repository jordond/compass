package dev.jordond.compass.internal

import android.location.Address
import dev.jordond.compass.Location

internal fun Address.toLocation(): Location = Location(
    latitude = latitude,
    longitude = longitude,
    address = getAddressLine(0),
    city = locality,
    state = adminArea,
    postalCode = postalCode,
    country = countryName,
)
