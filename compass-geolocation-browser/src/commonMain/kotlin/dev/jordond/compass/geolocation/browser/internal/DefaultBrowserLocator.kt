package dev.jordond.compass.geolocation.browser.internal

import co.touchlab.kermit.Logger
import dev.jordond.compass.Location
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.Priority
import dev.jordond.compass.geolocation.browser.BrowserLocator
import dev.jordond.compass.geolocation.browser.api.model.GeolocationPositionError
import dev.jordond.compass.geolocation.browser.api.model.GeolocationPositionErrorCode.PermissionDenied
import dev.jordond.compass.geolocation.browser.api.model.GeolocationPositionErrorCode.PositionUnavailable
import dev.jordond.compass.geolocation.browser.api.model.GeolocationPositionErrorCode.Timeout
import dev.jordond.compass.geolocation.browser.api.model.createGeolocationOptions
import dev.jordond.compass.geolocation.browser.api.model.value
import dev.jordond.compass.geolocation.browser.api.navigator
import dev.jordond.compass.geolocation.exception.GeolocationException
import dev.jordond.compass.geolocation.exception.PermissionDeniedException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * A [BrowserLocator] implementation that uses the browser's Geolocation API.
 *
 * Docs: [Geolocation API](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation)
 */
internal class DefaultBrowserLocator : BrowserLocator {

    private var trackingId: Int? = null

    private val _locationUpdates = MutableSharedFlow<Location>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val locationUpdates: Flow<Location> = _locationUpdates

    override fun isAvailable(): Boolean {
        return navigator?.geolocation != null
    }

    override suspend fun current(priority: Priority): Location {
        val request = LocationRequest(priority = priority)

        return suspendCoroutine { continuation ->
            navigator?.geolocation
                ?.getCurrentPosition(
                    success = { position -> position?.toModel()?.let(continuation::resume) },
                    error = { error -> continuation.resumeWithException(error.error()) },
                    options = request.toOptions(),
                )
                ?: throw NotSupportedException()
        } ?: throw NotFoundException()
    }

    // TODO: This breaks the throwing on calling track() since we can't get the permission
    // to suspend. Because we would have to track the location twice, once for the permission
    // and once for the actual location.
    // Instead maybe Geolocator should have a Flow<LocationStatus> Instead of a Flow<Location> or
    // a separate flow of tracking status
    override suspend fun track(request: LocationRequest): Flow<Location> {
        if (trackingId != null) return locationUpdates

//        suspendCoroutine { continuation ->
//            navigator?.geolocation
//                ?.getCurrentPosition(
//                    success = { position -> continuation.resume(Unit) },
//                    error = { error -> continuation.resumeWithException(error.error()) },
//                    options = request.toOptions(),
//                )
//        }

        return flow {
            trackingId = navigator?.geolocation?.watchPosition(
                success = { position ->
                    position?.toModel()?.let(_locationUpdates::tryEmit)
                },
                error = { cause ->
                    Logger.e(cause.error()) { "Unable to track location updates" }
                    trackingId = null
                },
                options = request.toOptions(),
            )

            locationUpdates.collect { emit(it) }
        }
    }

    override fun stopTracking() {
        val trackingId = trackingId ?: return
        navigator?.geolocation?.clearWatch(trackingId)
    }

    private fun LocationRequest.toOptions(): Object {
        return createGeolocationOptions(
            enableHighAccuracy = priority == Priority.HighAccuracy,
            maximumAge = maximumAge.toDouble(),
            timeout = Double.POSITIVE_INFINITY,
        )
    }

    private fun GeolocationPositionError.error(): Throwable {
        return when (value()) {
            PermissionDenied -> PermissionDeniedException()
            PositionUnavailable, Timeout -> GeolocationException(message)
        }
    }
}