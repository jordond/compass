package geocoder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import createGeocoder

/**
 * This screen demonstrates if you support Mobile + other platforms, but don't want to fallback
 * to a HTTP based PlatformGeocoder
 */
class PlatformGeocoderScreen : Screen {

    @Composable
    override fun Content() {
        val geocoder = remember { createGeocoder() }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier,
        ) {
            GeocoderView(
                title = "Google Maps",
                geocoder = geocoder,
                apiKey = null,
                updateApiKey = null,
            )
        }
    }
}