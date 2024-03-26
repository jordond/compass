package geocoder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.GeocoderResult
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun GeocoderContent(
    title: String,
    geocoder: Geocoder,
    apiKey: String? = "",
    updateApiKey: ((String) -> Unit)? = {},
) {
    var latitude by rememberSaveable { mutableDoubleStateOf(51.509865) }
    var longitude by rememberSaveable { mutableDoubleStateOf(-0.118092) }
    var loading by rememberSaveable { mutableStateOf(false) }
    var result: GeocoderResult<Place>? by remember { mutableStateOf(null) }

    var start: Boolean? by rememberSaveable { mutableStateOf(null) }
    LaunchedEffect(start) {
        if (start != null && latitude != Double.MAX_VALUE && longitude != Double.MAX_VALUE) {
            loading = true
            result = geocoder.reverse(latitude, longitude)
            loading = false
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Text("Reverse Geocoding - $title")
        if (apiKey != null && updateApiKey != null) {
            Text("Enter your API key or leave blank to use the default")
            TextField(
                value = apiKey,
                onValueChange = updateApiKey,
                label = { Text("API Key") },
            )
        }
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