package geocoder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import dev.jordond.compass.geocoder.web.GoogleMapsGeocoder
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsLocationType
import dev.jordond.compass.tools.web.HttpApiEndpoint

/**
 * This screen demonstrates how to use the Google Maps Geocoder for all platforms.
 */
class GoogleMapsGeocoderScreen : Screen {

    @Composable
    override fun Content() {
        var apiKey by rememberSaveable { mutableStateOf("") }
        val geocoder by remember(apiKey) {
            derivedStateOf {
                GoogleMapsGeocoder(
                    apiKey = apiKey,
                    client = HttpApiEndpoint.httpClient(enableLogging = true)
                ) {
                    locationType(GoogleMapsLocationType.Approximate)
                }
            }
        }

        GeocoderContent(
            title = "Google Maps",
            geocoder = geocoder,
            apiKey = apiKey,
            updateApiKey = { apiKey = it },
        )
    }
}