package dev.jordond.compass.geolocation.mobile.gms.internal

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import dev.jordond.compass.geolocation.mobile.LocationProviderRegistry
import dev.jordond.compass.geolocation.mobile.gms.stubPlayServices
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GmsLocationProviderInitializerTest {

    private val context = mockk<Context>()
    private val registered = slot<LocationProviderRegistry.Factory>()

    @BeforeTest
    fun setUp() {
        mockkObject(LocationProviderRegistry)
        every { LocationProviderRegistry.register(capture(registered)) } just Runs
        every { context.applicationContext } returns context
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun playServicesAvailableIsTrueOnlyForSuccess() {
        stubPlayServices(ConnectionResult.SUCCESS)
        assertTrue(context.playServicesAvailable())

        val unusable = listOf(
            ConnectionResult.SERVICE_MISSING,
            ConnectionResult.SERVICE_DISABLED,
            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,
        )
        for (result in unusable) {
            stubPlayServices(result)
            assertFalse(context.playServicesAvailable(), "available for connection result $result")
        }
    }

    @Test
    fun playServicesAvailableIsFalseWhenTheCheckThrows() {
        val availability = stubPlayServices(ConnectionResult.SUCCESS)
        every { availability.isGooglePlayServicesAvailable(context) } throws
            IllegalStateException("no play services")

        assertFalse(context.playServicesAvailable())
    }

    @Test
    fun createRegistersAFactoryThatBuildsTheGmsProviderWhenAvailable() {
        stubPlayServices(ConnectionResult.SUCCESS)

        GmsLocationProviderInitializer().create(context)

        assertIs<GmsLocationProvider>(registered.captured.create(context))
    }

    @Test
    fun theRegisteredFactoryDeclinesDevicesWithoutPlayServices() {
        stubPlayServices(ConnectionResult.SERVICE_MISSING)

        GmsLocationProviderInitializer().create(context)

        assertNull(registered.captured.create(context))
    }
}
