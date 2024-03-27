package dev.jordond.compass.permissions.mobile

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import dev.jordond.compass.tools.ContextProvider

internal actual fun openPermissionSettings() {
    val context = ContextProvider.getInstance().context.applicationContext
    val packageName = context.applicationInfo.packageName
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            data = Uri.fromParts("package", packageName, null)
        }

    context.startActivity(intent)
}