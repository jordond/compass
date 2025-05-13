package geolocation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import canShowAppSettings
import createGeolocator
import dev.stateholder.extensions.collectAsState
import kotlinx.coroutines.launch
import showAppSettings

class PlatformGeolocationScreen : Screen {

    @Composable
    override fun Content() {
        val model = rememberScreenModel<GeolocationModel> {
            GeolocationModel(createGeolocator())
        }
        val state by model.collectAsState()

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        if (canShowAppSettings) {
            LaunchedEffect(state.permissionsDeniedForever) {
                if (state.permissionsDeniedForever) {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Location permission is required to use this feature",
                            actionLabel = "Open Settings",
                            duration = SnackbarDuration.Long,
                        )

                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                showAppSettings()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { innerPadding ->
            GeolocationContent(
                state = state,
                currentLocation = model::currentLocation,
                startTracking = model::startTracking,
                stopTracking = model::stopTracking,
                getLastLocation = model::getLastLocation,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}