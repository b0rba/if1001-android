package br.cin.ufpe.if1001.taskmanager.utils

import android.content.Context
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class PermissionHelper {
    companion object {
        fun requestPermission(context: Context, permissions: Collection<String>, multiplePermissionsListener: MultiplePermissionsListener){
            Dexter.withContext(context)
                .withPermissions(permissions)
                .withListener(multiplePermissionsListener)
                .check()
        }
    }
}