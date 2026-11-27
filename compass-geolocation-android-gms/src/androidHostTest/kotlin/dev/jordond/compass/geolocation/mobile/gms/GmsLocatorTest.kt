package dev.jordond.compass.geolocation.mobile.gms

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.tools.ContextProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GmsLocatorTest {

    private val context = mockk<Context>(relaxed = true)
    private val availability = mockk<GoogleApiAvailability>()
    private val permissions = mockk<LocationPermissionController>()

    @BeforeTest
    fun setUp() {
        resetContextProvider()
        mockkStatic(GoogleApiAvailability::class)
        every { GoogleApiAvailability.getInstance() } returns availability
        every { context.applicationContext } returns context
        ContextProvider.create(context)
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
        resetContextProvider()
    }

    @Test
    fun isPlayServicesAvailableReflectsTheDevice() {
        every { availability.isGooglePlayServicesAvailable(context) } returns ConnectionResult.SUCCESS
        assertTrue(isPlayServicesAvailable())

        every { availability.isGooglePlayServicesAvailable(context) } returns
            ConnectionResult.SERVICE_DISABLED
        assertFalse(isPlayServicesAvailable())
    }

    @Test
    fun gmsLocatorFailsFastWhenPlayServicesIsMissing() {
        every { availability.isGooglePlayServicesAvailable(context) } returns
            ConnectionResult.SERVICE_MISSING

        val error = assertFailsWith<IllegalStateException> { GmsLocator(permissions) }

        assertTrue(error.message.orEmpty().contains("Google Play Services is not available"))
    }

    @Test
    fun gmsLocatorIsCreatedWhenPlayServicesIsAvailable() {
        every { availability.isGooglePlayServicesAvailable(context) } returns ConnectionResult.SUCCESS

        assertNotNull(GmsLocator(permissions))
    }

    @Test
    fun gmsGeolocatorFailsFastWhenPlayServicesIsMissing() {
        every { availability.isGooglePlayServicesAvailable(context) } returns
            ConnectionResult.SERVICE_MISSING

        assertFailsWith<IllegalStateException> { GmsGeolocator(permissions) }
    }

    @Test
    fun failsWithAClearErrorWhenTheContextWasNeverInitialized() {
        resetContextProvider()

        assertEquals(
            "ContextProvider has not been initialized",
            assertFailsWith<IllegalStateException> { GmsLocator(permissions) }.message,
        )
    }

    private fun resetContextProvider() {
        ContextProvider::class.java.getDeclaredField("instance").apply {
            isAccessible = true
            set(null, null)
        }
    }
}
