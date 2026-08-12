package com.dminus14.app.feature.interview.component

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dminus14.designsystem.theme.HilitTheme
import androidx.compose.ui.tooling.preview.Preview as ComposePreview

private const val MOCK_TEXT_REPEAT_COUNT = 4

/**
 * 전면 카메라 프리뷰 (Video-only, Portrait Fixed).
 *
 * `@Preview` 환경이나 카메라 미작동 시 Mock Placeholder UI를 노출하여 화면 렌더링을 보장한다.
 */
@Composable
fun InterviewCameraPreview(
    isCameraPermissionGranted: Boolean,
    videoCapture: VideoCapture<Recorder>,
    modifier: Modifier = Modifier,
    onCameraReady: () -> Unit = {},
    onCameraBindingFailed: () -> Unit = {},
) {
    val isPreview = LocalInspectionMode.current

    if (isPreview || !isCameraPermissionGranted) {
        CameraPreviewMock(modifier = modifier)
    } else {
        CameraPreviewReal(
            videoCapture = videoCapture,
            modifier = modifier,
            onCameraReady = onCameraReady,
            onCameraBindingFailed = onCameraBindingFailed,
        )
    }
}

@Composable
private fun CameraPreviewReal(
    videoCapture: VideoCapture<Recorder>,
    onCameraReady: () -> Unit,
    modifier: Modifier = Modifier,
    onCameraBindingFailed: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnCameraBindingFailed by rememberUpdatedState(onCameraBindingFailed)
    val currentOnCameraReady by rememberUpdatedState(onCameraReady)
    val previewView =
        remember {
            PreviewView(context).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                // SurfaceView(PERFORMANCE 모드)는 Compose가 그리는 다른 레이어 위에 항상
                // 얹혀 그려져 카메라 위에 겹치는 오버레이(VideoOverlay, InterviewCameraFrame 등)를
                // 가리므로 TextureView 기반 COMPATIBLE 모드를 사용한다.
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        }

    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)
        val preview =
            Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
        var cameraProvider: ProcessCameraProvider? = null
        var isDisposed = false

        cameraProviderFuture.addListener(
            {
                if (isDisposed) return@addListener

                try {
                    cameraProvider = cameraProviderFuture.get()
                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                    cameraProvider?.unbind(preview, videoCapture)
                    cameraProvider?.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        videoCapture,
                    )
                    currentOnCameraReady()
                } catch (_: Exception) {
                    currentOnCameraBindingFailed()
                }
            },
            executor,
        )

        onDispose {
            isDisposed = true
            try {
                cameraProvider?.unbind(preview, videoCapture)
            } catch (_: Exception) {
                // Ignore cleanup error
            }
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
private fun CameraPreviewMock(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = HilitTheme.colors.hilitWhite)
                .padding(vertical = 64.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        repeat(MOCK_TEXT_REPEAT_COUNT) {
            Text(
                text = "Camera Self-View (Preview Mode)",
                color = HilitTheme.colors.gray300,
                style = HilitTheme.typography.body6,
            )
        }
    }
}

@ComposePreview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun CameraPreviewMockPreview() {
    HilitTheme {
        CameraPreviewMock()
    }
}
