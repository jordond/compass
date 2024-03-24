package geocoder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import createGeocoder

/**
 * This screen demonstrates how to support Mobile with its native geocoder, as well as falling back
 * to an http based geocoder on other platforms.
 */
class PlatformGeocoderFallbackScreen : Screen {

    @Composable
    override fun Content() {
        var apiKey by remember { mutableStateOf("") }
        val geocoder by remember(apiKey) {
            derivedStateOf { createGeocoder(apiKey) }
        }

        GeocoderContent(
            title = "Google Maps",
            geocoder = geocoder,
            apiKey = apiKey,
            updateApiKey = { apiKey = it },
        )
    }
}