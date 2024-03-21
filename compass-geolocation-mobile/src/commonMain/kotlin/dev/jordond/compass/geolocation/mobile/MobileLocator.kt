package dev.jordond.compass.geolocation.mobile

import dev.jordond.compass.geolocation.Locator

public interface MobileLocator : Locator

/**
 * Create an Android/iOS [MobileLocator] instance for geolocation operations.
 *
 * @return A new Android/iOS [MobileLocator] instance.
 */
public fun MobileLocator(): MobileLocator = createLocator()

/**
 * Create an Android/iOS [MobileLocator] instance for geolocation operations.
 *
 * @return A new Android/iOS [MobileLocator] instance.
 */
public fun Locator.Companion.mobile(): MobileLocator = MobileLocator()

internal expect fun createLocator(): MobileLocator