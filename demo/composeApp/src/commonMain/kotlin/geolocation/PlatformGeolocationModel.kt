package geolocation

import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import createGeolocator
import dev.jordond.compass.Location
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.Priority
import dev.stateholder.extensions.voyager.StateScreenModel
import geolocation.PlatformGeolocationModel.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlatformGeolocationModel : StateScreenModel<State>(State()) {

    private var trackingJob: Job? = null

    fun toggleHandlePermissions() {
        if (trackingJob != null || state.value.busy) return

        updateState { state ->
            state.copy(
                handlePermissions = !this.state.value.handlePermissions,
                geolocator = createGeolocator(!this.state.value.handlePermissions)
            )
        }
    }

    fun lastLocation() {
        screenModelScope.launch {
            updateState { it.copy(loading = true) }
            val result = state.value.geolocator.last()
            updateState { it.copy(lastOperation = "last", lastResult = result, loading = false) }
        }
    }

    fun currentLocation() {
        screenModelScope.launch {
            updateState { it.copy(loading = true) }
            val result = state.value.geolocator.current()
            updateState { it.copy(lastOperation = "current", lastResult = result, loading = false) }
        }
    }

    fun startTracking() {
        if (trackingJob != null || state.value.busy) return

        trackingJob = screenModelScope.launch {
            updateState { it.copy(tracking = true) }
            state.value.geolocator.track(LocationRequest(priority = Priority.HighAccuracy))
                .catch { cause ->
                    updateState { state ->
                        state.copy(trackingError = cause.message, tracking = false)
                    }
                    cancel()
                }
                .onCompletion { cause ->
                    if (cause != null) {
                        Logger.e(cause) { "Tracking stopped" }
                    } else {
                        Logger.i("Tracking completed")
                    }

                    trackingJob = null
                }
                .onEach { Logger.d { "Tracking: $it" } }
                .collect { location ->
                    updateState { state ->
                        state.copy(trackingLocation = location)
                    }
                }
        }
    }

    fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null
        updateState { it.copy(tracking = false) }
    }

    data class State(
        val handlePermissions: Boolean = true,
        val geolocator: Geolocator = createGeolocator(handlePermissions),
        val loading: Boolean = false,
        val location: Location? = null,
        val lastOperation: String? = null,
        val lastResult: GeolocatorResult? = null,
        val locationServiceAvailable: Boolean = geolocator.isAvailable(),
        val tracking: Boolean = false,
        val trackingError: String? = null,
        val trackingLocation: Location? = null,
    ) {

        val busy: Boolean = loading || tracking
    }
}