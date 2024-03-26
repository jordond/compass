package geocoder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import dev.jordond.compass.geocoder.web.HttpApiPlatformGeocoder
import dev.jordond.compass.geocoder.web.MapBoxGeocoder

/**
 * This screen demonstrates how to use the Mapbox Geocoder for all platforms.
 */
class MapboxGeocoderScreen : Screen {

    @Composable
    override fun Content() {
        var apiKey by rememberSaveable { mutableStateOf("") }
        val geocoder by remember(apiKey) {
            derivedStateOf {
                MapBoxGeocoder(
                    apiKey = apiKey,
                    client = HttpApiPlatformGeocoder.httpClient(enableLogging = true)
                )
            }
        }

        GeocoderContent(
            title = "Mapbox",
            geocoder = geocoder,
            apiKey = apiKey,
            updateApiKey = { apiKey = it },
        )
    }
}