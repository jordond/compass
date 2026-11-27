package dev.jordond.compass.permissions.mobile

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.Intent
import android.net.Uri
import dev.jordond.compass.tools.ContextProvider
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.Runs
import io.mockk.verify
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ExtensionsAndroidTest {

    private val context = mockk<Context>()
    private val uri = mockk<Uri>()

    @BeforeTest
    fun setUp() {
        resetContextProvider()
        mockkConstructor(Intent::class)
        mockkStatic(Uri::class)
        every { context.applicationContext } returns context
        every { context.applicationInfo } returns ApplicationInfo().apply {
            packageName = "dev.example.app"
        }
        every { Uri.fromParts(any(), any(), any()) } returns uri
        every { anyConstructed<Intent>().setFlags(any()) } answers { self as Intent }
        every { anyConstructed<Intent>().setData(any()) } answers { self as Intent }
        ContextProvider.create(context)
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
        resetContextProvider()
    }

    @Test
    fun opensTheApplicationDetailsSettingsForThePackage() {
        every { context.startActivity(any()) } just Runs

        openPermissionSettings()

        verify(exactly = 1) { Uri.fromParts("package", "dev.example.app", null) }
        verify(exactly = 1) {
            anyConstructed<Intent>().setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK,
            )
        }
        verify(exactly = 1) { anyConstructed<Intent>().setData(uri) }
        verify(exactly = 1) { context.startActivity(any()) }
    }

    private fun resetContextProvider() {
        ContextProvider::class.java.getDeclaredField("instance").apply {
            isAccessible = true
            set(null, null)
        }
    }
}
