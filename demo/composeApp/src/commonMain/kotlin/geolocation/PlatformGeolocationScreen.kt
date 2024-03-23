package geolocation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import dev.stateholder.extensions.collectAsState

class PlatformGeolocationScreen : Screen {

    @Composable
    override fun Content() {
        val model = rememberScreenModel<PlatformGeolocationModel> { PlatformGeolocationModel() }
        val state by model.collectAsState()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text("Platform Geolocation Screen")
            Row {
                Checkbox(
                    checked = state.handlePermissions,
                    onCheckedChange = { model.toggleHandlePermissions() }
                )
                Text("Automatically handle required permissions")
            }

            Text("Location services available: ${state.locationServiceAvailable}")

            Text("Last Location: ${state.location}")
            Text("Is Loading: ${state.loading}")
            Text("Last operation result: ${state.lastResult ?: "N/A"}")
            CircularProgressIndicator(Modifier.alpha(if (state.loading) 1f else 0f))
            Button(enabled = !state.busy, onClick = { model.currentLocation() }) {
                Text("Get Current location")
            }

            Spacer(modifier = Modifier.height(64.dp))

            Text("Location Tracker")
            Text("Active: ${state.tracking}")
            Text("Last update: ${state.trackingLocation ?: "N/A"}")
            Text("Tracking error: ${state.trackingError ?: "N/A"}")

            Row {
                Button(enabled = !state.tracking, onClick = { model.startTracking() }) {
                    Text("Start")
                }
                Button(enabled = state.tracking, onClick = { model.stopTracking() }) {
                    Text("Stop")
                }
            }
        }
    }
}