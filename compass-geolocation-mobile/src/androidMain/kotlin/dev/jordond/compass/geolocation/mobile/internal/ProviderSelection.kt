package dev.jordond.compass.geolocation.mobile.internal

import android.location.LocationManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.location.LocationRequestCompat
import dev.jordond.compass.Priority

/**
 * Pick the provider to serve [priority], or `null` when nothing can.
 *
 * Only [enabledProviders] are considered. Registering on a disabled provider succeeds and then
 * never delivers, which is indistinguishable from a device that simply cannot get a fix.
 *
 * @param locationEnabled Whether location services are switched on at all.
 * @param enabledProviders The providers reporting themselves as enabled.
 * @param sdkInt The API level to select for.
 */
internal fun selectProvider(
    priority: Priority,
    locationEnabled: Boolean,
    enabledProviders: Collection<String>,
    sdkInt: Int = VERSION.SDK_INT,
): String? {
    if (!locationEnabled) return null

    if (priority == Priority.Passive) {
        return LocationManager.PASSIVE_PROVIDER.takeIf(enabledProviders::contains)
    }

    if (sdkInt >= VERSION_CODES.S && enabledProviders.contains(LocationManager.FUSED_PROVIDER)) {
        return LocationManager.FUSED_PROVIDER
    }

    val preferred = when (priority) {
        Priority.HighAccuracy -> listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
        )
        Priority.Balanced -> listOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.GPS_PROVIDER,
        )
        Priority.LowPower -> listOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER,
        )
        Priority.Passive -> error("Passive is resolved above")
    }

    return preferred
        .firstOrNull(enabledProviders::contains)
        ?: enabledProviders.firstOrNull { provider ->
            provider != LocationManager.PASSIVE_PROVIDER
        }
}

internal fun Priority.toQuality(): Int = when (this) {
    Priority.HighAccuracy -> LocationRequestCompat.QUALITY_HIGH_ACCURACY
    Priority.Balanced -> LocationRequestCompat.QUALITY_BALANCED_POWER_ACCURACY
    Priority.LowPower, Priority.Passive -> LocationRequestCompat.QUALITY_LOW_POWER
}
