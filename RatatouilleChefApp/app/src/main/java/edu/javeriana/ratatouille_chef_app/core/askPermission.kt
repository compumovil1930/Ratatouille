package edu.javeriana.ratatouille_chef_app.core

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun askPermission(
    context: Activity,
    permission: String,
    permission_code: Int,
    onPermissionGranted: () -> Unit
) {
    if (ContextCompat.checkSelfPermission(context, permission)
        != PackageManager.PERMISSION_GRANTED
    ) {

        ActivityCompat.requestPermissions(
            context,
            arrayOf(permission),
            permission_code
        )
    } else {
        onPermissionGranted()
    }
}