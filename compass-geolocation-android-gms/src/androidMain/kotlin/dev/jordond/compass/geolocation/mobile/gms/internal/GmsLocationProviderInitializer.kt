package dev.jordond.compass.geolocation.mobile.gms.internal

import android.content.Context
import androidx.startup.Initializer
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dev.jordond.compass.geolocation.mobile.LocationProviderRegistry

/**
 * Announces the Google Play Services fused provider to `compass-geolocation-mobile`, so that
 * adding this artifact to an Android source set is the only step needed to opt back into fused
 * locations.
 */
internal class GmsLocationProviderInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        LocationProviderRegistry.register { target ->
            if (target.playServicesAvailable()) {
                GmsLocationProvider(target)
            } else {
                null
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}

internal fun Context.playServicesAvailable(): Boolean = runCatching {
    GoogleApiAvailability.getInstance()
        .isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
}.getOrDefault(false)
