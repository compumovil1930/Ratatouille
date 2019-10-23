package edu.javeriana.ratatouille_chef_app.core

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun askPermission(
    context: Activity,
    permissionArray: Array<String>,
    permission_code: Int,
    onPermissionGranted: () -> Unit
) {
    var havePermission = true
    for (permission in permissionArray) {
        havePermission = havePermission && (ContextCompat.checkSelfPermission(
            context,
            permission
        ) != PackageManager.PERMISSION_GRANTED)
    }
    if (havePermission
    ) {

        ActivityCompat.requestPermissions(
            context,
            permissionArray,
            permission_code
        )
    } else {
        onPermissionGranted()
    }
}