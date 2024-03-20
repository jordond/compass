package dev.jordond.compass.geocoder

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.internal.DefaultGeocoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public interface ReverseGeocoder {

    /**
     * Get the address for a given latitude and longitude.
     *
     * @param latitude The latitude to reverse geocode.
     * @param longitude The longitude to reverse geocode.
     * @return A [GeocoderResult] containing a list of addresses or an error.
     */
    public suspend fun places(latitude: Double, longitude: Double): GeocoderResult<Place>

    /**
     * Get the address for a given [Location].
     *
     * @param location The [Location] to reverse geocode.
     * @return A [GeocoderResult] containing a list of addresses or an error.
     */
    public suspend fun places(location: Location): GeocoderResult<Place> =
        places(location.latitude, location.longitude)
}

public fun ReverseGeocoder(
    platformGeocoder: PlatformGeocoder,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): ReverseGeocoder = DefaultGeocoder(platformGeocoder, dispatcher)