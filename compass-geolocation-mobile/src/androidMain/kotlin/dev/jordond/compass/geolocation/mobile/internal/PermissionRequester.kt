package dev.jordond.compass.geolocation.mobile.internal

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.jordond.compass.geolocation.mobile.Permission
import dev.jordond.compass.geolocation.mobile.PermissionResult
import kotlinx.coroutines.launch

private data class PermissionCallback(
    val permission: Permission,
    val callback: (PermissionResult) -> Unit,
)

internal class PermissionRequester(private val activity: ComponentActivity) {

    private var permissionCallback: PermissionCallback? = null

    private val request = activity.registerForActivityResult(RequestMultiplePermissions()) { results ->
        val callback = permissionCallback?.callback ?: return@registerForActivityResult
        this.permissionCallback = null

        val success = results.values.all { it }
        if (success) {
            callback(PermissionResult.Granted)
        } else {
            val permission = results.entries.first { !it.value }.key
            val showRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

            if (showRationale) {
                callback(PermissionResult.Denied(permission.toPermission()))
            } else {
                callback(PermissionResult.DeniedForever(permission.toPermission()))
            }
        }
    }

    fun request(
        permissions: List<String>,
        resultCallback: (PermissionResult) -> Unit,
    ) = with(activity) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                permissionCallback?.let { (permission, callback) ->
                    callback(PermissionResult.Cancelled(permission))
                    permissionCallback = null
                }

                permissionCallback = PermissionCallback(
                    permission = permissions.first().toPermission(),
                    callback = resultCallback,
                )

                request.launch(permissions.toTypedArray())
            }
        }
    }
}
