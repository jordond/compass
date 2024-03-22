package dev.jordond.compass.geolocation.mobile

import dev.jordond.compass.geolocation.Locator
import dev.jordond.compass.geolocation.exception.PermissionMissingException

public interface MobileLocator : Locator {

    public fun hasPermission(): Boolean
}

/**
 * Create an Android/iOS [MobileLocator] instance for geolocation operations.
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
 * @param handlePermissions Whether to handle permissions requesting automatically. If this is
 * `false` and the required permissions are not granted, a [PermissionMissingException] will be
 * thrown when attempting to perform geolocation operations.
 * @return A new Android/iOS [MobileLocator] instance.
 */
public fun Locator.Companion.mobile(
    handlePermissions: Boolean = true,
): MobileLocator = MobileLocator(handlePermissions)

internal expect fun createLocator(handlePermissions: Boolean): MobileLocator