package com.dminus14.app.core.permission

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/**
 * [permissions]를 앞에서부터 순서대로(예: 마이크 → 카메라) 시스템 권한 다이얼로그로 요청하는
 * 함수를 반환한다.
 *
 * 반환된 함수를 호출하면 첫 권한부터 요청하며, 각 권한이 허용되면 다음 권한을 이어서 요청한다.
 * - 모든 권한이 허용되면 [onAllGranted] 호출.
 * - 하나라도 거부되면 즉시 중단하고 [onAnyDenied] 호출.
 *
 * 이미 허용된 권한은 시스템이 즉시 granted로 반환하므로 자연스럽게 통과한다.
 */
@Composable
fun PermissionManager.rememberSequentialPermissionRequester(
    permissions: List<AppPermission>,
    onAllGranted: () -> Unit = {},
    onAnyDenied: () -> Unit = {},
): () -> Unit {
    var index by remember { mutableIntStateOf(-1) }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                when {
                    !granted -> {
                        index = -1
                        onAnyDenied()
                    }

                    index >= permissions.lastIndex -> {
                        index = -1
                        onAllGranted()
                    }

                    else -> {
                        index += 1
                    }
                }
            },
        )

    // index가 유효 범위로 바뀔 때마다 해당 권한을 요청한다(런처를 콜백에서 자기참조하지 않기 위함).
    LaunchedEffect(index) {
        if (index in permissions.indices) {
            launcher.launch(permissions[index].manifestName)
        }
    }

    return {
        when {
            // 이미 전부 허용된 경우 다이얼로그 없이 즉시 통과한다.
            permissions.all { isGranted(it) } -> onAllGranted()

            else -> index = 0
        }
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
