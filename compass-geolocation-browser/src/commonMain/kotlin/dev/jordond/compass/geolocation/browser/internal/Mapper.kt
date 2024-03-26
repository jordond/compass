package dev.jordond.compass.geolocation.browser.internal

import dev.jordond.compass.Altitude
import dev.jordond.compass.Azimuth
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Location
import dev.jordond.compass.Speed
import dev.jordond.compass.geolocation.browser.api.model.GeolocationPosition

internal fun GeolocationPosition.toModel(): Location = Location(
    coordinates = Coordinates(coords.latitude, coords.longitude),
    accuracy = coords.accuracy,
    altitude = coords.altitude?.let { Altitude(it, coords.altitudeAccuracy?.toFloat()) },
    azimuth = coords.heading?.let { Azimuth(it.toFloat(), null) },
    speed = coords.speed?.let { Speed(it.toFloat(), null) },
    timestampMillis = timestamp.toLong(),
)