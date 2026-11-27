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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
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

            val state = controller().requirePermissionFor(priority)

            assertEquals(PermissionState.Granted, state, "state for $priority")
            assertEquals(listOf(coarse), permissions.captured, "permissions for $priority")
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

    @Test
    fun concurrentRequestsPromptOneAtATime() = runTest {
        val prompts = mutableListOf<(PermissionState) -> Unit>()
        every { requester.request(any(), any()) } answers {
            prompts += secondArg<(PermissionState) -> Unit>()
            mockk<Job>()
        }
        val controller = controller()
        val first = async { controller.requirePermissionFor(Priority.HighAccuracy) }
        val second = async { controller.requirePermissionFor(Priority.Balanced) }

        // A failed assertion must still answer every prompt so the test fails instead of hanging
        try {
            runCurrent()
            assertEquals(1, prompts.size, "prompts while the first request is unanswered")

            prompts.removeAt(0).invoke(PermissionState.Denied)
            runCurrent()
            assertEquals(1, prompts.size, "prompts once the first request is answered")

            prompts.removeAt(0).invoke(PermissionState.Granted)
            assertEquals(PermissionState.Denied, first.await())
            assertEquals(PermissionState.Granted, second.await())
        } finally {
            while (prompts.isNotEmpty()) {
                prompts.removeAt(0).invoke(PermissionState.Denied)
                runCurrent()
            }
        }
    }

    @Test
    fun cancellingWhileThePromptIsOpenReleasesTheCallerAndTheNextRequest() = runTest {
        val prompts = mutableListOf<(PermissionState) -> Unit>()
        val launched = mutableListOf<Job>()
        every { requester.request(any(), any()) } answers {
            prompts += secondArg<(PermissionState) -> Unit>()
            Job().also { job -> launched += job }
        }
        val controller = controller()

        val abandoned = async { controller.requirePermissionFor(Priority.HighAccuracy) }
        runCurrent()
        abandoned.cancel()
        runCurrent()

        assertTrue(abandoned.isCancelled)
        assertTrue(launched.single().isCancelled, "the abandoned prompt request is cancelled")

        val next = async { controller.requirePermissionFor(Priority.Balanced) }
        runCurrent()
        assertEquals(2, prompts.size, "the next request is not stuck behind the abandoned one")

        // A late answer to the abandoned prompt is ignored
        prompts[0].invoke(PermissionState.Denied)
        prompts[1].invoke(PermissionState.Granted)
        assertEquals(PermissionState.Granted, next.await())
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
