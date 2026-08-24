package dev.jordond.compass.geolocation.mobile.internal

import android.location.LocationManager.FUSED_PROVIDER
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.location.LocationManager.PASSIVE_PROVIDER
import android.os.Build.VERSION_CODES
import dev.jordond.compass.Priority
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val LEGACY = VERSION_CODES.R
private const val FUSED_CAPABLE = VERSION_CODES.S

class ProviderSelectionTest {

    @Test
    fun prefersThePlatformFusedProviderOnApi31AndAbove() {
        // The closest non-proprietary equivalent to the Play Services client, so it should win over
        // hand-picking a raw provider regardless of which priority was asked for.
        for (priority in listOf(Priority.HighAccuracy, Priority.Balanced, Priority.LowPower)) {
            assertEquals(
                FUSED_PROVIDER,
                select(priority, listOf(FUSED_PROVIDER, GPS_PROVIDER, NETWORK_PROVIDER)),
            )
        }
    }

    @Test
    fun ignoresTheFusedProviderBelowApi31() {
        // FUSED_PROVIDER did not exist before API 31, so a device reporting it there is not one we
        // should register on.
        assertEquals(
            GPS_PROVIDER,
            select(
                priority = Priority.HighAccuracy,
                providers = listOf(FUSED_PROVIDER, GPS_PROVIDER),
                sdkInt = LEGACY,
            ),
        )
    }

    @Test
    fun mapsPrioritiesToProvidersBelowApi31() {
        val all = listOf(GPS_PROVIDER, NETWORK_PROVIDER, PASSIVE_PROVIDER)

        assertEquals(GPS_PROVIDER, select(Priority.HighAccuracy, all, sdkInt = LEGACY))
        assertEquals(NETWORK_PROVIDER, select(Priority.Balanced, all, sdkInt = LEGACY))
        assertEquals(NETWORK_PROVIDER, select(Priority.LowPower, all, sdkInt = LEGACY))
        assertEquals(PASSIVE_PROVIDER, select(Priority.Passive, all, sdkInt = LEGACY))
    }

    @Test
    fun fallsBackToTheOtherProviderWhenThePreferredOneIsDisabled() {
        assertEquals(
            NETWORK_PROVIDER,
            select(Priority.HighAccuracy, listOf(NETWORK_PROVIDER), sdkInt = LEGACY),
        )
        assertEquals(
            GPS_PROVIDER,
            select(Priority.Balanced, listOf(GPS_PROVIDER), sdkInt = LEGACY),
        )
    }

    @Test
    fun onlyConsidersEnabledProviders() {
        // Registering on a disabled provider succeeds and then never delivers, which looks
        // identical to a device that cannot get a fix. A disabled GPS must not be selected.
        assertEquals(
            NETWORK_PROVIDER,
            select(Priority.HighAccuracy, listOf(NETWORK_PROVIDER), sdkInt = FUSED_CAPABLE),
        )
    }

    @Test
    fun neverAnswersAnActiveRequestWithThePassiveProvider() {
        // The passive provider only reports fixes another app already asked for, so it can stay
        // quiet indefinitely. Returning null lets the caller fail instead of hanging.
        for (priority in listOf(Priority.HighAccuracy, Priority.Balanced)) {
            assertNull(select(priority, listOf(PASSIVE_PROVIDER)))
        }
    }

    @Test
    fun allowsThePassiveProviderForLowPowerAndPassive() {
        assertEquals(
            PASSIVE_PROVIDER,
            select(Priority.LowPower, listOf(PASSIVE_PROVIDER), sdkInt = LEGACY),
        )
        assertEquals(PASSIVE_PROVIDER, select(Priority.Passive, listOf(PASSIVE_PROVIDER)))
    }

    @Test
    fun passiveDoesNotBorrowTheFusedProvider() {
        // Passive means "whatever other apps are already getting". Handing it the fused provider
        // would quietly start driving the sensors, which is the opposite of what was asked.
        assertNull(select(Priority.Passive, listOf(FUSED_PROVIDER, GPS_PROVIDER)))
    }

    @Test
    fun selectsNothingWhenLocationIsOff() {
        // Location being switched off outranks any provider still reporting itself as enabled.
        assertEquals(
            null,
            selectProvider(
                priority = Priority.HighAccuracy,
                locationEnabled = false,
                enabledProviders = listOf(FUSED_PROVIDER, GPS_PROVIDER, NETWORK_PROVIDER),
                sdkInt = FUSED_CAPABLE,
            ),
        )
    }

    @Test
    fun selectsNothingWhenNoProviderIsEnabled() {
        assertNull(select(Priority.HighAccuracy, emptyList()))
    }

    private fun select(
        priority: Priority,
        providers: Collection<String>,
        sdkInt: Int = FUSED_CAPABLE,
    ): String? = selectProvider(
        priority = priority,
        locationEnabled = true,
        enabledProviders = providers,
        sdkInt = sdkInt,
    )
}
