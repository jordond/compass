package dev.jordond.compass.geolocation.mobile.gms.internal

import android.content.Context
import androidx.startup.Initializer
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dev.jordond.compass.geolocation.mobile.LocationProviderRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GmsLocationProviderInitializerTest {

    private val context = mockk<Context>(relaxed = true)
    private val availability = mockk<GoogleApiAvailability>()

    @BeforeTest
    fun setUp() {
        resetRegistry()
        mockkStatic(GoogleApiAvailability::class)
        every { GoogleApiAvailability.getInstance() } returns availability
        every { context.applicationContext } returns context
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
        resetRegistry()
    }

    @Test
    fun playServicesAvailableIsTrueOnlyForSuccess() {
        every { availability.isGooglePlayServicesAvailable(context) } returns ConnectionResult.SUCCESS
        assertTrue(context.playServicesAvailable())

        every { availability.isGooglePlayServicesAvailable(context) } returns
            ConnectionResult.SERVICE_MISSING
        assertFalse(context.playServicesAvailable())

        every { availability.isGooglePlayServicesAvailable(context) } returns
            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED
        assertFalse(context.playServicesAvailable())
    }

    @Test
    fun playServicesAvailableIsFalseWhenTheCheckThrows() {
        every { availability.isGooglePlayServicesAvailable(context) } throws
            IllegalStateException("no play services")

        assertFalse(context.playServicesAvailable())
    }

    @Test
    fun createRegistersAFactoryThatBuildsTheGmsProviderWhenAvailable() {
        every { availability.isGooglePlayServicesAvailable(context) } returns ConnectionResult.SUCCESS

        GmsLocationProviderInitializer().create(context)

        assertIs<GmsLocationProvider>(registeredFactory().create(context))
    }

    @Test
    fun theRegisteredFactoryDeclinesDevicesWithoutPlayServices() {
        every { availability.isGooglePlayServicesAvailable(context) } returns
            ConnectionResult.SERVICE_MISSING

        GmsLocationProviderInitializer().create(context)

        assertNull(registeredFactory().create(context))
    }

    @Test
    fun initializerDeclaresNoDependencies() {
        val dependencies: List<Class<out Initializer<*>>> =
            GmsLocationProviderInitializer().dependencies()

        assertEquals(emptyList(), dependencies)
    }

    // The registry only exposes create to its own module, so read the stored factory directly.
    private fun registeredFactory(): LocationProviderRegistry.Factory =
        LocationProviderRegistry::class.java.getDeclaredField("factory").run {
            isAccessible = true
            get(null) as LocationProviderRegistry.Factory
        }

    private fun resetRegistry() {
        LocationProviderRegistry::class.java.getDeclaredField("factory").apply {
            isAccessible = true
            set(null, null)
        }
    }
}
