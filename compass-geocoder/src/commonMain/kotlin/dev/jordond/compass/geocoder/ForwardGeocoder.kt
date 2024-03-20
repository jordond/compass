package dev.jordond.compass.geocoder

import dev.jordond.compass.Location
import dev.jordond.compass.geocoder.internal.DefaultGeocoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public interface ForwardGeocoder {

    /**
     * Get a list of [Location]s for a given address.
     *
     * In most cases, the list will contain a single [Location]. However, in some cases, it may
     * return multiple [Location]s.
     *
     * @param address The address to geocode.
     * @return A [GeocoderResult] containing the list of [Location]s or an error.
     */
    public suspend fun locations(address: String): GeocoderResult<Location>
}

public fun ForwardGeocoder(
    platformGeocoder: PlatformGeocoder,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): ForwardGeocoder = DefaultGeocoder(platformGeocoder, dispatcher)
