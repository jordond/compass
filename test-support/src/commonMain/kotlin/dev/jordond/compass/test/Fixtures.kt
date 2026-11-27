package dev.jordond.compass.test

import dev.jordond.compass.Altitude
import dev.jordond.compass.Azimuth
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.Speed

/**
 * A [Place] where every field is null unless given, so a test names only the fields it cares about.
 */
public fun place(
    coordinates: Coordinates = Coordinates(latitude = 0.0, longitude = 0.0),
    name: String? = null,
    street: String? = null,
    isoCountryCode: String? = null,
    country: String? = null,
    postalCode: String? = null,
    administrativeArea: String? = null,
    subAdministrativeArea: String? = null,
    locality: String? = null,
    subLocality: String? = null,
    thoroughfare: String? = null,
    subThoroughfare: String? = null,
): Place = Place(
    coordinates = coordinates,
    name = name,
    street = street,
    isoCountryCode = isoCountryCode,
    country = country,
    postalCode = postalCode,
    administrativeArea = administrativeArea,
    subAdministrativeArea = subAdministrativeArea,
    locality = locality,
    subLocality = subLocality,
    thoroughfare = thoroughfare,
    subThoroughfare = subThoroughfare,
)

/**
 * A [Location] with plain defaults, so a test names only the fields it cares about.
 */
public fun location(
    latitude: Double = 43.5,
    longitude: Double = -79.5,
    accuracy: Double = 5.0,
    azimuth: Azimuth? = null,
    speed: Speed? = null,
    mslAltitude: Altitude? = null,
    ellipsoidalAltitude: Altitude? = null,
    timestampMillis: Long = 0L,
): Location = Location(
    coordinates = Coordinates(latitude = latitude, longitude = longitude),
    accuracy = accuracy,
    azimuth = azimuth,
    speed = speed,
    mslAltitude = mslAltitude,
    ellipsoidalAltitude = ellipsoidalAltitude,
    timestampMillis = timestampMillis,
)
