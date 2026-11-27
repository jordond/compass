package dev.jordond.compass.geocoder.mobile

import android.content.Context
import android.location.Address
import android.location.Geocoder
import dev.jordond.compass.Coordinates
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geocoder.exception.GeocodeException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// SDK_INT is 0 on the host, so these exercise the pre-Tiramisu synchronous geocoder path.
@Suppress("DEPRECATION")
class AndroidPlatformGeocoderTest {

    private val context = mockk<Context>()

    @BeforeTest
    fun setUp() {
        mockkStatic(Geocoder::class)
        mockkConstructor(Geocoder::class)
        every { Geocoder.isPresent() } returns true
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun isAvailableFollowsGeocoderIsPresent() {
        val geocoder = AndroidPlatformGeocoder(context, null)

        every { Geocoder.isPresent() } returns false
        assertFalse(geocoder.isAvailable())

        every { Geocoder.isPresent() } returns true
        assertTrue(geocoder.isAvailable())
    }

    @Test
    fun everyOperationThrowsNotSupportedWhenNoGeocoderIsPresent() = runTest {
        every { Geocoder.isPresent() } returns false
        val geocoder = AndroidPlatformGeocoder(context, null)

        assertFailsWith<NotSupportedException> { geocoder.reverse(1.0, 2.0) }
        assertFailsWith<NotSupportedException> { geocoder.placeFromAddress("home") }
        assertFailsWith<NotSupportedException> { geocoder.forward("home") }
    }

    @Test
    fun reverseMapsAddressesToPlaces() = runTest {
        every { anyConstructed<Geocoder>().getFromLocation(43.0, -79.0, 5) } returns
            mutableListOf(address(43.0, -79.0, name = "Home"))

        val places = AndroidPlatformGeocoder(context, null).reverse(43.0, -79.0)

        assertEquals(1, places.size)
        assertEquals("Home", places.single().name)
        assertEquals(Coordinates(43.0, -79.0), places.single().coordinates)
    }

    @Test
    fun placeFromAddressMapsAddressesToPlaces() = runTest {
        every { anyConstructed<Geocoder>().getFromLocationName("1 Main St", 5) } returns
            mutableListOf(address(1.0, 2.0, name = "Main"), address(3.0, 4.0, name = "Side"))

        val places = AndroidPlatformGeocoder(context, null).placeFromAddress("1 Main St")

        assertEquals(listOf("Main", "Side"), places.map { it.name })
    }

    @Test
    fun forwardMapsAddressesToCoordinates() = runTest {
        every { anyConstructed<Geocoder>().getFromLocationName("1 Main St", 5) } returns
            mutableListOf(address(1.0, 2.0), address(3.0, 4.0))

        val coordinates = AndroidPlatformGeocoder(context, null).forward("1 Main St")

        assertEquals(listOf(Coordinates(1.0, 2.0), Coordinates(3.0, 4.0)), coordinates)
    }

    @Test
    fun noResultsProduceAnEmptyList() = runTest {
        every { anyConstructed<Geocoder>().getFromLocationName(any<String>(), any()) } returns null

        assertEquals(emptyList(), AndroidPlatformGeocoder(context, null).forward("nowhere"))
    }

    @Test
    fun geocoderFailuresSurfaceAsGeocodeException() = runTest {
        every { anyConstructed<Geocoder>().getFromLocation(any(), any(), any()) } throws
            java.io.IOException("boom")

        assertFailsWith<GeocodeException> { AndroidPlatformGeocoder(context, null).reverse(0.0, 0.0) }
    }

    private fun address(lat: Double, lon: Double, name: String? = null): Address =
        mockk(relaxed = true) {
            every { latitude } returns lat
            every { longitude } returns lon
            every { featureName } returns name
        }
}
