package dev.jordond.compass.geocoder.mobile

import android.content.Context
import android.location.Geocoder
import android.os.Build
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.exception.NotSupportedException
import dev.jordond.compass.geocoder.mobile.internal.asyncOperation
import dev.jordond.compass.geocoder.mobile.internal.context.ContextProvider
import dev.jordond.compass.geocoder.mobile.internal.syncOperation
import dev.jordond.compass.geocoder.mobile.internal.toCoordinates
import dev.jordond.compass.geocoder.mobile.internal.toPlaces

internal class AndroidPlatformGeocoder(
    private val context: Context,
) : MobilePlatformGeocoder {

    override fun isAvailable(): Boolean = Geocoder.isPresent()

    override suspend fun reverse(latitude: Double, longitude: Double): List<Place> {
        val geocoder = createGeocoder()

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

    override suspend fun forward(address: String): List<Coordinates> {
        val geocoder = createGeocoder()
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            @Suppress("DEPRECATION")
            syncOperation {
                geocoder.getFromLocationName(address, MAX_RESULTS)
            }.toCoordinates()
        } else {
            asyncOperation { listener ->
                geocoder.getFromLocationName(address, MAX_RESULTS, listener)
            }.toCoordinates()
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

internal actual fun createPlatformGeocoder(): MobilePlatformGeocoder {
    return AndroidPlatformGeocoder(ContextProvider.getInstance().context)
}