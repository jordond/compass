package dev.jordond.compass.geocoder

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.internal.DefaultGeocoder
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope

/**
 * A geocoder over [platform] that runs on the scheduler of the calling test.
 */
internal fun TestScope.geocoder(platform: PlatformGeocoder): Geocoder =
    DefaultGeocoder(platform, StandardTestDispatcher(testScheduler))

internal class FakePlatformGeocoder(
    var available: Boolean = true,
    var onForward: suspend (String) -> List<Coordinates> = { emptyList() },
    var onReverse: suspend (Double, Double) -> List<Place> = { _, _ -> emptyList() },
) : PlatformGeocoder {

    var forwardCalls = 0
    var reverseCalls = 0
    var lastAddress: String? = null
    var lastReverse: Pair<Double, Double>? = null

    override fun isAvailable(): Boolean = available

    override suspend fun forward(address: String): List<Coordinates> {
        forwardCalls++
        lastAddress = address
        return onForward(address)
    }

    override suspend fun reverse(latitude: Double, longitude: Double): List<Place> {
        reverseCalls++
        lastReverse = latitude to longitude
        return onReverse(latitude, longitude)
    }
}
