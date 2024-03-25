package geolocation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.Locator
import dev.jordond.compass.geolocation.browser.browser
import dev.stateholder.extensions.collectAsState

actual class BrowserGeolocationScreen actual constructor(): Screen {

    @Composable
    override fun Content() {
        val model = rememberScreenModel<GeolocationModel> {
            GeolocationModel(Geolocator(Locator.browser()))
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