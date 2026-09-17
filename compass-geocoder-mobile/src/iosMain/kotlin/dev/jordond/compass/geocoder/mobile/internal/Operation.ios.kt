package dev.jordond.compass.geocoder.mobile.internal

import dev.jordond.compass.geocoder.exception.GeocodeException
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLGeocodeCompletionHandler
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLPlacemark
import platform.CoreLocation.kCLErrorDomain
import platform.CoreLocation.kCLErrorGeocodeFoundNoResult
import platform.CoreLocation.kCLErrorGeocodeFoundPartialResult
import platform.Foundation.NSError
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal suspend fun geocodeOperation(
    block: CLGeocoder.(listener: CLGeocodeCompletionHandler) -> Unit,
): List<CLPlacemark> {
    val geocoder = CLGeocoder()

    return suspendCancellableCoroutine { continuation ->
        val completionHandler: CLGeocodeCompletionHandler = { result, error ->
            @Suppress("UNCHECKED_CAST")
            val placemarks = (result as? List<CLPlacemark>).orEmpty()
            when {
                error == null || error.isIncompleteMatch() -> continuation.resume(placemarks)
                else ->
                    continuation.resumeWithException(GeocodeException(error.localizedDescription))
            }
        }

        geocoder.block(completionHandler)

        continuation.invokeOnCancellation {
            geocoder.cancelGeocode()
        }
    }
}

/**
 * Whether this error is `CLGeocoder` reporting that the request completed without a full match.
 *
 * Unlike Android, which returns whatever addresses it has, `CLGeocoder` signals both "no match"
 * (`kCLErrorGeocodeFoundNoResult`) and "partial match" (`kCLErrorGeocodeFoundPartialResult`) with
 * an error. Handing back the placemarks it did produce lets `DefaultGeocoder` map an empty result
 * to `GeocoderResult.NotFound` and a usable one to `GeocoderResult.Success`, so both platforms
 * behave the same way.
 */
private fun NSError.isIncompleteMatch(): Boolean = domain == kCLErrorDomain &&
    (code == kCLErrorGeocodeFoundNoResult || code == kCLErrorGeocodeFoundPartialResult)