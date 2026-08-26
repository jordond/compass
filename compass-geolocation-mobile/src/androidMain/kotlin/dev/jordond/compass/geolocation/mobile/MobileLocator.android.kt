package dev.jordond.compass.geolocation.mobile

import android.content.Context
import dev.jordond.compass.InternalCompassApi
import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.mobile.internal.StandardLocationProvider
import dev.jordond.compass.geolocation.mobile.internal.cachedLocationOrNull
import dev.jordond.compass.geolocation.mobile.internal.toModel
import dev.jordond.compass.permissions.LocationAccuracy
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState
import dev.jordond.compass.permissions.throwOnError
import dev.jordond.compass.tools.ContextProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal actual fun createLocator(
    permissionController: LocationPermissionController,
): MobileLocator {
    return AndroidLocator(ContextProvider.getInstance().context, permissionController)
}

/**
 * Create a [MobileLocator] backed by an explicit [provider].
 *
 * Prefer [MobileLocator], which picks the best available source for the device. This exists so the
 * `dev.jordond.compass:geolocation-android-gms` artifact can hand back a locator pinned to the
 * Google Play Services fused provider.
 */
@Suppress("FunctionName")
@InternalCompassApi
public fun AndroidLocator(
    permissionController: LocationPermissionController,
    provider: PlatformLocationProvider,
): MobileLocator = AndroidLocator(
    context = ContextProvider.getInstance().context,
    permissionController = permissionController,
    provider = provider,
)

internal class AndroidLocator(
    private val context: Context,
    private val permissionController: LocationPermissionController,
    private val provider: PlatformLocationProvider = defaultProvider(context),
) : MobileLocator {

    override val locationUpdates: Flow<Location> = provider.locationUpdates
        .map { location -> location.toModel(context) }

    override suspend fun lastLocation(priority: Priority): Location? {
        requirePermission(priority)
        return provider.lastLocation()?.toModel(context)
    }

    override suspend fun isAvailable(): Boolean {
        return provider.locationEnabled()
    }

    override fun hasPermission(): Boolean {
        return permissionController.hasPermission()
    }

    override fun grantedAccuracy(): LocationAccuracy {
        return permissionController.grantedAccuracy()
    }

    override suspend fun current(priority: Priority): Location {
        requirePermission(priority)
        return provider.currentLocation(priority).toModel(context)
    }

    override suspend fun current(request: LocationRequest): Location =
        cachedLocationOrNull(request) ?: current(request.priority)

    override suspend fun track(request: LocationRequest): Flow<Location> {
        requirePermission(request.priority)
        provider.startTracking(request)

        return locationUpdates
    }

    override fun stopTracking() {
        provider.stopTracking()
    }

    private suspend fun requirePermission(priority: Priority) {
        val state = permissionController.requirePermissionFor(priority)
        if (state != PermissionState.Granted) {
            state.throwOnError()
        }
    }

    private companion object {

        /**
         * The Google Play Services fused provider when the
         * `dev.jordond.compass:geolocation-android-gms` artifact is present and Play Services is
         * on the device, otherwise the platform `LocationManager`.
         */
        fun defaultProvider(context: Context): PlatformLocationProvider =
            LocationProviderRegistry.create(context) ?: StandardLocationProvider(context)
    }
}
