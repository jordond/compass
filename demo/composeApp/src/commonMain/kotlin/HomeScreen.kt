import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import geocoder.GoogleMapsGeocoderScreen
import geocoder.MapboxGeocoderScreen
import geocoder.OpenCageGeocoderScreen
import geocoder.PlatformGeocoderFallbackScreen
import geocoder.PlatformGeocoderScreen
import geolocation.BrowserGeolocationScreen
import geolocation.PlatformGeolocationScreen

class HomeScreen(private val isBrowser: Boolean = false) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        MaterialTheme {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text("Geocoder")
                NavButton("Platform") {
                    navigator.push(PlatformGeocoderScreen())
                }
                NavButton("Platform (Http fallback)") {
                    navigator.push(PlatformGeocoderFallbackScreen())
                }
                NavButton("Google Maps") {
                    navigator.push(GoogleMapsGeocoderScreen())
                }
                NavButton("Mapbox") {
                    navigator.push(MapboxGeocoderScreen())
                }
                NavButton("OpenCage") {
                    navigator.push(OpenCageGeocoderScreen())
                }

                HorizontalDivider(Modifier.padding(vertical = 32.dp))

                Text("Geolocation")
                NavButton("Platform") {
                    navigator.push(PlatformGeolocationScreen())
                }
                if (isBrowser) {
                    NavButton("Browser Only") {
                        navigator.push(BrowserGeolocationScreen())
                    }
                }
            }
        }
    }
}

@Composable
private fun NavButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text)
    }
}