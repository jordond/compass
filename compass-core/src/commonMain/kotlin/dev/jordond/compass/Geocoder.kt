package dev.jordond.compass

import dev.drewhamilton.poko.Poko
import dev.jordond.compass.internal.DefaultGeocoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public interface Geocoder {

    public fun available(): Boolean

    public suspend fun reverseGeocode(coordinates: LatLng): Result

    public suspend fun reverseGeocode(latitude: Double, longitude: Double): Result =
        reverseGeocode(LatLng(latitude, longitude))

    public suspend fun reverseGeocodeOrNull(coordinates: LatLng): Location? =
        reverseGeocode(coordinates).locationOrNull()

    public suspend fun reverseGeocodeOrNull(latitude: Double, longitude: Double): Location? =
        reverseGeocode(latitude, longitude).locationOrNull()

    public sealed interface Result {

        public interface Error : Result

        @Poko
        public class Success(public val location: Location) : Result

        public object NotFound : Error

        @Poko
        public class GeocodeFailed(public val message: String) : Error
        public object NotSupported : Error
        public object InvalidCoordinates : Error

        public val isError: Boolean
            get() = this is Error

        public fun locationOrNull(): Location? = if (this is Success) location else null

        public fun errorOrNull(): Error? = if (this is Error) this else null
    }
}

public fun Geocoder(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = DefaultGeocoder(dispatcher)