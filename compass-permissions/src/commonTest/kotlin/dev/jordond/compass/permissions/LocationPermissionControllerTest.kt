package dev.jordond.compass.permissions

import dev.jordond.compass.Priority
import dev.jordond.compass.permissions.exception.PermissionDeniedException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocationPermissionControllerTest {

    private class FakeController(private val state: PermissionState) : LocationPermissionController {
        val requested = mutableListOf<Priority>()

        override fun hasPermission(): Boolean = state == PermissionState.Granted

        override suspend fun requirePermissionFor(priority: Priority): PermissionState {
            requested += priority
            return state
        }
    }

    @Test
    fun grantedAccuracyDefaultsToUnknown() {
        assertEquals(LocationAccuracy.Unknown, FakeController(PermissionState.Granted).grantedAccuracy())
    }

    @Test
    fun requireOrThrowRequestsThePriorityAndReturnsWhenGranted() = runTest {
        val controller = FakeController(PermissionState.Granted)

        controller.requirePermissionForOrThrow(Priority.HighAccuracy)

        assertEquals(listOf(Priority.HighAccuracy), controller.requested)
    }

    @Test
    fun requireOrThrowThrowsWhenTheRequestIsDenied() = runTest {
        assertFailsWith<PermissionDeniedException> {
            FakeController(PermissionState.Denied).requirePermissionForOrThrow(Priority.Balanced)
        }
    }
}
