package dev.jordond.compass.permissions.mobile.internal.activity

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import dev.jordond.compass.permissions.mobile.internal.PermissionRequester
import java.lang.ref.WeakReference

internal class ActivityProvider(
    private val context: Context,
) {

    private var requester: WeakReference<PermissionRequester>? = null

    val permissionRequester: PermissionRequester
        get() = requester?.get() ?: throw IllegalStateException("PermissionRequester is not available")

    private val lifecycleObserver = createActivityLifecycleObserver { activity ->
        this.requester = WeakReference(PermissionRequester(activity))
    }

    init {
        val application = context.applicationContext as? Application
        application?.registerActivityLifecycleCallbacks(lifecycleObserver) ?: run {
            val activity = context.applicationContext as? ComponentActivity
            if (activity != null) {
                this.requester = WeakReference(PermissionRequester(activity))
            }
        }
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: ActivityProvider? = null

        fun create(context: Context): ActivityProvider {
            if (instance == null) {
                instance = ActivityProvider(context)
            }

            return instance!!
        }

        fun getInstance(): ActivityProvider = instance
            ?: throw IllegalStateException("ActivityProvider has not been initialized")
    }
}