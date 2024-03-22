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

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        MaterialTheme {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text("Geocoder")
                NavButton("Platform", onClick = { })
                NavButton("Platform (Http fallback)", onClick = { })
                NavButton("Google Maps", onClick = { })
                NavButton("Mapbox", onClick = { })

                HorizontalDivider()

                Text("Geolocation")
                NavButton("Platform", onClick = { })
                NavButton("Platform (Http fallback)", onClick = { })
                NavButton("Google Maps", onClick = { })
            }
        }
    }
}

@Composable
private fun NavButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text)
    }
}