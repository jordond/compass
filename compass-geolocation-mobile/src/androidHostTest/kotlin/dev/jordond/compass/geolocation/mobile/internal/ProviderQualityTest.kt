package dev.jordond.compass.geolocation.mobile.internal

import androidx.core.location.LocationRequestCompat
import dev.jordond.compass.Priority
import kotlin.test.Test
import kotlin.test.assertEquals

class ProviderQualityTest {

    @Test
    fun mapsPrioritiesToQuality() {
        assertEquals(LocationRequestCompat.QUALITY_HIGH_ACCURACY, Priority.HighAccuracy.toQuality())
        assertEquals(
            LocationRequestCompat.QUALITY_BALANCED_POWER_ACCURACY,
            Priority.Balanced.toQuality(),
        )
        assertEquals(LocationRequestCompat.QUALITY_LOW_POWER, Priority.LowPower.toQuality())
        assertEquals(LocationRequestCompat.QUALITY_LOW_POWER, Priority.Passive.toQuality())
    }
}
