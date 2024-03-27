package dev.jordond.compass.geolocation

import dev.jordond.compass.Location
import dev.jordond.compass.geolocation.internal.DefaultGeolocator

/**
 * Gets the current location or null if the location is not available.
 *
 * @receiver The [Geolocator] to get the location from.
 * @return The current location or null if the location is not available.
 */
public suspend fun Geolocator.currentLocationOrNull(): Location? = current().getOrNull()

/**
 * Check if the current [Geolocator] has the permissions required for geolocation.
 *
 * This will always return `true` if the [Locator] used by the [Geolocator] is not a [PermissionLocator].
 *
 * @receiver The [Geolocator] to check the permissions of.
 * @return True if the [Geolocator] has the permissions required for geolocation, false otherwise.
 */
public fun Geolocator.hasPermission(): Boolean {
    val locator = (this as? DefaultGeolocator)?.locator
    return (locator as? PermissionLocator)?.hasPermission() ?: true
}

/**
 * Check if the current [GeolocatorResult] is a [GeolocatorResult.PermissionDenied].
 *
 * @receiver The [GeolocatorResult] to check.
 * @return True if the [GeolocatorResult] is a [GeolocatorResult.PermissionDenied], false otherwise.
 */
public fun GeolocatorResult.Error.isPermissionDenied(): Boolean {
    return this is GeolocatorResult.PermissionDenied
}

/**
 * Check if the current [GeolocatorResult] is a [GeolocatorResult.PermissionDenied].
 *
 * @receiver The [GeolocatorResult] to check.
 * @return True if the [GeolocatorResult] is a [GeolocatorResult.PermissionDenied], false otherwise.
 */
public fun GeolocatorResult.Error.isPermissionDeniedForever(): Boolean {
    return (this as? GeolocatorResult.PermissionDenied)?.forever ?: false
}