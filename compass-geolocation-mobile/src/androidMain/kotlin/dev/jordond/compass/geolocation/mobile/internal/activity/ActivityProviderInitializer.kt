package dev.jordond.compass.geolocation.mobile.internal.activity

import android.content.Context
import androidx.startup.Initializer

internal class ActivityProviderInitializer : Initializer<ActivityProvider> {

    override fun create(context: Context): ActivityProvider {
        return ActivityProvider.create(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}