package dev.jordond.compass.geolocation.mobile.internal

import dev.jordond.compass.geolocation.exception.GeolocationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationAccuracy
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private typealias LocationWaiter = (NSError?, CLLocation?) -> Unit

private typealias TrackingWaiter = (NSError?) -> Unit

/**
 * Owns the [CLLocationManager] instances used to resolve locations.
 *
 * There are two of them, one for one-shot requests and one for tracking. `requestLocation()` and
 * `startUpdatingLocation()` cancel each other when issued to the same manager, and the two need
 * different `desiredAccuracy` values, so a shared manager makes `current()` and `track()` interfere
 * with each other.
 *
 * Both managers are created and driven on the main thread, see [onMainThread]. Delegate callbacks
 * arrive there too, so none of the callback bookkeeping below needs synchronizing.
 */
internal class LocationManagerDelegate : NSObject(), CLLocationManagerDelegateProtocol {

    private var oneShotManager: CLLocationManager? = null
    private var trackingManager: CLLocationManager? = null

    internal var isTracking: Boolean = false
        private set

    private var locationCallback: ((CLLocation) -> Unit)? = null
    private val locationWaiters = mutableListOf<LocationWaiter>()
    private var trackingWaiter: TrackingWaiter? = null

    suspend fun lastLocation(): CLLocation? = withContext(Dispatchers.Main) {
        requireOneShotManager().location
    }

    fun monitorLocation(callback: (CLLocation) -> Unit) {
        onMainThread { locationCallback = callback }
    }

    /**
     * Request a single location fix at the given [accuracy].
     *
     * Concurrent callers each get their own waiter, so overlapping requests all resolve off the
     * same fix instead of the last one winning.
     *
     * @throws GeolocationException If CoreLocation reports an error.
     */
    suspend fun requestLocation(accuracy: CLLocationAccuracy): CLLocation {
        return suspendCancellableCoroutine { continuation ->
            val waiter: LocationWaiter = { error, location ->
                if (location != null) continuation.resume(location)
                else {
                    val cause = error?.localizedDescription ?: "Unknown error"
                    continuation.resumeWithException(GeolocationException(cause))
                }
            }

            continuation.invokeOnCancellation {
                onMainThread { locationWaiters.remove(waiter) }
            }

            onMainThread {
                if (continuation.isActive) {
                    locationWaiters.add(waiter)
                    val manager = requireOneShotManager()
                    manager.desiredAccuracy = accuracy
                    manager.requestLocation()
                }
            }
        }
    }

    /**
     * Start tracking at the given [accuracy], suspending until the first update arrives.
     *
     * @throws GeolocationException If CoreLocation reports an error.
     */
    suspend fun trackLocation(accuracy: CLLocationAccuracy) {
        if (isTracking) return

        suspendCancellableCoroutine { continuation ->
            val waiter: TrackingWaiter = { error ->
                if (error == null) continuation.resume(Unit)
                else continuation.resumeWithException(GeolocationException(error.localizedDescription))
            }

            continuation.invokeOnCancellation {
                onMainThread { if (trackingWaiter == waiter) trackingWaiter = null }
            }

            onMainThread {
                if (continuation.isActive) {
                    trackingWaiter = waiter
                    val manager = requireTrackingManager()
                    manager.desiredAccuracy = accuracy
                    manager.startUpdatingLocation()
                    isTracking = true
                }
            }
        }
    }

    fun stopTracking() {
        isTracking = false
        onMainThread {
            trackingManager?.stopUpdatingLocation()
            trackingWaiter = null
        }
    }

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        @Suppress("UNCHECKED_CAST")
        val locations = didUpdateLocations as? List<CLLocation> ?: return

        if (manager == trackingManager) {
            locationCallback?.let { callback -> locations.forEach(callback) }
            trackingWaiter?.let { waiter ->
                trackingWaiter = null
                waiter(null)
            }
        } else {
            val location = locations.firstOrNull() ?: return
            resumeLocationWaiters(error = null, location = location)
        }
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        if (manager == trackingManager) {
            trackingWaiter?.let { waiter ->
                trackingWaiter = null
                waiter(didFailWithError)
            }
        } else {
            resumeLocationWaiters(error = didFailWithError, location = null)
        }
    }

    private fun resumeLocationWaiters(error: NSError?, location: CLLocation?) {
        if (locationWaiters.isEmpty()) return

        val waiters = locationWaiters.toList()
        locationWaiters.clear()
        waiters.forEach { waiter -> waiter(error, location) }
    }

    /**
     * Only valid on the main thread, see [onMainThread].
     */
    private fun requireOneShotManager(): CLLocationManager = oneShotManager
        ?: CLLocationManager().also { manager ->
            manager.delegate = this
            oneShotManager = manager
        }

    /**
     * Only valid on the main thread, see [onMainThread].
     */
    private fun requireTrackingManager(): CLLocationManager = trackingManager
        ?: CLLocationManager().also { manager ->
            manager.delegate = this
            trackingManager = manager
        }
}
