package dev.jordond.compass.geolocation

import dev.jordond.compass.Location

public suspend fun Geolocator.lastLocationOrNull(): Location? = last().getOrNull()

public suspend fun Geolocator.currentLocationOrNull(): Location? = current().getOrNull()

public suspend fun Geolocator.extendedLocationOrNull(): ExtendedLocation? = extended().getOrNull()