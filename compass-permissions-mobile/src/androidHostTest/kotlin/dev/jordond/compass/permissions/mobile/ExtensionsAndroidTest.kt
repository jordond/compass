package dev.jordond.compass.permissions.mobile

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.provider.Settings
import dev.jordond.compass.tools.ContextProvider
import io.mockk.EqMatcher
import io.mockk.MockKMatcherScope
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ExtensionsAndroidTest {

    private val context = mockk<Context>()
    private val packageUri = mockk<Uri>()

    private var flags: Int? = null
    private var data: Uri? = null

    @BeforeTest
    fun setUp() {
        mockkObject(ContextProvider)
        mockkConstructor(Intent::class)
        mockkStatic(Uri::class)
        every { ContextProvider.getInstance() } returns ContextProvider(context)
        every { context.applicationContext } returns context
        every { context.applicationInfo } returns ApplicationInfo().apply {
            packageName = "dev.example.app"
        }
        every { context.startActivity(any()) } just Runs
        every { Uri.fromParts("package", "dev.example.app", null) } returns packageUri

        // Only an intent built with the settings action answers these. One built with any other
        // action falls through to the unmocked platform stub and fails the test.
        every { settingsIntent().setFlags(any()) } answers {
            flags = firstArg()
            self as Intent
        }
        every { settingsIntent().setData(any()) } answers {
            data = firstArg()
            self as Intent
        }
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun startsTheApplicationDetailsSettingsForThePackageInANewTask() {
        openPermissionSettings()

        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK, flags)
        assertSame(packageUri, data)
        verify(exactly = 1) { context.startActivity(ofType<Intent>()) }
    }

    private fun MockKMatcherScope.settingsIntent(): Intent =
        constructedWith<Intent>(EqMatcher(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
}
