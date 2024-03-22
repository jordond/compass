package dev.jordond.compass.geolocation.mobile

import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.exception.PermissionMissingException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Create a new [Geolocator] instance for geolocation operations.
 *
 * @param handlePermissions Whether to handle permissions requesting automatically. If this is
 * `false` and the required permissions are not granted, a [PermissionMissingException] will be
 * thrown when attempting to perform geolocation operations.
 * @param dispatcher The [CoroutineDispatcher] to use for geolocation operations.
 * @return A new [Geolocator] instance.
 */
public fun Geolocator(
    handlePermissions: Boolean = true,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = MobileGeolocator(handlePermissions, dispatcher)

/**
 * Create a new [Geolocator] instance for geolocation operations.
 *
 * @param handlePermissions Whether to handle permissions requesting automatically. If this is
 * `false` and the required permissions are not granted, a [PermissionMissingException] will be
 * thrown when attempting to perform geolocation operations.
 * @param dispatcher The [CoroutineDispatcher] to use for geolocation operations.
 * @return A new [Geolocator] instance.
 */
@Suppress("FunctionName")
public fun MobileGeolocator(
    handlePermissions: Boolean = true,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = Geolocator(MobileLocator(handlePermissions), dispatcher)