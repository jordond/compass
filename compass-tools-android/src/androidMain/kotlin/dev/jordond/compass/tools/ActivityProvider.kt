package dev.jordond.compass.tools

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import dev.jordond.compass.tools.internal.createActivityLifecycleObserver
import java.lang.ref.WeakReference

public class ActivityProvider(
    private val context: Context,
) {

    private var activity: WeakReference<ComponentActivity>? = null

    public val activeActivity: ComponentActivity?
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
            val activity = context.applicationContext as? ComponentActivity
            this.activity = WeakReference(activity)
        }
    }
}