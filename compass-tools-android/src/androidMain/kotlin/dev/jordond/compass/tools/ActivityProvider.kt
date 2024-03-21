package dev.jordond.compass.tools

import android.app.Activity
import android.app.Application
import android.content.Context
import dev.jordond.compass.tools.internal.createActivityLifecycleObserver
import java.lang.ref.WeakReference

public class ActivityProvider(
    private val context: Context,
) {

    private var activity: WeakReference<Activity>? = null

    public val activeActivity: Activity?
        get() {
            val activity = this.activity?.get() ?: return null
            if (activity.isFinishing || activity.isDestroyed) return null
            return activity
        }

    private val lifecycleObserver = createActivityLifecycleObserver { activity ->
        this.activity = WeakReference(activity)
    }

    init {
        val application = context.applicationContext as? Application
        application?.registerActivityLifecycleCallbacks(lifecycleObserver) ?: run {
            val activity = context.applicationContext as? Activity
            this.activity = WeakReference(activity)
        }
    }
}