package dev.jordond.compass.geocoder

import dev.jordond.compass.geocoder.mobile.MobilePlatformGeocoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Create a new [Geocoder] instance for geocoding operations.
 *
 * @param dispatcher The [CoroutineDispatcher] to use for geocoding operations.
 * @param locale The locale string used for reverse geocoding, null will use device default
 * @return A new [Geocoder] instance.
 */
public fun Geocoder(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    locale: String? = null,
): Geocoder = MobileGeocoder(dispatcher, locale)

/**
 * Create a new [Geocoder] instance for geocoding operations.
 *
 * @param dispatcher The [CoroutineDispatcher] to use for geocoding operations.
 * @param locale The locale string used for reverse geocoding, null will use device default
 * @return A new [Geocoder] instance.
 */
@Suppress("FunctionName")
public fun MobileGeocoder(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    locale: String? = null,
): Geocoder = Geocoder(MobilePlatformGeocoder(locale), dispatcher)

/**
 * Create a new [Geocoder] instance for geocoding operations.
 *
 * @param dispatcher The [CoroutineDispatcher] to use for geocoding operations.
 * @param locale The locale string used for reverse geocoding, null will use device default
 * @return A new [Geocoder] instance.
 */
public fun Geocoder.Companion.mobile(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    locale: String? = null,
): Geocoder = MobileGeocoder(dispatcher, locale)