package dev.jordond.compass.geocoder

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.internal.geocodeOperation
import dev.jordond.compass.geocoder.internal.toLocations
import dev.jordond.compass.geocoder.internal.toPlaces
import platform.CoreLocation.CLLocation

internal class IosPlatformGeocoder : PlatformGeocoder {

    override fun isAvailable(): Boolean = true

    override suspend fun placeFromLocation(location: Location): List<Place> {
        val platformLocation = CLLocation(location.latitude, location.longitude)
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

internal actual fun createPlatformGeocoder(): PlatformGeocoder {
    return IosPlatformGeocoder()
}