package dev.jordond.compass.geolocation.mobile.gms

import android.content.Context
import android.location.Location
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.jordond.compass.Priority
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState
import dev.jordond.compass.tools.ContextProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GmsLocatorTest {

    private val context = mockk<Context>()
    private val permissions = mockk<LocationPermissionController>()

    @BeforeTest
    fun setUp() {
        mockkObject(ContextProvider)
        every { ContextProvider.getInstance() } returns ContextProvider(context)
        every { context.applicationContext } returns context
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun isPlayServicesAvailableChecksTheApplicationContext() {
        val availability = stubPlayServices(ConnectionResult.SUCCESS)

        assertTrue(isPlayServicesAvailable())
        verify(exactly = 1) { availability.isGooglePlayServicesAvailable(context) }
    }

    @Test
    fun gmsLocatorFailsFastWhenPlayServicesIsMissing() {
        stubPlayServices(ConnectionResult.SERVICE_MISSING)

        val error = assertFailsWith<IllegalStateException> { GmsLocator(permissions) }

        assertContains(error.message.orEmpty(), "Google Play Services is not available")
    }

    @Test
    fun gmsLocatorReadsLocationsFromTheFusedClient() = runTest {
        val fused = mockk<FusedLocationProviderClient>()
        stubPlayServices(ConnectionResult.SUCCESS)
        mockkStatic(LocationServices::class)
        every { LocationServices.getFusedLocationProviderClient(context) } returns fused
        every { fused.lastLocation } returns fakeTask<Location>(result = null)
        coEvery { permissions.requirePermissionFor(any()) } returns PermissionState.Granted

        assertNull(GmsLocator(permissions).lastLocation(Priority.Balanced))

        verify(exactly = 1) { fused.lastLocation }
    }
}
