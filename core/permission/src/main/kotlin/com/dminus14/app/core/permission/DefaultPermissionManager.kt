package com.dminus14.app.core.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultPermissionManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : PermissionManager {
        override fun isGranted(permission: AppPermission): Boolean =
            ContextCompat.checkSelfPermission(context, permission.manifestName) ==
                PackageManager.PERMISSION_GRANTED

        override fun shouldShowRationale(
            permission: AppPermission,
            shouldShowRequestPermissionRationale: (manifestPermission: String) -> Boolean,
        ): Boolean = shouldShowRequestPermissionRationale(permission.manifestName)
    }
