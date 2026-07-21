package dev.jordond.compass.geolocation.mobile.internal

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.mobile.MobileLocator
import dev.jordond.compass.permissions.exception.PermissionDeniedForeverException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame

private const val NOW = 1_000_000L

class CachedLocationTest {

    @Test
    fun neverServesACachedFixWhenMaximumAgeIsZero() = runTest {
        val locator = FakeMobileLocator { locationAt(NOW) }

        assertNull(locator.cachedLocationOrNull(request(maximumAge = 0)) { NOW })

        // Not just the wrong answer, the question is never asked. `maximumAge` defaults to zero, so
        // this is what keeps `current()` off the last known location for everyone who never set it.
        assertEquals(0, locator.lastLocationCalls)
    }

    @Test
    fun servesAFixYoungerThanMaximumAge() = runTest {
        val cached = locationAt(NOW - 4_000)
        val locator = FakeMobileLocator { cached }

        assertSame(cached, locator.cachedLocationOrNull(request(maximumAge = 5_000)) { NOW })
    }

    @Test
    fun servesAFixExactlyAtMaximumAge() = runTest {
        val cached = locationAt(NOW - 5_000)
        val locator = FakeMobileLocator { cached }

        assertSame(cached, locator.cachedLocationOrNull(request(maximumAge = 5_000)) { NOW })
    }

    @Test
    fun rejectsAFixOlderThanMaximumAge() = runTest {
        val locator = FakeMobileLocator { locationAt(NOW - 5_001) }

        assertNull(locator.cachedLocationOrNull(request(maximumAge = 5_000)) { NOW })
    }

    @Test
    fun rejectsAFixStampedInTheFuture() = runTest {
        val locator = FakeMobileLocator { locationAt(NOW + 1) }

        assertNull(locator.cachedLocationOrNull(request(maximumAge = 5_000)) { NOW })
    }

    @Test
    fun rejectsWhenNothingIsCached() = runTest {
        val locator = FakeMobileLocator { null }

        assertNull(locator.cachedLocationOrNull(request(maximumAge = 5_000)) { NOW })
    }

    @Test
    fun countsTimeSpentReadingTheCachedFixTowardsItsAge() = runTest {
        // Reading the last known location is not instant, it goes through the permission gate and
        // the platform. A clock sampled before that read would call a stale fix fresh.
        var clock = NOW
        val locator = FakeMobileLocator {
            clock += 5_000
            locationAt(NOW)
        }

        assertNull(locator.cachedLocationOrNull(request(maximumAge = 4_999)) { clock })
    }

    @Test
    fun fallsThroughWhenTheCachedFixCannotBeRead() = runTest {
        val locator = FakeMobileLocator { throw NotFoundException() }

        assertNull(locator.cachedLocationOrNull(request(maximumAge = 5_000)) { NOW })
    }

    @Test
    fun propagatesAMissingPermission() = runTest {
        val locator = FakeMobileLocator { throw PermissionDeniedForeverException() }

        // A live request would fail the same way, so swallowing this would only put the caller
        // through the permission gate a second time to reach the same answer.
        assertFailsWith<PermissionDeniedForeverException> {
            locator.cachedLocationOrNull(request(maximumAge = 5_000)) { NOW }
        }
    }

    @Test
    fun propagatesCancellation() = runTest {
        val locator = FakeMobileLocator { throw CancellationException("cancelled") }

        assertFailsWith<CancellationException> {
            locator.cachedLocationOrNull(request(maximumAge = 5_000)) { NOW }
        }
    }

    private fun request(maximumAge: Long) = LocationRequest(maximumAge = maximumAge)

    private fun locationAt(timestampMillis: Long) = Location(
        coordinates = Coordinates(latitude = 0.0, longitude = 0.0),
        accuracy = 0.0,
        azimuth = null,
        speed = null,
        mslAltitude = null,
        ellipsoidalAltitude = null,
        timestampMillis = timestampMillis,
    )
}

private class FakeMobileLocator(
    private val lastLocation: () -> Location?,
) : MobileLocator {

    var lastLocationCalls: Int = 0
        private set

    override suspend fun lastLocation(priority: Priority): Location? {
        lastLocationCalls++
        return lastLocation.invoke()
    }

    override val locationUpdates: Flow<Location> = emptyFlow()

    override suspend fun isAvailable(): Boolean = true

    override fun hasPermission(): Boolean = true

    override suspend fun current(priority: Priority): Location =
        error("cachedLocationOrNull must not request a fresh location")

    override suspend fun track(request: LocationRequest): Flow<Location> =
        error("cachedLocationOrNull must not start tracking")

    override fun stopTracking(): Unit =
        error("cachedLocationOrNull must not stop tracking")
}
