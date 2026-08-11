package com.dminus14.app.error

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.modal.GlobalModalRequest
import com.dminus14.app.core.common.modal.GlobalModalResult
import com.dminus14.app.core.common.modal.showGlobalModal

@Composable
fun GlobalErrorHost(
    onExit: () -> Unit,
    onGlobalEventRendered: (String) -> Unit = {},
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        GlobalErrorHandler.events.collect { envelope ->
            when (envelope.event) {
                GlobalAppEvent.ShowNetworkErrorAndExit,
                GlobalAppEvent.ShowServerErrorAndExit,
                -> {
                    val result =
                        showGlobalModal(
                            GlobalModalRequest(
                                title = "연결 오류",
                                message = "네트워크 또는 서버 오류가 발생했어요. 잠시 후 다시 시도해주세요.",
                                confirmText = "종료하기",
                                dismissible = false,
                            ),
                        )
                    envelope.deliveryId?.let(onGlobalEventRendered)
                    if (result == GlobalModalResult.Confirm) onExit()
                }

                GlobalAppEvent.ShowUnknownError -> {
                    Toast.makeText(context, "알 수 없는 오류가 발생했어요.", Toast.LENGTH_SHORT).show()
                    envelope.deliveryId?.let(onGlobalEventRendered)
                }
            }
        }
    }
}
