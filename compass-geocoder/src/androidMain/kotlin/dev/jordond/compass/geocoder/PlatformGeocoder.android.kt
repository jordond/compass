package dev.jordond.compass.geocoder

import android.content.Context
import android.location.Geocoder
import android.os.Build
import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.internal.NotSupportedException
import dev.jordond.compass.geocoder.internal.asyncOperation
import dev.jordond.compass.geocoder.internal.context.ContextProvider
import dev.jordond.compass.geocoder.internal.syncOperation
import dev.jordond.compass.geocoder.internal.toLocations
import dev.jordond.compass.geocoder.internal.toPlaces

internal class AndroidPlatformGeocoder(
    private val context: Context,
) : PlatformGeocoder {

    override fun isAvailable(): Boolean = Geocoder.isPresent()

    override suspend fun placeFromLocation(location: Location): List<Place> {
        val geocoder = createGeocoder()
        val (latitude, longitude) = location.run { latitude to longitude }

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            @Suppress("DEPRECATION")
            syncOperation { geocoder.getFromLocation(latitude, longitude, MAX_RESULTS) }.toPlaces()
        } else {
            asyncOperation { listener ->
                geocoder.getFromLocation(latitude, longitude, MAX_RESULTS, listener)
            }.toPlaces()
        }
    }

    override suspend fun placeFromAddress(address: String): List<Place> {
        val geocoder = createGeocoder()
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            @Suppress("DEPRECATION")
            syncOperation { geocoder.getFromLocationName(address, MAX_RESULTS) }.toPlaces()
        } else {
            asyncOperation { listener ->
                geocoder.getFromLocationName(address, MAX_RESULTS, listener)
            }.toPlaces()
        }
    }

    override suspend fun locationFromAddress(address: String): List<Location> {
        val geocoder = createGeocoder()
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            @Suppress("DEPRECATION")
            syncOperation {
                geocoder.getFromLocationName(address, MAX_RESULTS)
            }.toLocations()
        } else {
            asyncOperation { listener ->
                geocoder.getFromLocationName(address, MAX_RESULTS, listener)
            }.toLocations()
        }
    }

    private fun createGeocoder(): Geocoder {
        if (Geocoder.isPresent().not()) throw NotSupportedException()
        return Geocoder(context)
    }

    companion object {

        private const val MAX_RESULTS = 5
    }
}

internal actual fun createPlatformGeocoder(): PlatformGeocoder {
    return AndroidPlatformGeocoder(ContextProvider.getInstance().context)
}