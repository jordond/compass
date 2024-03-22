package geocoder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier,
        ) {
            GeocoderView(
                title = "Google Maps",
                geocoder = geocoder,
                apiKey = apiKey,
                updateApiKey = { apiKey = it },
            )
        }
    }
}