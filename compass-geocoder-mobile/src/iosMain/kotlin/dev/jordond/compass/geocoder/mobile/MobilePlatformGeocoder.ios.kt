package dev.jordond.compass.geocoder.mobile

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.mobile.internal.geocodeOperation
import dev.jordond.compass.geocoder.mobile.internal.toLocations
import dev.jordond.compass.geocoder.mobile.internal.toPlaces
import platform.CoreLocation.CLLocation

internal class IosPlatformGeocoder : MobilePlatformGeocoder {

    override fun isAvailable(): Boolean = true

    override suspend fun placeFromLocation(latitude: Double, longitude: Double): List<Place> {
        val platformLocation = CLLocation(latitude, longitude)
        return geocodeOperation { listener ->
            reverseGeocodeLocation(platformLocation, listener)
        }.toPlaces()
    }

    override suspend fun placeFromAddress(address: String): List<Place> {
        return geocodeOperation { listener -> geocodeAddressString(address, listener) }.toPlaces()
    }

    override suspend fun locationFromAddress(address: String): List<Location> {
        return geocodeOperation { listener ->
            geocodeAddressString(address, listener)
        }.toLocations()
    }
}

internal actual fun createPlatformGeocoder(): MobilePlatformGeocoder {
    return IosPlatformGeocoder()
}