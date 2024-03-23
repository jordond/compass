package dev.jordond.compass.geolocation.mobile.internal.activity

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import dev.jordond.compass.geolocation.exception.PermissionRequestException
import dev.jordond.compass.geolocation.mobile.internal.PermissionRequester
import java.lang.ref.WeakReference

internal class ActivityProvider(
    private val context: Context,
) {

    private var activity: WeakReference<ComponentActivity>? = null
    private var requester: WeakReference<PermissionRequester>? = null

    val activeActivity: ComponentActivity?
        get() {
            val activity = this.activity?.get() ?: return null
            if (activity.isFinishing || activity.isDestroyed) return null
            return activity
        }

    val permissionRequester: PermissionRequester
        get() = requester?.get() ?: throw PermissionRequestException(
            permission = "Any",
            message = "PermissionRequester is not available"
        )

    private val lifecycleObserver = createActivityLifecycleObserver { activity ->
        this.activity = WeakReference(activity)
        this.requester = WeakReference(PermissionRequester(activity))
    }

    init {
        val application = context.applicationContext as? Application
        application?.registerActivityLifecycleCallbacks(lifecycleObserver) ?: run {
            val activity = context.applicationContext as? ComponentActivity
            this.activity = WeakReference(activity)
        }
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: ActivityProvider? = null

        public fun create(context: Context): ActivityProvider {
            if (instance == null) {
                instance = ActivityProvider(context)
            }

            return instance!!
        }

        public fun getInstance(): ActivityProvider = instance
            ?: throw IllegalStateException("ActivityProvider has not been initialized")
    }
}