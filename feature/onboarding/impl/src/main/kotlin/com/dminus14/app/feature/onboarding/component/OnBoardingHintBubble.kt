package com.dminus14.app.feature.onboarding.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.bubblefield.BubbleField
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailAlign
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailEdge
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailShape
import com.dminus14.designsystem.component.bubblefield.BubbleFieldType
import kotlinx.coroutines.delay

private const val HINT_BUBBLE_VISIBLE_DURATION_MS = 3_000L

/** Figma bubble-field 고정 폭(`443:9400`). */
private val HintBubbleWidth = 274.dp

/**
 * 온보딩 인터뷰 스텝 하단에 잠깐 노출됐다 3초 후 사라지는 힌트 말풍선.
 *
 * Figma 주석("3초 후 사라짐")에 대응한다. 꼬리는 본문 오른쪽(화면을 세로로 4등분했을 때
 * 4번째 선 부근)에 아래를 향해 붙는다.
 *
 * [BubbleField]의 Big 타입 본문은 내부적으로 `fillMaxWidth()`를 쓰기 때문에, 폭을
 * [HintBubbleWidth]로 직접 고정하지 않으면 이 화면의 남는 공간만큼 늘어나면서 오른쪽 꼬리
 * 위치도 같이 밀려난다.
 */
@Composable
fun OnBoardingHintBubble(
    text: String,
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(HINT_BUBBLE_VISIBLE_DURATION_MS)
        visible = false
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = visible,
            exit = fadeOut(),
        ) {
            BubbleField(
                text = text,
                type = BubbleFieldType.Big,
                tailEdge = BubbleFieldTailEdge.Bottom,
                tailAlign = BubbleFieldTailAlign.Right,
                tailShape = BubbleFieldTailShape.Right,
                modifier = Modifier.width(HintBubbleWidth),
            )
        }
    }
}
