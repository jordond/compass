package dev.jordond.compass.permissions

import dev.jordond.compass.Priority
import dev.jordond.compass.permissions.exception.PermissionDeniedException
import dev.jordond.compass.permissions.exception.PermissionDeniedForeverException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PermissionStateTest {

    @Test
    fun grantedAndNotDeterminedDoNotThrow() {
        PermissionState.Granted.throwOnError()
        PermissionState.NotDetermined.throwOnError()
    }

    @Test
    fun deniedThrowsPermissionDenied() {
        assertFailsWith<PermissionDeniedException> { PermissionState.Denied.throwOnError() }
    }

    @Test
    fun deniedForeverThrowsPermissionDeniedForever() {
        assertFailsWith<PermissionDeniedForeverException> {
            PermissionState.DeniedForever.throwOnError()
        }
    }
}

class LocationPermissionControllerTest {

    @Test
    fun grantedAccuracyDefaultsToUnknown() {
        assertEquals(LocationAccuracy.Unknown, FakeController(PermissionState.Granted).grantedAccuracy())
    }

    @Test
    fun requireOrThrowReturnsWhenGranted() = runTest {
        val controller = FakeController(PermissionState.Granted)

        controller.requirePermissionForOrThrow(Priority.HighAccuracy)

        assertEquals(listOf(Priority.HighAccuracy), controller.requested)
    }

    @Test
    fun requireOrThrowThrowsWhenDenied() = runTest {
        assertFailsWith<PermissionDeniedException> {
            FakeController(PermissionState.Denied).requirePermissionForOrThrow(Priority.Balanced)
        }
    }

    @Test
    fun requireOrThrowThrowsWhenDeniedForever() = runTest {
        assertFailsWith<PermissionDeniedForeverException> {
            FakeController(PermissionState.DeniedForever).requirePermissionForOrThrow(Priority.Balanced)
        }
    }
}

private class FakeController(private val state: PermissionState) : LocationPermissionController {
    val requested = mutableListOf<Priority>()

    override fun hasPermission(): Boolean = state == PermissionState.Granted

    override suspend fun requirePermissionFor(priority: Priority): PermissionState {
        requested += priority
        return state
    }
}
