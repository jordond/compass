package dev.jordond.compass.permissions.mobile.internal

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.jordond.compass.permissions.PermissionState
import kotlinx.coroutines.launch

/**
 * Request the location permissions using the Activity Result API.
 */
internal class PermissionRequester(private val activity: ComponentActivity) {

    private var permissionCallback: ((PermissionState) -> Unit)? = null

    private val request = activity
        .registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val callback = permissionCallback ?: return@registerForActivityResult
            this.permissionCallback = null

            val success = results.values.all { it }
            if (success) {
                callback(PermissionState.Granted)
            } else {
                val permission = results.entries.first { !it.value }.key
                val showRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

                if (showRationale) {
                    callback(PermissionState.Denied)
                } else {
                    callback(PermissionState.DeniedForever)
                }
            }
        }

    fun request(
        permissions: List<String>,
        resultCallback: (PermissionState) -> Unit,
    ) = with(activity) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                permissionCallback?.let { callback ->
                    callback(PermissionState.Denied)
                    permissionCallback = null
                }

                permissionCallback = resultCallback

                request.launch(permissions.toTypedArray())
            }
        }
    }
}