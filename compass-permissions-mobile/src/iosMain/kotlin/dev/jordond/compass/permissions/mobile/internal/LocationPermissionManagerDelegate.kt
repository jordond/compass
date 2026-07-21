package dev.jordond.compass.permissions.mobile.internal

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.Foundation.NSBundle
import platform.darwin.NSObject
import kotlin.coroutines.resume

private const val WHEN_IN_USE_USAGE_DESCRIPTION = "NSLocationWhenInUseUsageDescription"

/**
 * Owns the [CLLocationManager] used to read and request the location permission.
 *
 * The manager that prompts is created and driven on the main thread, see [onMainThread], because
 * CoreLocation only delivers [locationManagerDidChangeAuthorization] on the run loop the manager
 * was created on. Reading the status does not go through it, see [statusReader].
 */
internal class LocationPermissionManagerDelegate : NSObject(), CLLocationManagerDelegateProtocol {

    private var manager: CLLocationManager? = null

    /**
     * A second manager, without a delegate, used only to read the authorization status.
     *
     * Reading that property needs no run loop, since the run loop only matters for delivering
     * delegate callbacks and a manager without a delegate has none to deliver. Keeping it separate
     * is what lets the status be read from any thread without suspending, which callers such as
     * `hasPermission()` need because they cannot hop onto the main thread to ask.
     */
    private val statusReader: CLLocationManager by lazy { CLLocationManager() }

    private var permissionCallback: ((CLAuthorizationStatus) -> Unit)? = null
    private var requestPermissionCallback: ((CLAuthorizationStatus) -> Unit)? = null

    fun currentPermissionStatus(): CLAuthorizationStatus = statusReader.authorizationStatus

    /**
     * Report every authorization change to [callback].
     *
     * Changes only. The status as it stands is available from [currentPermissionStatus], which does
     * not have to wait to get onto the main thread.
     */
    fun monitorPermission(callback: (CLAuthorizationStatus) -> Unit) {
        onMainThread {
            permissionCallback = callback

            // Attaching the delegate is what starts the reports.
            requireManager()
        }
    }

    /**
     * Show the permission prompt and suspend until the user answers it.
     *
     * Resolves without prompting when the status is already an answer, and when the app is unable
     * to prompt at all, so it can never be left waiting on a callback that is not coming.
     */
    suspend fun requestPermission(): CLAuthorizationStatus {
        return suspendCancellableCoroutine { continuation ->
            val callback: (CLAuthorizationStatus) -> Unit = { answer ->
                continuation.resume(answer)
            }

            continuation.invokeOnCancellation {
                onMainThread {
                    if (requestPermissionCallback == callback) requestPermissionCallback = null
                }
            }

            onMainThread {
                if (!continuation.isActive) return@onMainThread

                val manager = requireManager()
                val status = manager.authorizationStatus
                when {
                    // Already answered. `requestWhenInUseAuthorization` does nothing once the
                    // status is settled, so asking would wait on a change that never comes.
                    status != kCLAuthorizationStatusNotDetermined -> continuation.resume(status)

                    // Without the usage description in Info.plist iOS refuses to show the prompt
                    // and reports nothing back, so the permission can never be granted. Denied is
                    // the honest answer, and the only one that does not strand the caller.
                    !canPromptForWhenInUse() -> continuation.resume(kCLAuthorizationStatusDenied)

                    else -> {
                        requestPermissionCallback = callback
                        manager.requestWhenInUseAuthorization()
                    }
                }
            }
        }
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        val status = manager.authorizationStatus
        permissionCallback?.invoke(status)

        // CoreLocation reports `notDetermined` when the delegate is first attached, and again while
        // the prompt is still on screen. Neither is an answer, so keep waiting for a real one.
        // Every case where an answer is never coming is settled before the prompt is requested.
        if (status == kCLAuthorizationStatusNotDetermined) return

        requestPermissionCallback?.let { callback ->
            requestPermissionCallback = null
            callback(status)
        }
    }

    private fun canPromptForWhenInUse(): Boolean =
        NSBundle.mainBundle.objectForInfoDictionaryKey(WHEN_IN_USE_USAGE_DESCRIPTION) != null

    /**
     * Only valid on the main thread, see [onMainThread].
     */
    private fun requireManager(): CLLocationManager = manager
        ?: CLLocationManager().also { locationManager ->
            locationManager.delegate = this
            manager = locationManager
        }
}
