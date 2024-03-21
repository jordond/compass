package dev.jordond.compass.geolocation.mobile.internal

import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject

internal class LocationTracker(
    private val onLocationUpdate: (CLLocation) -> Unit,
    private val onLocationError: (NSError) -> Unit,
) : NSObject(), CLLocationManagerDelegateProtocol {

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        @Suppress("UNCHECKED_CAST")
        val locations = didUpdateLocations as? List<CLLocation>
        locations?.forEach(onLocationUpdate)
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        onLocationError(didFailWithError)
    }
}