package geolocation

import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.TrackingStatus
import dev.stateholder.extensions.voyager.StateScreenModel
import geolocation.GeolocationModel.State
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GeolocationModel(private val geolocator: Geolocator) : StateScreenModel<State>(State()) {

    init {
        screenModelScope.launch {
            val isAvailable = geolocator.isAvailable()
            updateState { it.copy(locationServiceAvailable = isAvailable) }
        }

        geolocator.trackingStatus
            .onEach { status ->
                Logger.d { "GeolocationModel: tracking status -> $status" }
                updateState { it.copy(trackingLocation = status) }
            }
            .launchIn(screenModelScope)
    }

    fun currentLocation() {
        screenModelScope.launch {
            updateState { it.copy(loading = true) }
            val result = geolocator.current()
            updateState { it.copy(lastResult = result, loading = false) }
        }
    }

    fun startTracking() {
        if (state.value.busy) return

        geolocator.track(LocationRequest(priority = Priority.HighAccuracy))
    }

    fun stopTracking() {
        geolocator.stopTracking()
    }

    data class State(
        val handlePermissions: Boolean = true,
        val loading: Boolean = false,
        val location: Location? = null,
        val lastResult: GeolocatorResult? = null,
        val locationServiceAvailable: Boolean = false,
        val trackingLocation: TrackingStatus = TrackingStatus.Idle,
    ) {

        val tracking = trackingLocation.isActive
        val trackingError = (trackingLocation as? TrackingStatus.Error)?.cause
        val busy: Boolean = loading || tracking
        val permissionsDeniedForever: Boolean =
            lastResult is GeolocatorResult.PermissionDenied && lastResult.forever
    }
}