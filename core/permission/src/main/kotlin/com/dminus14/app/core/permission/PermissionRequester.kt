package com.dminus14.app.core.permission

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/**
 * [ActivityResultContracts.RequestPermission]로 권한을 요청하는 함수를 반환한다.
 *
 * 반환된 함수에 [permission]을 넘기면 시스템 권한 다이얼로그가 뜨고,
 * 결과에 따라 [onGrant] 또는 [onDenied]가 호출된다.
 */
@Composable
fun PermissionManager.rememberPermissionRequester(
    onGrant: () -> Unit = {},
    onDenied: () -> Unit = {},
): (AppPermission) -> Unit {
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    onGrant()
                } else {
                    onDenied()
                }
            },
        )

    return { permission ->
        launcher.launch(permission.manifestName)
    }
}

/**
 * 앱 설정 화면으로 이동하는 함수를 반환한다.
 *
 * 반환된 함수에 [permission]을 넘기면 시스템 설정 화면이 열리고,
 * 돌아왔을 때 해당 권한 허용 여부에 따라 [onGrant] 또는 [onDenied]가 호출된다.
 */
@Composable
fun PermissionManager.rememberAppSettingsLauncher(
    onGrant: () -> Unit = {},
    onDenied: () -> Unit = {},
): (AppPermission) -> Unit {
    val context = LocalContext.current
    val intent =
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null),
        )
    var pendingPermission by remember { mutableStateOf<AppPermission?>(null) }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                val permission = pendingPermission ?: return@rememberLauncherForActivityResult
                if (isGranted(permission)) {
                    onGrant()
                } else {
                    onDenied()
                }
                pendingPermission = null
            },
        )

    return { permission ->
        pendingPermission = permission
        launcher.launch(intent)
    }
}
