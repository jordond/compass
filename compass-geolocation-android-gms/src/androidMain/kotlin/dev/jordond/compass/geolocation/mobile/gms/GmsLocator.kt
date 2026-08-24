package dev.jordond.compass.geolocation.mobile.gms

import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.Locator
import dev.jordond.compass.geolocation.mobile.AndroidLocator
import dev.jordond.compass.geolocation.mobile.MobileLocator
import dev.jordond.compass.geolocation.mobile.gms.internal.GmsLocationProvider
import dev.jordond.compass.geolocation.mobile.gms.internal.playServicesAvailable
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.mobile
import dev.jordond.compass.tools.ContextProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Whether Google Play Services is present and usable on this device.
 *
 * Locations still work without it, through the platform `LocationManager`. Check this only if your
 * app needs to know which source it is about to get, for example to warn about reduced accuracy.
 */
public fun isPlayServicesAvailable(): Boolean =
    ContextProvider.getInstance().context.playServicesAvailable()

/**
 * Create a [MobileLocator] pinned to the Google Play Services fused location provider.
 *
 * @param permissionController The permission controller to use for handling location permissions.
 * @return A [MobileLocator] backed by the fused provider.
 * @throws IllegalStateException If Google Play Services is not available on this device.
 */
@Suppress("FunctionName")
public fun GmsLocator(
    permissionController: LocationPermissionController = LocationPermissionController.mobile(),
): MobileLocator {
    val context = ContextProvider.getInstance().context
    check(context.playServicesAvailable()) {
        "Google Play Services is not available on this device. Use MobileLocator() instead, " +
            "which falls back to the platform LocationManager."
    }

    return AndroidLocator(permissionController, GmsLocationProvider(context))
}

/**
 * Create a [MobileLocator] pinned to the Google Play Services fused location provider.
 *
 * @param permissionController The permission controller to use for handling location permissions.
 * @return A [MobileLocator] backed by the fused provider.
 * @throws IllegalStateException If Google Play Services is not available on this device.
 * @see GmsLocator
 */
public fun Locator.Companion.gms(
    permissionController: LocationPermissionController = LocationPermissionController.mobile(),
): MobileLocator = GmsLocator(permissionController)

/**
 * Create a [Geolocator] pinned to the Google Play Services fused location provider.
 *
 * @param permissionController The permission controller to use for handling location permissions.
 * @param dispatcher The [CoroutineDispatcher] to use for geolocation operations.
 * @return A [Geolocator] backed by the fused provider.
 * @throws IllegalStateException If Google Play Services is not available on this device.
 * @see GmsLocator
 */
@Suppress("FunctionName")
public fun GmsGeolocator(
    permissionController: LocationPermissionController = LocationPermissionController.mobile(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = Geolocator(GmsLocator(permissionController), dispatcher)

/**
 * Create a [Geolocator] pinned to the Google Play Services fused location provider.
 *
 * @param permissionController The permission controller to use for handling location permissions.
 * @param dispatcher The [CoroutineDispatcher] to use for geolocation operations.
 * @return A [Geolocator] backed by the fused provider.
 * @throws IllegalStateException If Google Play Services is not available on this device.
 * @see GmsLocator
 */
public fun Geolocator.Companion.gms(
    permissionController: LocationPermissionController = LocationPermissionController.mobile(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = GmsGeolocator(permissionController, dispatcher)
