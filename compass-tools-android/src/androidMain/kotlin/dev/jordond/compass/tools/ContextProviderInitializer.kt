package dev.jordond.compass.tools

import android.content.Context
import androidx.startup.Initializer
import dev.jordond.compass.InternalCompassApi

@InternalCompassApi
internal class ContextProviderInitializer : Initializer<ContextProvider> {

    override fun create(context: Context): ContextProvider {
        return ContextProvider.create(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
