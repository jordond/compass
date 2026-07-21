package dev.jordond.compass.geolocation.mobile.internal

import dev.jordond.compass.Location
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.mobile.MobileLocator
import kotlinx.coroutines.CancellationException

internal expect fun currentTimeMillis(): Long

/**
 * The last known location, if it is recent enough to satisfy [LocationRequest.maximumAge].
 *
 * Mirrors the semantics of the W3C Geolocation API's `PositionOptions.maximumAge`, which the
 * parameter is modelled on. Age is the only criteria, the accuracy of the cached fix is not
 * considered, and the default of `0` never serves one.
 *
 * Returns `null` when there is nothing cached, when what is cached is too old, or when the platform
 * failed to produce it. In every one of those cases the caller should go on to request a fresh fix,
 * so a failure here must not fail the request as a whole.
 */
internal suspend fun MobileLocator.cachedLocationOrNull(request: LocationRequest): Location? {
    if (request.maximumAge <= 0L) return null

    val cached = try {
        lastLocation(request.priority)
    } catch (cause: CancellationException) {
        throw cause
    } catch (_: Throwable) {
        null
    } ?: return null

    // A fix stamped in the future means the clocks disagree, so it cannot be trusted as recent.
    val age = currentTimeMillis() - cached.timestampMillis
    return cached.takeIf { age in 0L..request.maximumAge }
}
