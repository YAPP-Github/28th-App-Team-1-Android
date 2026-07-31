package com.dminus14.app.feature.login.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.bottomsheet.HilitBottomSheet
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 약관 상세 보기 Bottom Sheet.
 *
 * [HilitBottomSheet] content 슬롯에 약관 타이틀·본문 레이아웃을 채운다.
 *
 * @param title 약관 제목
 * @param body 약관 본문
 * @param onDismissRequest 시트 밖 클릭·드래그로 닫힐 때 호출된다
 * @param modifier 시트 컨테이너 Modifier
 * @param sheetState Bottom Sheet 상태
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermBottomSheet(
    title: String,
    body: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    HilitBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        content = {
            Text(
                text = title,
                style = HilitTheme.typography.sub7,
                color = HilitTheme.colors.hilitBlack800,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = TermBottomSheetHorizontalPadding,
                            vertical = TermBottomSheetTitleVerticalPadding,
                        ),
            )
            Text(
                text = body,
                style = HilitTheme.typography.body4,
                color = HilitTheme.colors.gray800,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = TermBottomSheetHorizontalPadding)
                        .padding(bottom = TermBottomSheetContentBottomPadding),
            )
        },
    )
}

private val TermBottomSheetHorizontalPadding = 20.dp
private val TermBottomSheetTitleVerticalPadding = 10.dp
private val TermBottomSheetContentBottomPadding = 20.dp

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "TermBottomSheet", showBackground = true)
@Composable
private fun TermBottomSheetPreview() {
    HilitTheme {
        TermBottomSheet(
            title = "서비스 이용 약관",
            body =
                "이 문구는 실제 사용자 데이터가 아닌 합성 예시입니다. " +
                    "약관 본문이 여러 줄로 표시되는 레이아웃을 확인하기 위해 사용합니다.",
            onDismissRequest = {},
        )
    }
}
