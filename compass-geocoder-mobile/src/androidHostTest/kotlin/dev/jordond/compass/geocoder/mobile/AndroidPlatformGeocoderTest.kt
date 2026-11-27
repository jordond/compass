package dev.jordond.compass.geocoder.mobile

import android.content.Context
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import dev.jordond.compass.Coordinates
import dev.jordond.compass.exception.NotSupportedException
import io.mockk.EqMatcher
import io.mockk.OfTypeMatcher
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
        val geocoder = geocoder()

        every { Geocoder.isPresent() } returns false
        assertFalse(geocoder.isAvailable())

        every { Geocoder.isPresent() } returns true
        assertTrue(geocoder.isAvailable())
    }

    @Test
    fun everyOperationThrowsNotSupportedWhenNoGeocoderIsPresent() = runTest {
        every { Geocoder.isPresent() } returns false
        val geocoder = geocoder()

        assertFailsWith<NotSupportedException> { geocoder.reverse(1.0, 2.0) }
        assertFailsWith<NotSupportedException> { geocoder.placeFromAddress("home") }
        assertFailsWith<NotSupportedException> { geocoder.forward("home") }
    }

    @Test
    fun reverseMapsAddressesToPlaces() = runTest {
        every { anyConstructed<Geocoder>().getFromLocation(43.0, -79.0, 5) } returns
            mutableListOf(address(43.0, -79.0, name = "Home"))

        val places = geocoder().reverse(43.0, -79.0)

        assertEquals(1, places.size)
        assertEquals("Home", places.single().name)
        assertEquals(Coordinates(43.0, -79.0), places.single().coordinates)
    }

    @Test
    fun placeFromAddressMapsAddressesToPlaces() = runTest {
        every { anyConstructed<Geocoder>().getFromLocationName("1 Main St", 5) } returns
            mutableListOf(address(1.0, 2.0, name = "Main"), address(3.0, 4.0, name = "Side"))

        val places = geocoder().placeFromAddress("1 Main St")

        assertEquals(listOf("Main", "Side"), places.map { it.name })
    }

    @Test
    fun forwardMapsAddressesToCoordinates() = runTest {
        every { anyConstructed<Geocoder>().getFromLocationName("1 Main St", 5) } returns
            mutableListOf(address(1.0, 2.0), address(3.0, 4.0))

        val coordinates = geocoder().forward("1 Main St")

        assertEquals(listOf(Coordinates(1.0, 2.0), Coordinates(3.0, 4.0)), coordinates)
    }

    @Test
    fun aLocaleIsHandedToThePlatformGeocoder() = runTest {
        val locale = Locale.CANADA_FRENCH
        every {
            constructedWith<Geocoder>(OfTypeMatcher<Context>(Context::class), EqMatcher(locale))
                .getFromLocationName("1 rue Principale", 5)
        } returns mutableListOf(address(1.0, 2.0))

        val coordinates = geocoder(locale = locale).forward("1 rue Principale")

        assertEquals(listOf(Coordinates(1.0, 2.0)), coordinates)
    }

    @Test
    fun tiramisuAndAboveUseTheListenerApi() = runTest {
        every {
            anyConstructed<Geocoder>().getFromLocation(43.0, -79.0, 5, any<GeocodeListener>())
        } answers {
            arg<GeocodeListener>(3).onGeocode(mutableListOf(address(43.0, -79.0, name = "Home")))
        }
        every {
            anyConstructed<Geocoder>().getFromLocationName("1 Main St", 5, any<GeocodeListener>())
        } answers {
            arg<GeocodeListener>(2).onGeocode(mutableListOf(address(1.0, 2.0, name = "Main")))
        }
        val geocoder = geocoder(sdkInt = Build.VERSION_CODES.TIRAMISU)

        assertEquals(listOf("Home"), geocoder.reverse(43.0, -79.0).map { it.name })
        assertEquals(listOf("Main"), geocoder.placeFromAddress("1 Main St").map { it.name })
        assertEquals(listOf(Coordinates(1.0, 2.0)), geocoder.forward("1 Main St"))
    }

    private fun geocoder(
        locale: Locale? = null,
        sdkInt: Int = Build.VERSION_CODES.S,
    ) = AndroidPlatformGeocoder(context, locale, sdkInt)
}
