package dev.jordond.compass.geocoder

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.internal.DefaultGeocoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public interface Geocoder {

    public fun available(): Boolean

    public suspend fun places(coordinates: Location): GeocoderResult

    public suspend fun places(latitude: Double, longitude: Double): GeocoderResult =
        places(Location(latitude, longitude))
}

public fun Geocoder(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = DefaultGeocoder(dispatcher)

public suspend fun Geocoder.placesOrNull(coordinates: Location): List<Place>? =
    places(coordinates).locationsOrNull()

public suspend fun Geocoder.placesOrNull(latitude: Double, longitude: Double): List<Place>? =
    places(latitude, longitude).locationsOrNull()

public suspend fun Geocoder.placeOrNull(coordinates: Location): Place? =
    places(coordinates).locationsOrNull()?.firstOrNull()

public suspend fun Geocoder.placeOrNull(latitude: Double, longitude: Double): Place? =
    places(latitude, longitude).locationsOrNull()?.firstOrNull()
