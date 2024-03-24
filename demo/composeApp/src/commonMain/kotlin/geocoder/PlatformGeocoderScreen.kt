package geocoder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

        GeocoderContent(
            title = "Google Maps",
            geocoder = geocoder,
            apiKey = null,
            updateApiKey = null,
        )
    }
}