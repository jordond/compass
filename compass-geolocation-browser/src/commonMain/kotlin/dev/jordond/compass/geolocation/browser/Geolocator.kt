package dev.jordond.compass.geolocation.browser

import dev.jordond.compass.geolocation.Geolocator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Create a new [Geolocator] that uses the [BrowserLocator] for geolocation operations.
 *
 * [MDN documentation](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation)
 *
 * @param dispatcher The [CoroutineDispatcher] to use for geolocation operations.
 * @return A new [Geolocator] instance.
 */
public fun Geolocator(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = BrowserGeolocator(dispatcher)

/**
 * Create a new [Geolocator] that uses the [BrowserLocator] for geolocation operations.
 *
 * [MDN documentation](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation)
 *
 * @param dispatcher The [CoroutineDispatcher] to use for geolocation operations.
 * @return A new [Geolocator] instance.
 */
@Suppress("FunctionName")
public fun BrowserGeolocator(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = Geolocator(createBrowserLocator(), dispatcher)