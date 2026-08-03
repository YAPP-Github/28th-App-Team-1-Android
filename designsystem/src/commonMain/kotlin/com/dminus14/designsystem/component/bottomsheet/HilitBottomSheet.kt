package com.dminus14.designsystem.component.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 공용 Modal Bottom Sheet.
 *
 * 핸들·스크림·시트 높이만 제공한다. CTA는 포함하지 않으며, 핸들 아래 영역은 [content] 슬롯이다.
 *
 * @param onDismissRequest 시트 밖 클릭·드래그로 닫힐 때 호출된다
 * @param content 핸들 아래 콘텐츠 슬롯
 * @param modifier 시트 컨테이너 Modifier
 * @param sheetState Bottom Sheet 상태
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HilitBottomSheet(
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = RectangleShape,
        containerColor = HilitTheme.colors.hilitWhite,
        contentColor = HilitTheme.colors.hilitWhite,
        scrimColor = HilitBottomSheetScrimColor,
        dragHandle = { HilitBottomSheetDragHandle() },
        content = {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val sheetHeight =
                    if (constraints.hasBoundedHeight) {
                        maxHeight * HILIT_BOTTOM_SHEET_HEIGHT_FRACTION
                    } else {
                        HilitBottomSheetFallbackHeight
                    }
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(sheetHeight),
                    content = content,
                )
            }
        },
    )
}

@Composable
private fun HilitBottomSheetDragHandle(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(HilitBottomSheetHandleContainerHeight),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .width(HilitBottomSheetHandleWidth)
                    .height(HilitBottomSheetHandleHeight)
                    .background(color = HilitTheme.colors.gray400),
        )
    }
}

private val HilitBottomSheetScrimColor = Color.Black.copy(alpha = 0.65f)
private const val HILIT_BOTTOM_SHEET_HEIGHT_FRACTION = 0.81f
private val HilitBottomSheetFallbackHeight = 662.dp
private val HilitBottomSheetHandleContainerHeight = 20.dp
private val HilitBottomSheetHandleWidth = 60.dp
private val HilitBottomSheetHandleHeight = 5.dp

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "HilitBottomSheet", showBackground = true, heightDp = 800)
@Composable
private fun HilitBottomSheetPreview() {
    var isShow by remember { mutableStateOf(false) }

    HilitTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Button(onClick = { isShow = !isShow }) {
                Text("시트 열고 닫기")
            }

            if (isShow) {
                HilitBottomSheet(
                    onDismissRequest = { isShow = false },
                    content = {
                        Text(
                            text = "콘텐츠 슬롯",
                            style = HilitTheme.typography.sub7,
                            color = HilitTheme.colors.hilitBlack800,
                            modifier = Modifier.padding(20.dp),
                        )
                        Text(
                            text = "본문 슬롯 내용",
                            style = HilitTheme.typography.body4,
                            color = HilitTheme.colors.gray800,
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 20.dp),
                        )
                    },
                )
            }
        }
    }
}
