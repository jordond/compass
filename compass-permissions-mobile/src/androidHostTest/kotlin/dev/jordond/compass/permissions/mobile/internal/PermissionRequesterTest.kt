package dev.jordond.compass.permissions.mobile.internal

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import dev.jordond.compass.permissions.PermissionState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private const val FINE = Manifest.permission.ACCESS_FINE_LOCATION
private const val COARSE = Manifest.permission.ACCESS_COARSE_LOCATION

@OptIn(ExperimentalCoroutinesApi::class)
class PermissionRequesterTest {

    private val resultCallback = slot<ActivityResultCallback<Map<String, Boolean>>>()
    private val launcher = mockk<ActivityResultLauncher<Array<String>>>()
    private val activity = mockk<ComponentActivity>()
    private val lifecycle = LifecycleRegistry.createUnsafe(activity)

    private val launched = mutableListOf<List<String>>()
    private val states = mutableListOf<PermissionState>()

    @BeforeTest
    fun setUp() {
        // The requester launches on the activity lifecycle scope, which runs on the main dispatcher.
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(ActivityCompat::class)
        every { activity.lifecycle } returns lifecycle
        every {
            activity.registerForActivityResult(
                any<ActivityResultContract<Array<String>, Map<String, Boolean>>>(),
                capture(resultCallback),
            )
        } returns launcher
        every { launcher.launch(any()) } answers { launched += firstArg<Array<String>>().toList() }
        lifecycle.currentState = Lifecycle.State.CREATED
    }

    @AfterTest
    fun tearDown() {
        lifecycle.currentState = Lifecycle.State.DESTROYED
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun requestLaunchesThePermissionPromptForTheGivenPermissions() {
        request(FINE, COARSE)

        assertEquals(listOf(listOf(FINE, COARSE)), launched)
        assertEquals(emptyList(), states)
    }

    @Test
    fun requestWaitsForTheActivityToBeCreated() {
        lifecycle.currentState = Lifecycle.State.INITIALIZED
        val requester = PermissionRequester(activity)

        requester.request(listOf(COARSE)) { state -> states += state }
        assertEquals(emptyList(), launched)

        lifecycle.currentState = Lifecycle.State.CREATED
        assertEquals(listOf(listOf(COARSE)), launched)
    }

    @Test
    fun grantedWhenEveryPermissionIsGranted() {
        request(FINE, COARSE)

        deliver(mapOf(FINE to true, COARSE to true))

        assertEquals(listOf(PermissionState.Granted), states)
    }

    @Test
    fun deniedWhenARationaleCanStillBeShown() {
        every { ActivityCompat.shouldShowRequestPermissionRationale(activity, FINE) } returns true
        request(FINE, COARSE)

        deliver(mapOf(FINE to false, COARSE to true))

        assertEquals(listOf(PermissionState.Denied), states)
    }

    @Test
    fun deniedForeverWhenNoRationaleCanBeShown() {
        every { ActivityCompat.shouldShowRequestPermissionRationale(activity, FINE) } returns false
        request(FINE, COARSE)

        deliver(mapOf(FINE to false, COARSE to false))

        assertEquals(listOf(PermissionState.DeniedForever), states)
    }

    @Test
    fun theRationaleCheckUsesTheFirstDeniedPermission() {
        every { ActivityCompat.shouldShowRequestPermissionRationale(activity, COARSE) } returns true
        request(FINE, COARSE)

        deliver(mapOf(FINE to true, COARSE to false))

        assertEquals(listOf(PermissionState.Denied), states)
    }

    @Test
    fun aResultIsDeliveredOnlyOnce() {
        request(FINE)

        deliver(mapOf(FINE to true))
        deliver(mapOf(FINE to true))

        assertEquals(listOf(PermissionState.Granted), states)
    }

    @Test
    fun aResultWithNoPendingRequestIsIgnored() {
        PermissionRequester(activity)

        deliver(mapOf(FINE to true))

        assertEquals(emptyList(), states)
    }

    @Test
    fun aNewRequestDeniesThePendingOne() {
        val requester = PermissionRequester(activity)
        val first = mutableListOf<PermissionState>()
        val second = mutableListOf<PermissionState>()

        requester.request(listOf(FINE)) { state -> first += state }
        requester.request(listOf(COARSE)) { state -> second += state }

        assertEquals(listOf(PermissionState.Denied), first)
        assertEquals(emptyList(), second)
        assertEquals(listOf(listOf(FINE), listOf(COARSE)), launched)

        deliver(mapOf(COARSE to true))

        assertEquals(listOf(PermissionState.Denied), first)
        assertEquals(listOf(PermissionState.Granted), second)
    }

    private fun request(vararg permissions: String) {
        PermissionRequester(activity).request(permissions.toList()) { state -> states += state }
    }

    private fun deliver(results: Map<String, Boolean>) {
        resultCallback.captured.onActivityResult(results)
    }
}
