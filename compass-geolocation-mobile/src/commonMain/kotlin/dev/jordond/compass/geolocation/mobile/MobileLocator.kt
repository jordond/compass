package dev.jordond.compass.geolocation.mobile

import dev.jordond.compass.geolocation.Locator
import dev.jordond.compass.geolocation.PermissionLocator
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.mobile.mobile

/**
 * A locator that provides geolocation services on Android and iOS.
 */
public interface MobileLocator : PermissionLocator

/**
 * Create an Android/iOS [MobileLocator] instance for geolocation operations.
 *
 * Make sure you read the [Android documentation](https://developer.android.com/develop/sensors-and-location/location)
 * as well as the [iOS documentation](https://developer.apple.com/documentation/corelocation)
 * to understand the permissions and accuracy.
 *
 * @param permissionController The permission controller to use for handling location permissions.
 * @return A new Android/iOS [MobileLocator] instance.
 */
public fun MobileLocator(
    permissionController: LocationPermissionController = LocationPermissionController.mobile(),
): MobileLocator = createLocator(permissionController)

/**
 * Create an Android/iOS [MobileLocator] instance for geolocation operations.
 *
 * Make sure you read the [Android documentation](https://developer.android.com/develop/sensors-and-location/location)
 * as well as the [iOS documentation](https://developer.apple.com/documentation/corelocation)
 * to understand the permissions and accuracy.
 *
 * @param permissionController The permission controller to use for handling location permissions.
 * @return A new Android/iOS [MobileLocator] instance.
 */
public fun Locator.Companion.mobile(
    permissionController: LocationPermissionController = LocationPermissionController.mobile(),
): MobileLocator = MobileLocator(permissionController)

internal expect fun createLocator(permissionController: LocationPermissionController): MobileLocator