package dev.jordond.compass.geocoder.mobile

import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.GeocoderResult
import dev.jordond.compass.geocoder.PlatformGeocoder

public interface MobilePlatformGeocoder : PlatformGeocoder {

    /**
     * Geocode an address to a list of [Place].
     *
     * In most cases, the list will contain a single [Place]. However, in some cases, it may
     * return multiple [Place]s.
     *
     * @param address The address to geocode.
     * @return A [GeocoderResult] containing the list of [Place]s or an error.
     */
    public suspend fun placeFromAddress(address: String): List<Place>
}

/**
 * Create an Android/iOS [PlatformGeocoder] instance for geocoding operations.
 *
 * @return A new Android/iOS [PlatformGeocoder] instance.
 */
public fun MobilePlatformGeocoder(): MobilePlatformGeocoder = createPlatformGeocoder()

/**
 * Create an Android/iOS [PlatformGeocoder] instance for geocoding operations.
 *
 * @return A new Android/iOS [PlatformGeocoder] instance.
 */
public fun PlatformGeocoder.Companion.create(): MobilePlatformGeocoder = MobilePlatformGeocoder()

internal expect fun createPlatformGeocoder(): MobilePlatformGeocoder