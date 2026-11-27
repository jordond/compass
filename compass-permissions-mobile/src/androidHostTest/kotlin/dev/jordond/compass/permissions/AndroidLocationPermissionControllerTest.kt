package dev.jordond.compass.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dev.jordond.compass.Priority
import dev.jordond.compass.permissions.mobile.internal.PermissionRequester
import dev.jordond.compass.permissions.mobile.internal.activity.ActivityProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AndroidLocationPermissionControllerTest {

    private val fine = Manifest.permission.ACCESS_FINE_LOCATION
    private val coarse = Manifest.permission.ACCESS_COARSE_LOCATION

    private val context = mockk<Context>()
    private val requester = mockk<PermissionRequester>(relaxed = true)
    private val activityProvider = mockk<ActivityProvider> {
        every { permissionRequester } returns requester
    }
    private val held = mutableSetOf<String>()

    @BeforeTest
    fun setUp() {
        mockkStatic(ContextCompat::class)
        every { ContextCompat.checkSelfPermission(context, any()) } answers {
            if (secondArg<String>() in held) {
                PackageManager.PERMISSION_GRANTED
            } else {
                PackageManager.PERMISSION_DENIED
            }
        }
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun hasPermissionIsTrueWithEitherLocationPermission() {
        assertFalse(controller().hasPermission())

        held += coarse
        assertTrue(controller().hasPermission())

        held.clear()
        held += fine
        assertTrue(controller().hasPermission())
    }

    @Test
    fun grantedAccuracyIsUnknownWithoutAnyPermission() {
        assertEquals(LocationAccuracy.Unknown, controller().grantedAccuracy())
    }

    @Test
    fun grantedAccuracyIsReducedWithOnlyTheCoarsePermission() {
        held += coarse

        assertEquals(LocationAccuracy.Reduced, controller().grantedAccuracy())
    }

    @Test
    fun grantedAccuracyIsFullWithTheFinePermission() {
        held += fine

        assertEquals(LocationAccuracy.Full, controller().grantedAccuracy())
        held += coarse
        assertEquals(LocationAccuracy.Full, controller().grantedAccuracy())
    }

    @Test
    fun requirePermissionIsGrantedWithoutPromptingWhenAlreadyHeld() = runTest {
        held += fine
        held += coarse

        assertEquals(PermissionState.Granted, controller().requirePermissionFor(Priority.HighAccuracy))
        verify(exactly = 0) { requester.request(any(), any()) }
    }

    @Test
    fun highAccuracyAsksForBothPermissionsAndReturnsTheResult() = runTest {
        val permissions = slot<List<String>>()
        answerRequest(PermissionState.DeniedForever, permissions)

        val state = controller().requirePermissionFor(Priority.HighAccuracy)

        assertEquals(PermissionState.DeniedForever, state)
        assertEquals(listOf(fine, coarse), permissions.captured)
    }

    @Test
    fun lowerPrioritiesOnlyAskForTheCoarsePermission() = runTest {
        for (priority in listOf(Priority.Balanced, Priority.LowPower, Priority.Passive)) {
            val permissions = slot<List<String>>()
            answerRequest(PermissionState.Granted, permissions)

            assertEquals(PermissionState.Granted, controller().requirePermissionFor(priority))
            assertEquals(listOf(coarse), permissions.captured)
        }
    }

    @Test
    fun onlyTheMissingPermissionsAreRequested() = runTest {
        held += coarse
        val permissions = slot<List<String>>()
        answerRequest(PermissionState.Granted, permissions)

        controller().requirePermissionFor(Priority.HighAccuracy)

        assertEquals(listOf(fine), permissions.captured)
    }

    @Test
    fun aLowerPriorityIsSatisfiedByTheCoarsePermissionAlone() = runTest {
        held += coarse

        assertEquals(PermissionState.Granted, controller().requirePermissionFor(Priority.Balanced))
        verify(exactly = 0) { requester.request(any(), any()) }
    }

    private fun answerRequest(
        state: PermissionState,
        permissions: io.mockk.CapturingSlot<List<String>>,
    ) {
        every { requester.request(capture(permissions), any()) } answers {
            secondArg<(PermissionState) -> Unit>().invoke(state)
            mockk<Job>()
        }
    }

    private fun controller() = AndroidLocationPermissionController(context, activityProvider)
}
