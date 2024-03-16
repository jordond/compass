package dev.jordond.compass.geocoder.mobile.internal

import dev.jordond.compass.geocoder.exception.GeocodeException
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLGeocodeCompletionHandler
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLPlacemark
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal suspend fun geocodeOperation(
    block: CLGeocoder.(listener: CLGeocodeCompletionHandler) -> Unit,
): List<CLPlacemark> {
    val geocoder = CLGeocoder()

    @Suppress("UNCHECKED_CAST")
    return suspendCancellableCoroutine { continuation ->
        val completionHandler: CLGeocodeCompletionHandler = { result, error ->
            when {
                error != null ->
                    continuation.resumeWithException(GeocodeException(error.localizedDescription))
                else -> {
                    continuation.resume((result as? List<CLPlacemark>) ?: emptyList())
                }
            }
        }

        geocoder.block(completionHandler)

        continuation.invokeOnCancellation {
            geocoder.cancelGeocode()
        }
    }
}