package dev.jordond.compass.geolocation.mobile

import android.content.Context
import io.mockk.mockk
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class LocationProviderRegistryTest {

    private val context = mockk<Context>()

    @BeforeTest
    fun setUp() = resetRegistry()

    @AfterTest
    fun tearDown() = resetRegistry()

    @Test
    fun createReturnsNullWhenNothingIsRegistered() {
        assertNull(LocationProviderRegistry.create(context))
    }

    @Test
    fun createDelegatesToTheRegisteredFactory() {
        val provider = mockk<PlatformLocationProvider>()
        var received: Context? = null

        LocationProviderRegistry.register { target ->
            received = target
            provider
        }

        assertSame(provider, LocationProviderRegistry.create(context))
        assertSame(context, received)
    }

    @Test
    fun theMostRecentRegistrationWins() {
        val first = mockk<PlatformLocationProvider>()
        val second = mockk<PlatformLocationProvider>()

        LocationProviderRegistry.register { first }
        LocationProviderRegistry.register { second }

        assertSame(second, LocationProviderRegistry.create(context))
    }

    @Test
    fun aFactoryMayDeclineTheDevice() {
        LocationProviderRegistry.register { null }

        assertNull(LocationProviderRegistry.create(context))
    }

    private fun resetRegistry() {
        LocationProviderRegistry.factory = null
    }
}
