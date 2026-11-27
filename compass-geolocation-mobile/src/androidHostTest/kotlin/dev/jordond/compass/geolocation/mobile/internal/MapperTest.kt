package dev.jordond.compass.geolocation.mobile.internal

import android.content.Context
import android.location.Location
import android.os.Build.VERSION_CODES
import androidx.core.location.LocationCompat
import androidx.core.location.altitude.AltitudeConverterCompat
import dev.jordond.compass.Altitude
import dev.jordond.compass.Azimuth
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Speed
import dev.jordond.compass.geolocation.mobile.mockPlatformLocation
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private const val LEGACY = VERSION_CODES.N

// Every test maps a strict source mock, so each one also proves that nothing is read from the
// source once the defensive copy exists. Unstubbed accuracy getters throw, so the tests below
// API 26 also prove those getters are never called there.
class MapperTest {

    private val context = mockk<Context>()
    private lateinit var source: Location

    @BeforeTest
    fun setUp() {
        mockkStatic(AltitudeConverterCompat::class)
        source = mockPlatformLocation(
            latitude = 43.65,
            longitude = -79.38,
            accuracy = 12.5f,
            timeMillis = 1_700_000_000_000L,
        )
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun mapsTheBasicFields() = runTest {
        val model = source.toModel(context, LEGACY)

        assertEquals(Coordinates(43.65, -79.38), model.coordinates)
        assertEquals(12.5, model.accuracy)
        assertEquals(1_700_000_000_000L, model.timestampMillis)
    }

    @Test
    fun leavesOptionalMeasurementsNullWhenTheFixLacksThem() = runTest {
        val model = source.toModel(context, VERSION_CODES.O)

        assertNull(model.azimuth)
        assertNull(model.speed)
        assertNull(model.mslAltitude)
        assertNull(model.ellipsoidalAltitude)
    }

    @Test
    fun mapsBearingSpeedAndAltitudeWithoutAccuraciesBelowApi26() = runTest {
        stubBearingSpeedAndAltitude()

        val model = source.toModel(context, LEGACY)

        assertEquals(Azimuth(degrees = 90f, accuracy = null), model.azimuth)
        assertEquals(Speed(mps = 3.5f, accuracy = null), model.speed)
        assertEquals(Altitude(meters = 76.0, accuracy = null), model.ellipsoidalAltitude)
    }

    @Test
    fun mapsTheAccuraciesFromApi26() = runTest {
        stubBearingSpeedAndAltitude()
        every { anyConstructed<Location>().hasBearingAccuracy() } returns true
        every { anyConstructed<Location>().bearingAccuracyDegrees } returns 5f
        every { anyConstructed<Location>().hasSpeedAccuracy() } returns true
        every { anyConstructed<Location>().speedAccuracyMetersPerSecond } returns 0.5f
        every { anyConstructed<Location>().hasVerticalAccuracy() } returns true
        every { anyConstructed<Location>().verticalAccuracyMeters } returns 1.5f

        val model = source.toModel(context, VERSION_CODES.O)

        assertEquals(Azimuth(degrees = 90f, accuracy = 5f), model.azimuth)
        assertEquals(Speed(mps = 3.5f, accuracy = 0.5f), model.speed)
        assertEquals(Altitude(meters = 76.0, accuracy = 1.5f), model.ellipsoidalAltitude)
    }

    @Test
    fun omitsTheAccuraciesAFixDoesNotReportFromApi26() = runTest {
        stubBearingSpeedAndAltitude()
        every { anyConstructed<Location>().hasBearingAccuracy() } returns false
        every { anyConstructed<Location>().hasSpeedAccuracy() } returns false
        every { anyConstructed<Location>().hasVerticalAccuracy() } returns false

        val model = source.toModel(context, VERSION_CODES.O)

        assertEquals(Azimuth(degrees = 90f, accuracy = null), model.azimuth)
        assertEquals(Speed(mps = 3.5f, accuracy = null), model.speed)
        assertEquals(Altitude(meters = 76.0, accuracy = null), model.ellipsoidalAltitude)
    }

    @Test
    fun addsTheMeanSeaLevelAltitudeWhenTheFixHasAnAltitude() = runTest {
        stubBearingSpeedAndAltitude()
        every { LocationCompat.hasMslAltitude(any()) } returns true
        every { LocationCompat.getMslAltitudeMeters(any()) } returns 40.0
        every { LocationCompat.hasMslAltitudeAccuracy(any()) } returns true
        every { LocationCompat.getMslAltitudeAccuracyMeters(any()) } returns 2.0f

        val model = source.toModel(context, LEGACY)

        verify(exactly = 1) { AltitudeConverterCompat.addMslAltitudeToLocation(context, any()) }
        assertEquals(Altitude(meters = 40.0, accuracy = 2.0f), model.mslAltitude)
    }

    @Test
    fun omitsTheMeanSeaLevelAccuracyWhenUnavailable() = runTest {
        every { LocationCompat.hasMslAltitude(any()) } returns true
        every { LocationCompat.getMslAltitudeMeters(any()) } returns 40.0
        every { LocationCompat.hasMslAltitudeAccuracy(any()) } returns false

        val altitude = source.toModel(context, LEGACY).mslAltitude

        assertEquals(Altitude(meters = 40.0, accuracy = null), altitude)
    }

    @Test
    fun skipsTheMeanSeaLevelConversionWithoutAnAltitude() = runTest {
        source.toModel(context, LEGACY)

        verify(exactly = 0) { AltitudeConverterCompat.addMslAltitudeToLocation(any(), any()) }
    }

    @Test
    fun stillMapsWhenTheMeanSeaLevelConversionFails() = runTest {
        stubBearingSpeedAndAltitude()
        every { AltitudeConverterCompat.addMslAltitudeToLocation(any(), any()) } throws
            java.io.IOException("no geoid data")

        val model = source.toModel(context, LEGACY)

        assertNotNull(model.ellipsoidalAltitude)
        assertNull(model.mslAltitude)
    }

    private fun stubBearingSpeedAndAltitude() {
        every { anyConstructed<Location>().hasBearing() } returns true
        every { anyConstructed<Location>().bearing } returns 90f
        every { anyConstructed<Location>().hasSpeed() } returns true
        every { anyConstructed<Location>().speed } returns 3.5f
        every { anyConstructed<Location>().hasAltitude() } returns true
        every { anyConstructed<Location>().altitude } returns 76.0
        every { AltitudeConverterCompat.addMslAltitudeToLocation(any(), any()) } returns Unit
    }
}
