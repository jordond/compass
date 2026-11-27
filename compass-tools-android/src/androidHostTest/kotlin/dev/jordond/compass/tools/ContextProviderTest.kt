package dev.jordond.compass.tools

import android.content.Context
import io.mockk.mockk
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class ContextProviderTest {

    @BeforeTest
    fun setUp() = resetContextProvider()

    @AfterTest
    fun tearDown() = resetContextProvider()

    @Test
    fun getInstanceThrowsBeforeInitialization() {
        val error = assertFailsWith<IllegalStateException> { ContextProvider.getInstance() }

        assertEquals("ContextProvider has not been initialized", error.message)
    }

    @Test
    fun createStoresTheContextForGetInstance() {
        val context = mockk<Context>()

        val created = ContextProvider.create(context)

        assertSame(context, created.context)
        assertSame(created, ContextProvider.getInstance())
    }

    @Test
    fun createKeepsTheFirstInstance() {
        val first = ContextProvider.create(mockk<Context>())
        val second = ContextProvider.create(mockk<Context>())

        assertSame(first, second)
        assertSame(first.context, ContextProvider.getInstance().context)
    }

    @Test
    fun initializerRegistersTheSingleton() {
        val context = mockk<Context>()

        val created = ContextProviderInitializer().create(context)

        assertSame(context, created.context)
        assertSame(created, ContextProvider.getInstance())
    }

    private fun resetContextProvider() {
        ContextProvider.instance = null
    }
}
