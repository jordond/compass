package dev.jordond.compass.geolocation.mobile.internal

import dev.jordond.compass.geolocation.exception.GeolocationException
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationAccuracy
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private typealias LocationWaiter = (GeolocationException?, CLLocation?) -> Unit

private typealias TrackingWaiter = (GeolocationException?) -> Unit

/**
 * Owns the [CLLocationManager] instances used to resolve locations.
 *
 * There are two of them, one for one-shot requests and one for tracking. `requestLocation()` and
 * `startUpdatingLocation()` cancel each other when issued to the same manager, and the two need
 * different `desiredAccuracy` values, so a shared manager makes `current()` and `track()` interfere
 * with each other.
 *
 * Both managers are created and driven on the main thread, see [onMainThread]. Delegate callbacks
 * arrive there too, so none of the callback bookkeeping below needs synchronizing. [isTracking] is
 * the exception: it is read from whichever thread starts or stops tracking, so it is atomic.
 *
 * Every waiter registered here is resumed on exactly one path. A request that can no longer be
 * answered resumes with a failure rather than being dropped, since a dropped waiter leaves its
 * caller suspended for good.
 */
internal class LocationManagerDelegate : NSObject(), CLLocationManagerDelegateProtocol {

    private var oneShotManager: CLLocationManager? = null
    private var trackingManager: CLLocationManager? = null

    private val _isTracking = atomic(false)

    internal val isTracking: Boolean
        get() = _isTracking.value

    private var locationCallback: ((CLLocation) -> Unit)? = null
    private val locationWaiters = mutableListOf<LocationWaiter>()
    private var trackingWaiter: TrackingWaiter? = null

    suspend fun lastLocation(): CLLocation? = awaitOnMainThread {
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
                else continuation.resumeWithException(error ?: unknownError())
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
     * @throws GeolocationException If CoreLocation reports an error, or if tracking is stopped
     * before the first update arrives.
     */
    suspend fun trackLocation(accuracy: CLLocationAccuracy) {
        if (isTracking) return

        suspendCancellableCoroutine { continuation ->
            val waiter: TrackingWaiter = { error ->
                if (error == null) continuation.resume(Unit)
                else continuation.resumeWithException(error)
            }

            // Cancelling before the first fix has to undo the start as well as drop the waiter.
            // Otherwise CoreLocation keeps running with nothing listening, and `isTracking` stays
            // true, which would make every later `trackLocation` return without restarting it.
            continuation.invokeOnCancellation {
                onMainThread {
                    if (trackingWaiter == waiter) {
                        trackingWaiter = null
                        stopUpdating()
                    }
                }
            }

            onMainThread {
                if (continuation.isActive) {
                    trackingWaiter = waiter
                    val manager = requireTrackingManager()
                    manager.desiredAccuracy = accuracy
                    manager.startUpdatingLocation()
                    _isTracking.value = true
                }
            }
        }
    }

    fun stopTracking() {
        onMainThread {
            stopUpdating()

            // A `trackLocation` still waiting on its first fix will never get one now, so fail it
            // rather than leave its caller suspended.
            trackingWaiter?.let { waiter ->
                trackingWaiter = null
                waiter(GeolocationException("Tracking stopped before a location was reported"))
            }
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
        val error = GeolocationException(didFailWithError.localizedDescription)

        if (manager == trackingManager) {
            trackingWaiter?.let { waiter ->
                trackingWaiter = null
                waiter(error)
            }
        } else {
            resumeLocationWaiters(error = error, location = null)
        }
    }

    private fun resumeLocationWaiters(error: GeolocationException?, location: CLLocation?) {
        if (locationWaiters.isEmpty()) return

        val waiters = locationWaiters.toList()
        locationWaiters.clear()
        waiters.forEach { waiter -> waiter(error, location) }
    }

    /**
     * Only valid on the main thread, see [onMainThread].
     */
    private fun stopUpdating() {
        trackingManager?.stopUpdatingLocation()
        _isTracking.value = false
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

    private fun unknownError() = GeolocationException("Unknown error")
}
