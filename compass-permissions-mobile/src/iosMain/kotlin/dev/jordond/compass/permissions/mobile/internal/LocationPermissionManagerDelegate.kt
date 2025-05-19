package dev.jordond.compass.permissions.mobile.internal

import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.darwin.NSObject

internal class LocationPermissionManagerDelegate : NSObject(), CLLocationManagerDelegateProtocol {

    internal val manager = CLLocationManager()

    private var permissionCallback: ((CLAuthorizationStatus) -> Unit)? = null
    private var requestPermissionCallback: ((CLAuthorizationStatus) -> Unit)? = null
    
    init {
        manager.delegate = this
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

        manager.requestWhenInUseAuthorization()
    }
}
