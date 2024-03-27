package dev.jordond.compass.geolocation.mobile.internal

import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationAccuracy
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject

internal class LocationManagerDelegate : NSObject(), CLLocationManagerDelegateProtocol {

    private val manager = CLLocationManager()
    internal var isTracking: Boolean = false
        private set

    private var locationCallback: ((CLLocation) -> Unit)? = null
    private var oneTimeLocationCallback: ((NSError?, CLLocation?) -> Unit)? = null
    private var oneTimeTrackingCallback: ((NSError?) -> Unit)? = null

    init {
        manager.delegate = this
    }

    fun monitorLocation(callback: (CLLocation) -> Unit) {
        locationCallback = callback
    }

    fun requestLocation(callback: (NSError?, CLLocation?) -> Unit) {
        oneTimeLocationCallback = callback
        manager.requestLocation()
    }

    fun trackLocation(accuracy: CLLocationAccuracy, callback: (NSError?) -> Unit) {
        if (isTracking) return

        manager.desiredAccuracy = accuracy
        manager.startUpdatingLocation()
        isTracking = true
        oneTimeTrackingCallback = callback
    }

    fun stopTracking() {
        manager.stopUpdatingLocation()
        oneTimeTrackingCallback = null
        isTracking = false
    }

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        @Suppress("UNCHECKED_CAST")
        val locations = didUpdateLocations as? List<CLLocation>

        locationCallback?.let { callback -> locations?.forEach(callback) }

        oneTimeTrackingCallback?.let { oneTimeCallback ->
            oneTimeCallback(null)
            oneTimeTrackingCallback = null
        }

        oneTimeLocationCallback?.let { oneTimeCallback ->
            val location = locations?.firstOrNull()
            if (location != null) {
                oneTimeCallback(null, location)
                oneTimeLocationCallback = null
            }
        }
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        oneTimeTrackingCallback?.let { oneTimeCallback ->
            oneTimeCallback(didFailWithError)
            oneTimeTrackingCallback = null
        }
        oneTimeLocationCallback?.let { oneTimeCallback ->
            oneTimeCallback(didFailWithError, null)
            oneTimeLocationCallback = null
        }
    }
}