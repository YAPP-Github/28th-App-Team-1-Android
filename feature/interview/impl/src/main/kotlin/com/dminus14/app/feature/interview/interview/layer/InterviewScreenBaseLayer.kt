package com.dminus14.app.feature.interview.interview.layer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interview.component.InterviewCameraFrame
import com.dminus14.app.feature.interview.component.InterviewCameraPreview
import com.dminus14.app.feature.interview.component.VideoOverlay
import com.dminus14.app.feature.interview.component.VideoOverlayDirection
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun InterviewScreenBaseLayer(
    isCameraPermissionGranted: Boolean,
    modifier: Modifier = Modifier,
    onCameraBindingFailed: () -> Unit = {},
) {
    Box(
        modifier = modifier,
    ) {
        // Camera View
        InterviewCameraPreview(
            isCameraPermissionGranted = isCameraPermissionGranted,
            onCameraBindingFailed = onCameraBindingFailed,
        )

        // Video Overlays
        VideoOverlay(
            direction = VideoOverlayDirection.TOP,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(229.dp)
                    .align(Alignment.TopCenter),
        )
        VideoOverlay(
            direction = VideoOverlayDirection.BOTTOM,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(229.dp)
                    .align(Alignment.BottomCenter),
        )

        // Camera Frame
        InterviewCameraFrame(modifier = Modifier.align(Alignment.Center))
    }
}

@Preview
@Composable
private fun InterviewScreenBaseLayerPreview() {
    HilitTheme {
        InterviewScreenBaseLayer(isCameraPermissionGranted = true)
    }
}
