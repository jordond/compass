package dev.jordond.compass.geolocation.mobile.internal

import android.content.Context
import android.location.Location
import androidx.core.location.LocationCompat
import androidx.core.location.altitude.AltitudeConverterCompat
import dev.jordond.compass.Altitude
import dev.jordond.compass.Coordinates
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
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

// Every Location in these tests shares one constructor mock, so the platform source and its
// defensive copy answer the same. SDK_INT is 0 on the host, so the API 26 accuracy values are
// always reported as absent here.
class MapperTest {

    private val context = mockk<Context>()

    @BeforeTest
    fun setUp() {
        mockkConstructor(Location::class)
        mockkStatic(LocationCompat::class)
        mockkStatic(AltitudeConverterCompat::class)
        every { anyConstructed<Location>().latitude } returns 43.65
        every { anyConstructed<Location>().longitude } returns -79.38
        every { anyConstructed<Location>().accuracy } returns 12.5f
        every { anyConstructed<Location>().time } returns 1_700_000_000_000L
        every { anyConstructed<Location>().hasBearing() } returns false
        every { anyConstructed<Location>().hasSpeed() } returns false
        every { anyConstructed<Location>().hasAltitude() } returns false
        every { LocationCompat.hasMslAltitude(any()) } returns false
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun mapsTheBasicFields() = runTest {
        val model = Location("gps").toModel(context)

        assertEquals(Coordinates(43.65, -79.38), model.coordinates)
        assertEquals(12.5, model.accuracy)
        assertEquals(1_700_000_000_000L, model.timestampMillis)
    }

    @Test
    fun leavesOptionalMeasurementsNullWhenTheFixLacksThem() = runTest {
        val model = Location("gps").toModel(context)

        assertNull(model.azimuth)
        assertNull(model.speed)
        assertNull(model.mslAltitude)
        assertNull(model.ellipsoidalAltitude)
    }

    @Test
    fun mapsBearingAndSpeedWhenPresent() = runTest {
        every { anyConstructed<Location>().hasBearing() } returns true
        every { anyConstructed<Location>().bearing } returns 90f
        every { anyConstructed<Location>().hasSpeed() } returns true
        every { anyConstructed<Location>().speed } returns 3.5f

        val model = Location("gps").toModel(context)

        assertEquals(90f, model.azimuth?.degrees)
        assertEquals(3.5f, model.speed?.mps)
    }

    @Test
    fun mapsTheEllipsoidalAltitude() = runTest {
        every { anyConstructed<Location>().hasAltitude() } returns true
        every { anyConstructed<Location>().altitude } returns 76.0
        every { AltitudeConverterCompat.addMslAltitudeToLocation(any(), any()) } returns Unit

        val model = Location("gps").toModel(context)

        assertEquals(76.0, model.ellipsoidalAltitude?.meters)
    }

    @Test
    fun addsTheMeanSeaLevelAltitudeWhenTheFixHasAnAltitude() = runTest {
        every { anyConstructed<Location>().hasAltitude() } returns true
        every { anyConstructed<Location>().altitude } returns 76.0
        every { AltitudeConverterCompat.addMslAltitudeToLocation(any(), any()) } returns Unit
        every { LocationCompat.hasMslAltitude(any()) } returns true
        every { LocationCompat.getMslAltitudeMeters(any()) } returns 40.0
        every { LocationCompat.hasMslAltitudeAccuracy(any()) } returns true
        every { LocationCompat.getMslAltitudeAccuracyMeters(any()) } returns 2.0f

        val model = Location("gps").toModel(context)

        verify(exactly = 1) { AltitudeConverterCompat.addMslAltitudeToLocation(context, any()) }
        assertEquals(Altitude(meters = 40.0, accuracy = 2.0f), model.mslAltitude)
    }

    @Test
    fun omitsTheMeanSeaLevelAccuracyWhenUnavailable() = runTest {
        every { LocationCompat.hasMslAltitude(any()) } returns true
        every { LocationCompat.getMslAltitudeMeters(any()) } returns 40.0
        every { LocationCompat.hasMslAltitudeAccuracy(any()) } returns false

        val altitude = Location("gps").toModel(context).mslAltitude

        assertEquals(40.0, altitude?.meters)
        assertNull(altitude?.accuracy)
    }

    @Test
    fun skipsTheMeanSeaLevelConversionWithoutAnAltitude() = runTest {
        Location("gps").toModel(context)

        verify(exactly = 0) { AltitudeConverterCompat.addMslAltitudeToLocation(any(), any()) }
    }

    @Test
    fun stillMapsWhenTheMeanSeaLevelConversionFails() = runTest {
        every { anyConstructed<Location>().hasAltitude() } returns true
        every { anyConstructed<Location>().altitude } returns 76.0
        every { AltitudeConverterCompat.addMslAltitudeToLocation(any(), any()) } throws
            java.io.IOException("no geoid data")

        val model = Location("gps").toModel(context)

        assertNotNull(model.ellipsoidalAltitude)
        assertNull(model.mslAltitude)
    }
}
