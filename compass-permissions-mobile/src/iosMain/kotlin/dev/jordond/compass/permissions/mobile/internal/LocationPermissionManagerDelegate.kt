package dev.jordond.compass.permissions.mobile.internal

import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSBundle
import platform.darwin.NSObject

internal class LocationPermissionManagerDelegate : NSObject(), CLLocationManagerDelegateProtocol {

    internal val manager = CLLocationManager()

    private var permissionCallback: ((CLAuthorizationStatus) -> Unit)? = null
    private var requestPermissionCallback: ((CLAuthorizationStatus) -> Unit)? = null

    private val useAlwaysAuthorization = canUseAlwaysAuthorization()
    
    init {
        manager.delegate = this
    }

    private fun canUseAlwaysAuthorization(): Boolean {
        return NSBundle.mainBundle.infoDictionary
            ?.containsKey("NSLocationAlwaysAndWhenInUseUsageDescription") ?: false
    }

    fun currentPermissionStatus(): CLAuthorizationStatus {
        return manager.authorizationStatus
    }

    fun monitorPermission(callback: (CLAuthorizationStatus) -> Unit) {
        permissionCallback = callback
        callback(manager.authorizationStatus)
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        val status = manager.authorizationStatus
        permissionCallback?.invoke(status)
        requestPermissionCallback?.invoke(status)
    }

    fun requestPermission(callback: (CLAuthorizationStatus) -> Unit) {
        requestPermissionCallback = { status ->
            callback(status)
            requestPermissionCallback = null
        }
        
        if (useAlwaysAuthorization) {
            manager.requestAlwaysAuthorization()
        } else {
            manager.requestWhenInUseAuthorization()
        }
    }
}
