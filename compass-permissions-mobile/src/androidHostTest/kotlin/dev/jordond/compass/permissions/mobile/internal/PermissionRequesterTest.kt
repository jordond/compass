package dev.jordond.compass.permissions.mobile.internal

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityCompat
import dev.jordond.compass.permissions.PermissionState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private const val FINE = "android.permission.ACCESS_FINE_LOCATION"
private const val COARSE = "android.permission.ACCESS_COARSE_LOCATION"

class PermissionRequesterTest {

    private val resultCallback = slot<ActivityResultCallback<Map<String, Boolean>>>()
    private val activity = mockk<ComponentActivity>()
    private val states = mutableListOf<PermissionState>()

    @BeforeTest
    fun setUp() {
        mockkStatic(ActivityCompat::class)
        every {
            activity.registerForActivityResult(
                any<ActivityResultContract<Array<String>, Map<String, Boolean>>>(),
                capture(resultCallback),
            )
        } returns mockk<ActivityResultLauncher<Array<String>>>(relaxed = true)
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun grantedWhenEveryPermissionIsGranted() {
        awaitingResult()

        deliver(mapOf(FINE to true, COARSE to true))

        assertEquals(listOf(PermissionState.Granted), states)
    }

    @Test
    fun deniedWhenARationaleCanStillBeShown() {
        awaitingResult()
        every { ActivityCompat.shouldShowRequestPermissionRationale(activity, FINE) } returns true

        deliver(mapOf(FINE to false, COARSE to true))

        assertEquals(listOf(PermissionState.Denied), states)
    }

    @Test
    fun deniedForeverWhenNoRationaleCanBeShown() {
        awaitingResult()
        every { ActivityCompat.shouldShowRequestPermissionRationale(activity, FINE) } returns false

        deliver(mapOf(FINE to false, COARSE to false))

        assertEquals(listOf(PermissionState.DeniedForever), states)
    }

    @Test
    fun theRationaleCheckUsesTheFirstDeniedPermission() {
        awaitingResult()
        every { ActivityCompat.shouldShowRequestPermissionRationale(activity, COARSE) } returns true

        deliver(mapOf(FINE to true, COARSE to false))

        assertEquals(listOf(PermissionState.Denied), states)
    }

    @Test
    fun aResultIsDeliveredOnlyOnce() {
        awaitingResult()

        deliver(mapOf(FINE to true))
        deliver(mapOf(FINE to true))

        assertEquals(listOf(PermissionState.Granted), states)
    }

    @Test
    fun aResultWithNoPendingRequestIsIgnored() {
        PermissionRequester(activity)

        resultCallback.captured.onActivityResult(mapOf(FINE to true))

        assertEquals(emptyList(), states)
    }

    private fun awaitingResult() {
        val requester = PermissionRequester(activity)
        requester.javaClass.getDeclaredField("permissionCallback").apply {
            isAccessible = true
            set(requester, { state: PermissionState -> states += state })
        }
    }

    private fun deliver(results: Map<String, Boolean>) {
        resultCallback.captured.onActivityResult(results)
    }
}
