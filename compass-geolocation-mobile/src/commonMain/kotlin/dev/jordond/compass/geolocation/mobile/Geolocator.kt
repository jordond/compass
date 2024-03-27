package dev.jordond.compass.geolocation.mobile

import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.mobile.mobile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Create a new [Geolocator] instance for geolocation operations.
 *
 * Make sure you read the [Android documentation](https://developer.android.com/develop/sensors-and-location/location)
 * as well as the [iOS documentation](https://developer.apple.com/documentation/corelocation)
 * to understand the permissions and accuracy.
 *
 * @param permissionController The [LocationPermissionController] to use for handling permissions.
 * @param dispatcher The [CoroutineDispatcher] to use for geolocation operations.
 * @return A new [Geolocator] instance.
 */
public fun Geolocator(
    permissionController: LocationPermissionController = LocationPermissionController.mobile(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = MobileGeolocator(permissionController, dispatcher)

/**
 * Create a new [Geolocator] instance for geolocation operations.
 *
 * Make sure you read the [Android documentation](https://developer.android.com/develop/sensors-and-location/location)
 * as well as the [iOS documentation](https://developer.apple.com/documentation/corelocation)
 * to understand the permissions and accuracy.
 *
 * @param permissionController The [LocationPermissionController] to use for handling permissions.
 * @param dispatcher The [CoroutineDispatcher] to use for geolocation operations.
 * @return A new [Geolocator] instance.
 */
@Suppress("FunctionName")
public fun MobileGeolocator(
    permissionController: LocationPermissionController = LocationPermissionController.mobile(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = Geolocator(MobileLocator(permissionController), dispatcher)

/**
 * Create a new [Geolocator] instance for geolocation operations.
 *
 * Make sure you read the [Android documentation](https://developer.android.com/develop/sensors-and-location/location)
 * as well as the [iOS documentation](https://developer.apple.com/documentation/corelocation)
 * to understand the permissions and accuracy.
 *
 * @param permissionController The [LocationPermissionController] to use for handling permissions.
 * @param dispatcher The [CoroutineDispatcher] to use for geolocation operations.
 * @return A new [Geolocator] instance.
 */
public fun Geolocator.Companion.mobile(
    permissionController: LocationPermissionController = LocationPermissionController.mobile(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = MobileGeolocator(permissionController, dispatcher)