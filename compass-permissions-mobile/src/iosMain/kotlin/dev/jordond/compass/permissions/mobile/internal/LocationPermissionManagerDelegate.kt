package dev.jordond.compass.permissions.mobile.internal

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.darwin.NSObject
import kotlin.coroutines.resume

/**
 * Owns the [CLLocationManager] used to read and request the location permission.
 *
 * The manager is created and driven on the main thread, see [onMainThread], because CoreLocation
 * only delivers [locationManagerDidChangeAuthorization] on the run loop the manager was created on.
 */
internal class LocationPermissionManagerDelegate : NSObject(), CLLocationManagerDelegateProtocol {

    private var manager: CLLocationManager? = null

    private var permissionCallback: ((CLAuthorizationStatus) -> Unit)? = null
    private var requestPermissionCallback: ((CLAuthorizationStatus) -> Unit)? = null

    suspend fun currentPermissionStatus(): CLAuthorizationStatus = withContext(Dispatchers.Main) {
        requireManager().authorizationStatus
    }

    fun monitorPermission(callback: (CLAuthorizationStatus) -> Unit) {
        onMainThread {
            permissionCallback = callback
            callback(requireManager().authorizationStatus)
        }
    }

    /**
     * Show the permission prompt and suspend until the user answers it.
     */
    suspend fun requestPermission(): CLAuthorizationStatus {
        return suspendCancellableCoroutine { continuation ->
            val callback: (CLAuthorizationStatus) -> Unit = { status -> continuation.resume(status) }

            continuation.invokeOnCancellation {
                onMainThread {
                    if (requestPermissionCallback == callback) requestPermissionCallback = null
                }
            }

            onMainThread {
                if (continuation.isActive) {
                    requestPermissionCallback = callback
                    requireManager().requestWhenInUseAuthorization()
                }
            }
        }
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        val status = manager.authorizationStatus
        permissionCallback?.invoke(status)

        // CoreLocation reports `notDetermined` when the delegate is first attached, and again while
        // the prompt is still on screen. Neither is an answer, so keep waiting for a real one.
        if (status == kCLAuthorizationStatusNotDetermined) return

        requestPermissionCallback?.let { callback ->
            requestPermissionCallback = null
            callback(status)
        }
    }

    /**
     * Only valid on the main thread, see [onMainThread].
     */
    private fun requireManager(): CLLocationManager = manager
        ?: CLLocationManager().also { locationManager ->
            locationManager.delegate = this
            manager = locationManager
        }
}
