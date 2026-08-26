package dev.jordond.compass.geolocation.mobile.internal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import dev.jordond.compass.Priority
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.geolocation.mobile.PlatformLocationProvider
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import dev.jordond.compass.geolocation.LocationRequest as CompassLocationRequest

/**
 * A [PlatformLocationProvider] built on the platform `LocationManager`.
 */
internal class StandardLocationProvider(
    context: Context,
) : PlatformLocationProvider {

    private val context: Context = context.applicationContext

    private val locationManager =
        this.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val executor = ContextCompat.getMainExecutor(this.context)

    private val _locationUpdates = MutableSharedFlow<Location>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val locationUpdates: Flow<Location> = _locationUpdates

    private var session: TrackingSession? = null

    override suspend fun lastLocation(): Location? {
        // Unlike the fused client there is no single source to ask, so take the freshest fix any
        // enabled provider is holding. elapsedRealtimeNanos is monotonic, Location.time is not.
        return locationManager.getProviders(true)
            .mapNotNull { provider -> locationManager.getLastKnownLocation(provider) }
            .maxByOrNull { location -> location.elapsedRealtimeNanos }
    }

    override suspend fun locationEnabled(): Boolean =
        LocationManagerCompat.isLocationEnabled(locationManager)

    override suspend fun currentLocation(priority: Priority): Location {
        val provider = bestProvider(priority) ?: throw NotFoundException()

        return suspendCancellableCoroutine { continuation ->
            val cancellationSignal = CancellationSignal()
            continuation.invokeOnCancellation { cancellationSignal.cancel() }

            LocationManagerCompat.getCurrentLocation(
                locationManager,
                provider,
                cancellationSignal,
                executor,
            ) { location ->
                if (location == null) continuation.resumeWithException(NotFoundException())
                else continuation.resume(location)
            }
        }
    }

    override fun startTracking(request: CompassLocationRequest) {
        if (session != null) return

        val session = TrackingSession(request)
        session.start()
        this.session = session
    }

    override fun stopTracking() {
        session?.stop()
        session = null
    }

    /**
     * Keeps a listener registered on the best provider for [request] for as long as tracking runs.
     *
     * A single registration is not enough on its own. Unlike the fused client, a `LocationManager`
     * listener is bound to one named provider, and turning that provider off, or turning location
     * off entirely, ends the stream for good. So the session watches for those changes and
     * re-registers on whatever provider is best once they settle.
     */
    private inner class TrackingSession(private val request: CompassLocationRequest) {

        private var registeredProvider: String? = null

        private val listener = object : LocationListenerCompat {

            override fun onLocationChanged(location: Location) {
                _locationUpdates.tryEmit(location)
            }

            override fun onProviderEnabled(provider: String) {
                refresh()
            }

            override fun onProviderDisabled(provider: String) {
                refresh()
            }
        }

        private val settingsReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                refresh()
            }
        }

        /**
         * @throws NotFoundException If no provider can serve the request right now.
         */
        fun start() {
            if (!register()) throw NotFoundException()
            registerSettingsReceiver()
        }

        fun stop() {
            runCatching { context.unregisterReceiver(settingsReceiver) }
            unregister()
        }

        private fun refresh() {
            runCatching { register() }
        }

        private fun register(): Boolean {
            val provider = bestProvider(request.priority)
            if (provider == null) {
                unregister()
                return false
            }
            if (provider == registeredProvider) return true

            unregister()
            LocationManagerCompat.requestLocationUpdates(
                locationManager,
                provider,
                LocationRequestCompat.Builder(request.interval)
                    .setMinUpdateIntervalMillis(request.interval)
                    .setQuality(request.priority.toQuality())
                    .build(),
                executor,
                listener,
            )
            registeredProvider = provider
            return true
        }

        private fun unregister() {
            if (registeredProvider == null) return
            LocationManagerCompat.removeUpdates(locationManager, listener)
            registeredProvider = null
        }

        private fun registerSettingsReceiver() {
            val filter = IntentFilter().apply {
                addAction(LocationManager.MODE_CHANGED_ACTION)
                addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
            }

            ContextCompat.registerReceiver(
                context,
                settingsReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
        }
    }

    private fun bestProvider(priority: Priority): String? = selectProvider(
        priority = priority,
        locationEnabled = LocationManagerCompat.isLocationEnabled(locationManager),
        enabledProviders = locationManager.getProviders(true),
    )
}
