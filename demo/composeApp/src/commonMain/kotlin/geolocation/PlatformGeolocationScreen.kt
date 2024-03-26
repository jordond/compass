package geolocation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import createGeolocator
import dev.stateholder.extensions.collectAsState

class PlatformGeolocationScreen : Screen {

    @Composable
    override fun Content() {
        val model = rememberScreenModel<GeolocationModel> {
            GeolocationModel(createGeolocator(handlePermissions = true))
        }
        val state by model.collectAsState()

        GeolocationContent(
            state = state,
            currentLocation = model::currentLocation,
            startTracking = model::startTracking,
            stopTracking = model::stopTracking,
        )
    }
}