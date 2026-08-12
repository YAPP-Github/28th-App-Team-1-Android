package com.dminus14.app.feature.interviewreport.player

import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.dminus14.app.feature.interviewreport.model.PlayerContentUiModel
import com.dminus14.app.feature.interviewreport.model.PlayerScriptLineUiModel
import com.dminus14.app.feature.interviewreport.model.PlayerSegmentUiModel
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.loading.HilitLoadingIndicator
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.delay

private const val POSITION_POLL_INTERVAL_MS = 250L

/**
 * 영상 플레이어 화면 (Figma Node: 443:7804 / 443:7902).
 *
 * ExoPlayer 로 합성 영상을 재생하고, 카드(질문)별 구간으로 나눈 세그먼트 진행바와 대본 오버레이를
 * 제공한다. 대본 오버레이는 최상위 script 타임라인으로 현재 발화 라인을 강조한다.
 */
@Composable
fun InterviewReportPlayerScreen(
    sessionId: Long,
    startSec: Float?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InterviewReportPlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(sessionId) {
        viewModel.bindSessionId(sessionId)
        viewModel.onIntent(InterviewReportPlayerIntent.Load)
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                InterviewReportPlayerEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    InterviewReportPlayerContent(
        state = state,
        startSec = startSec,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
private fun InterviewReportPlayerContent(
    state: InterviewReportPlayerState,
    startSec: Float?,
    onIntent: (InterviewReportPlayerIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    Box(modifier = modifier.fillMaxSize().background(colors.hilitBlack900)) {
        when (val phase = state.phase) {
            InterviewReportPlayerState.Phase.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    HilitLoadingIndicator()
                }
            }

            InterviewReportPlayerState.Phase.Failed -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clickable { onIntent(InterviewReportPlayerIntent.ClickClose) }
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "영상을 불러오지 못했어요. 화면을 눌러 돌아가요.",
                        style = HilitTheme.typography.body7,
                        color = colors.gray300,
                    )
                }
            }

            is InterviewReportPlayerState.Phase.Ready -> {
                PlayerReady(
                    content = phase.content,
                    startSec = startSec,
                    transcriptVisible = state.transcriptVisible,
                    onIntent = onIntent,
                )
            }
        }
    }
}

@Composable
private fun PlayerReady(
    content: PlayerContentUiModel,
    startSec: Float?,
    transcriptVisible: Boolean,
    onIntent: (InterviewReportPlayerIntent) -> Unit,
) {
    val colors = HilitTheme.colors
    val context = LocalContext.current
    val url = content.videoUrl
    val player = remember { ExoPlayer.Builder(context).build() }
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }
    LaunchedEffect(url) {
        if (url.isNullOrBlank()) return@LaunchedEffect
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        startSec?.let { player.seekTo((it * 1000f).toLong()) }
        player.playWhenReady = true
    }
    var positionMs by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            positionMs = player.currentPosition
            delay(POSITION_POLL_INTERVAL_MS)
        }
    }

    Box(Modifier.fillMaxSize()) {
        if (!url.isNullOrBlank()) {
            AndroidView(
                factory = { ctx -> SurfaceView(ctx).also(player::setVideoSurfaceView) },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "영상이 만료되어 재생할 수 없어요.",
                    style = HilitTheme.typography.body7,
                    color = colors.gray300,
                )
            }
        }

        HilitIcon(
            asset = HilitIconAsset.Cancel,
            contentDescription = "닫기",
            tint = colors.hilitWhite,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(20.dp)
                    .clickable { onIntent(InterviewReportPlayerIntent.ClickClose) },
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (content.segments.isNotEmpty()) {
                SegmentedProgressBar(
                    segments = content.segments,
                    positionMs = positionMs,
                    onSeek = { player.seekTo(it) },
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TranscriptToggleButton(
                    onClick = { onIntent(InterviewReportPlayerIntent.ToggleTranscript) },
                )
            }
        }

        if (transcriptVisible) {
            TranscriptOverlay(
                scriptLines = content.scriptLines,
                positionMs = positionMs,
                onClose = { onIntent(InterviewReportPlayerIntent.ToggleTranscript) },
            )
        }
    }
}

@Composable
private fun SegmentedProgressBar(
    segments: List<PlayerSegmentUiModel>,
    positionMs: Long,
    onSeek: (Long) -> Unit,
) {
    val colors = HilitTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        segments.forEach { segment ->
            val span = (segment.endMs - segment.startMs).coerceAtLeast(1L)
            val fraction =
                ((positionMs - segment.startMs).toFloat() / span.toFloat()).coerceIn(0f, 1f)
            Box(
                modifier =
                    Modifier
                        .weight(span.toFloat())
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(colors.gray700)
                        .clickable { onSeek(segment.startMs) },
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(fraction)
                            .fillMaxHeight()
                            .background(colors.hilitGreen500),
                )
            }
        }
    }
}

@Composable
private fun TranscriptToggleButton(onClick: () -> Unit) {
    val colors = HilitTheme.colors
    Box(
        modifier =
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.hilitBlack800)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        HilitIcon(
            asset = HilitIconAsset.Script,
            contentDescription = "대본 보기",
            tint = colors.hilitWhite,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun TranscriptOverlay(
    scriptLines: List<PlayerScriptLineUiModel>,
    positionMs: Long,
    onClose: () -> Unit,
) {
    val colors = HilitTheme.colors
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.65f)),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            scriptLines.forEach { line ->
                val current = positionMs in line.startMs until line.endMs
                Text(
                    text = line.text,
                    style = HilitTheme.typography.body3,
                    color = if (current) colors.hilitWhite else colors.gray500,
                )
            }
        }
        HilitIcon(
            asset = HilitIconAsset.Cancel,
            contentDescription = "대본 닫기",
            tint = colors.hilitWhite,
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .clickable(onClick = onClose),
        )
    }
}

// Ready 프레임은 ExoPlayer 를 생성하므로 Preview 불가 → 로딩/실패 상태만 미리본다.
@Preview(name = "플레이어 - 로딩")
@Composable
private fun InterviewReportPlayerLoadingPreview() {
    HilitTheme {
        InterviewReportPlayerContent(
            state = InterviewReportPlayerState(phase = InterviewReportPlayerState.Phase.Loading),
            startSec = null,
            onIntent = {},
        )
    }
}

@Preview(name = "플레이어 - 실패")
@Composable
private fun InterviewReportPlayerFailedPreview() {
    HilitTheme {
        InterviewReportPlayerContent(
            state = InterviewReportPlayerState(phase = InterviewReportPlayerState.Phase.Failed),
            startSec = null,
            onIntent = {},
        )
    }
}
