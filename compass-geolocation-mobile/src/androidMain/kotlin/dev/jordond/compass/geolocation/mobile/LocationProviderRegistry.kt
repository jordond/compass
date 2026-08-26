package dev.jordond.compass.geolocation.mobile

import android.content.Context
import dev.jordond.compass.InternalCompassApi

/**
 * Where an optional [PlatformLocationProvider] implementation announces itself to the Android
 * locator.
 *
 * The `dev.jordond.compass:geolocation-android-gms` artifact registers its Google Play Services
 * fused provider here from an `androidx.startup` initializer, so simply adding that artifact to an
 * Android source set is enough to opt back into fused locations.
 *
 * When nothing registers, or the registered factory declines the device, the locator falls back to
 * the platform `LocationManager`.
 */
@InternalCompassApi
public object LocationProviderRegistry {

    @Volatile
    private var factory: Factory? = null

    /**
     * Builds a [PlatformLocationProvider] for a device, or returns `null` when it cannot serve the
     * device it is asked about.
     */
    @InternalCompassApi
    public fun interface Factory {

        public fun create(context: Context): PlatformLocationProvider?
    }

    /**
     * Register [factory] as the preferred source. The most recent registration wins.
     */
    public fun register(factory: Factory) {
        this.factory = factory
    }

    /**
     * The preferred source for [context], or `null` to use the platform default.
     */
    internal fun create(context: Context): PlatformLocationProvider? = factory?.create(context)
}
