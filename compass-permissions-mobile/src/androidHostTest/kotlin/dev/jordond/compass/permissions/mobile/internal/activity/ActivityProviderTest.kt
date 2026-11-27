package dev.jordond.compass.permissions.mobile.internal.activity

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.startup.Initializer
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class ActivityProviderTest {

    private val callbacks = slot<Application.ActivityLifecycleCallbacks>()
    private val application = mockk<Application>(relaxed = true)
    private val context = mockk<Context>()

    @BeforeTest
    fun setUp() {
        resetProvider()
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { context.applicationContext } returns application
        every { application.registerActivityLifecycleCallbacks(capture(callbacks)) } returns Unit
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
        resetProvider()
    }

    @Test
    fun getInstanceThrowsBeforeInitialization() {
        val error = assertFailsWith<IllegalStateException> { ActivityProvider.getInstance() }

        assertEquals("ActivityProvider has not been initialized", error.message)
    }

    @Test
    fun createKeepsTheFirstInstance() {
        val first = ActivityProvider.create(context)
        val second = ActivityProvider.create(context)

        assertSame(first, second)
        assertSame(first, ActivityProvider.getInstance())
    }

    @Test
    fun initializerRegistersTheSingleton() {
        val created = ActivityProviderInitializer().create(context)

        assertSame(created, ActivityProvider.getInstance())
    }

    @Test
    fun initializerDeclaresNoDependencies() {
        val dependencies: List<Class<out Initializer<*>>> = ActivityProviderInitializer().dependencies()

        assertEquals(emptyList(), dependencies)
    }

    @Test
    fun permissionRequesterIsUnavailableBeforeAnActivityIsCreated() {
        val provider = ActivityProvider(context)

        assertFailsWith<IllegalStateException> { provider.permissionRequester }
    }

    @Test
    fun registersALifecycleObserverOnTheApplication() {
        ActivityProvider(context)

        verify(exactly = 1) { application.registerActivityLifecycleCallbacks(any()) }
    }

    @Test
    fun aCreatedComponentActivityGetsAPermissionRequester() {
        val activity = mockk<ComponentActivity>(relaxed = true)
        ActivityProvider(context)

        callbacks.captured.onActivityCreated(activity, null)

        verify(exactly = 1) { activity.registerForActivityResult(
                any<ActivityResultContract<Array<String>, Map<String, Boolean>>>(),
                any<ActivityResultCallback<Map<String, Boolean>>>(),
            ) }
    }

    @Test
    fun observerForwardsOnlyComponentActivities() {
        val received = mutableListOf<ComponentActivity>()
        val observer = createActivityLifecycleObserver { received += it }
        val component = mockk<ComponentActivity>()

        observer.onActivityCreated(component, null)
        observer.onActivityCreated(mockk<Activity>(), null)

        assertEquals(listOf(component), received)
        verify(exactly = 1) { Log.e(any(), any()) }
    }

    private fun resetProvider() {
        ActivityProvider::class.java.getDeclaredField("instance").apply {
            isAccessible = true
            set(null, null)
        }
    }
}
