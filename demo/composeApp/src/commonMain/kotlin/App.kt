import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.GeocoderResult
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var apiKey by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableDoubleStateOf(42.9849) }
    var longitude by rememberSaveable { mutableDoubleStateOf(-81.2453) }
    var loading by rememberSaveable { mutableStateOf(false) }
    var result: GeocoderResult<Place>? by remember { mutableStateOf(null) }

    val geocoder = remember(apiKey) { createGeocoder(apiKey) }

    var start: Boolean? by rememberSaveable { mutableStateOf(null) }
    LaunchedEffect(start) {
        if (start != null && latitude != Double.MAX_VALUE && longitude != Double.MAX_VALUE) {
            loading = true
            result = geocoder.reverse(latitude, longitude)
            loading = false
        }
    }

    MaterialTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text("Reverse Geocoding")
            Text("Enter your API key or leave blank to use the default")
            TextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
            )
            Text("Available: ${geocoder.isAvailable()}")
            Text("Enter latitude and longitude to reverse geocode")
            TextField(
                value = if (latitude == Double.MAX_VALUE) "" else latitude.toString(),
                onValueChange = { latitude = it.toDoubleOrNull() ?: Double.MAX_VALUE },
                label = { Text("Latitude") },
            )
            TextField(
                value = if (longitude == Double.MAX_VALUE) "" else longitude.toString(),
                onValueChange = { longitude = it.toDoubleOrNull() ?: Double.MAX_VALUE },
                label = { Text("Longitude") },
            )
            Button(
                enabled = latitude != Double.MAX_VALUE && longitude != Double.MAX_VALUE && !loading,
                onClick = { start = if (start == null) true else !start!! },
            ) {
                Text("Reverse Geocode")
            }

            if (loading) {
                Text("Loading...")
                CircularProgressIndicator()
            }

            when (result) {
                is GeocoderResult.Error -> Text("Error: $result")
                is GeocoderResult.Success -> Text("Success: ${result?.getFirstOrNull()}")
                null -> {}
            }
        }
    }
}