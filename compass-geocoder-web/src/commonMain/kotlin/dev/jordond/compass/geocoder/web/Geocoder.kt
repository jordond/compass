package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.ForwardGeocoder
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.ReverseGeocoder
import dev.jordond.compass.geocoder.web.internal.DefaultHttpApiPlatformGeocoder
import dev.jordond.compass.geocoder.web.internal.ForwardHttpApiPlatformGeocoder
import dev.jordond.compass.geocoder.web.internal.ReverseHttpApiPlatformGeocoder
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

/**
 * Create a new [Geocoder] instance for geocoding operations.
 *
 * @param dispatcher The [CoroutineDispatcher] to use for geocoding operations.
 * @return A new [Geocoder] instance.
 */
public fun Geocoder(
    platformGeocoder: HttpApiPlatformGeocoder,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = Geocoder(platformGeocoder, dispatcher)

public fun Geocoder(
    forwardEndpoint: HttpApiEndpoint<String, List<Location>>,
    reverseEndpoint: HttpApiEndpoint<Location, List<Place>>,
    json: Json = HttpApiPlatformGeocoder.json(),
    httpClient: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder {
    val apiGeocoder = DefaultHttpApiPlatformGeocoder(forwardEndpoint, reverseEndpoint, httpClient)
    return Geocoder(apiGeocoder, dispatcher)
}

/**
 * Create a new [Geocoder] instance for geocoding operations.
 *
 * @param endpoint The [HttpApiEndpoint] to use for forward geocoding operations.
 * @param dispatcher The [CoroutineDispatcher] to use for geocoding operations.
 * @return A new [Geocoder] instance.
 */
public fun ForwardGeocoder(
    endpoint: HttpApiEndpoint<String, List<Location>>,
    json: Json = HttpApiPlatformGeocoder.json(),
    httpClient: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): ForwardGeocoder {
    return ForwardGeocoder(ForwardHttpApiPlatformGeocoder(endpoint, httpClient), dispatcher)
}

/**
 * Create a new [Geocoder] instance for geocoding operations.
 *
 * @param endpoint The [HttpApiEndpoint] to use for forward geocoding operations.
 * @param dispatcher The [CoroutineDispatcher] to use for geocoding operations.
 * @return A new [Geocoder] instance.
 */
public fun ReverseGeocoder(
    endpoint: HttpApiEndpoint<Location, List<Place>>,
    json: Json = HttpApiPlatformGeocoder.json(),
    httpClient: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): ReverseGeocoder {
    return ReverseGeocoder(ReverseHttpApiPlatformGeocoder(endpoint, httpClient), dispatcher)
}
