package dev.jordond.compass.geolocation

import dev.jordond.compass.test.location
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TrackingStatusTest {

    @Test
    fun onlyTrackingAndUpdateAreActive() {
        assertTrue(TrackingStatus.Tracking.isActive)
        assertTrue(TrackingStatus.Update(location()).isActive)
        assertFalse(TrackingStatus.Idle.isActive)
        assertFalse(TrackingStatus.Error(GeolocatorResult.NotFound).isActive)
    }
}
