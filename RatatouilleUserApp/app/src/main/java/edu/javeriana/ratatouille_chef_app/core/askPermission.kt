package edu.javeriana.ratatouille_chef_app.core

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun askPermission(
    activity: Activity,
    permissionArray: Array<String>,
    permission_code: Int,
    onPermissionGranted: () -> Unit
) {
    var havePermission = true
    for (permission in permissionArray) {
        havePermission = havePermission && (ContextCompat.checkSelfPermission(
            activity,
            permission
        ) != PackageManager.PERMISSION_GRANTED)
    }
    if (havePermission
    ) {

        ActivityCompat.requestPermissions(
            activity,
            permissionArray,
            permission_code
        )
    } else {
        onPermissionGranted()
    }
}