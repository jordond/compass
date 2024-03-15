package dev.jordond.compass.internal

import android.content.Context
import androidx.startup.Initializer

internal class ContextProviderInitializer : Initializer<ContextProvider> {

    override fun create(context: Context): ContextProvider {
        return ContextProvider.create(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
