package dev.jordond.compass.geolocation.mobile.internal.activity

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

internal fun createActivityLifecycleObserver(
    block: (ComponentActivity) -> Unit,
): Application.ActivityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is ComponentActivity) {
            block(activity)
        } else {
            Log.e("CompassTools", "Activity is not a ComponentActivity. Cannot attach lifecycle observer.")
        }
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}