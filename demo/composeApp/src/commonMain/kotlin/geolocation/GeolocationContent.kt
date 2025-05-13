package geolocation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

@Composable
fun GeolocationContent(
    state: GeolocationModel.State,
    currentLocation: () -> Unit,
    startTracking: () -> Unit,
    stopTracking: () -> Unit,
    getLastLocation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Text("Platform Geolocation Screen")
        Text("Location services available: ${state.locationServiceAvailable}")
        Text("Last Location: ${state.location}")
        Text("Is Loading: ${state.loading}")
        Text("Last operation result: ${state.lastResult ?: "N/A"}")
        CircularProgressIndicator(Modifier.alpha(if (state.loading) 1f else 0f))
        Button(enabled = !state.busy, onClick = currentLocation) {
            Text("Get Current location")
        }

        Spacer(modifier = Modifier.height(64.dp))

        Text("Location Tracker")
        Text("Active: ${state.tracking}")
        Text("Last update: ${state.trackingLocation}")
        Text("Tracking error: ${state.trackingError ?: "N/A"}")

        Row {
            Button(enabled = !state.tracking, onClick = startTracking) {
                Text("Start")
            }
            Button(enabled = state.tracking, onClick = stopTracking) {
                Text("Stop")
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        Text("Last Location")
        Text("Last known location: ${state.lastLocation}")

        Row {
            Button(onClick = getLastLocation) {
                Text("Get Last Location")
            }
        }
    }
}