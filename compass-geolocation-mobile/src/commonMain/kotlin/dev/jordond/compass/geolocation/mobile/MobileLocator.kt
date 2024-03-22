package dev.jordond.compass.geolocation.mobile

import dev.jordond.compass.geolocation.Locator

public interface MobileLocator : Locator

/**
 * Create an Android/iOS [MobileLocator] instance for geolocation operations.
 *
 * @return A new Android/iOS [MobileLocator] instance.
 */
public fun MobileLocator(
    handlePermissions: Boolean = true,
): MobileLocator = createLocator(handlePermissions)

/**
 * Create an Android/iOS [MobileLocator] instance for geolocation operations.
 *
 * @return A new Android/iOS [MobileLocator] instance.
 */
public fun Locator.Companion.mobile(
    handlePermissions: Boolean = true,
): MobileLocator = MobileLocator(handlePermissions)

internal expect fun createLocator(handlePermissions: Boolean): MobileLocator