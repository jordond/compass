package dev.jordond.compass.geolocation.mobile

import dev.jordond.compass.geolocation.Locator
import dev.jordond.compass.geolocation.PermissionLocator
import dev.jordond.compass.permissions.exception.PermissionMissingException

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
 * @param handlePermissions Whether to handle permissions requesting automatically. If this is
 * `false` and the required permissions are not granted, a [PermissionMissingException] will be
 * thrown when attempting to perform geolocation operations.
 * @return A new Android/iOS [MobileLocator] instance.
 */
public fun MobileLocator(
    handlePermissions: Boolean = true,
): MobileLocator = createLocator(handlePermissions)

/**
 * Create an Android/iOS [MobileLocator] instance for geolocation operations.
 *
 * Make sure you read the [Android documentation](https://developer.android.com/develop/sensors-and-location/location)
 * as well as the [iOS documentation](https://developer.apple.com/documentation/corelocation)
 * to understand the permissions and accuracy.
 *
 * @param handlePermissions Whether to handle permissions requesting automatically. If this is
 * `false` and the required permissions are not granted, a [PermissionMissingException] will be
 * thrown when attempting to perform geolocation operations.
 * @return A new Android/iOS [MobileLocator] instance.
 */
public fun Locator.Companion.mobile(
    handlePermissions: Boolean = true,
): MobileLocator = MobileLocator(handlePermissions)

internal expect fun createLocator(handlePermissions: Boolean): MobileLocator