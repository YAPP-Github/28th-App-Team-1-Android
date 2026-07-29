package stories.components.designsystem.hilitbottomoutlinedtextfield

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.textfield.HilitBottomOutlinedTextField
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val HilitBottomOutlinedTextFieldStories =
    StoryGroup(
        path = "Components/HilitBottomOutlinedTextField",
        description = "표시 문자열의 실제 폭에 맞춰 너비가 변하는 하단 외곽선 TextField.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "Placeholder와 초기 입력값을 바꾸고 실제 입력에 따른 너비 변화를 확인. 포커스 아웃 시 회색으로 바뀜.",
                ) {
                    HilitBottomOutlinedTextFieldCatalogAdapterControls(
                        initialArgs =
                            HilitBottomOutlinedTextFieldCatalogAdapterArgs(
                                placeholder = "이름을 알려주세요",
                                initialValue = "",
                            ),
                    )
                },
                Story(
                    id = "figma-reference",
                    title = "Figma 기준",
                    description = "비활성 Placeholder와 값이 있는 활성 상태를 함께 비교.",
                ) {
                    HilitTheme {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            HilitBottomOutlinedTextField(
                                value = "",
                                onValueChange = {},
                                placeholder = "이름을 알려주세요",
                            )
                            HilitBottomOutlinedTextField(
                                value = "박민",
                                onValueChange = {},
                                placeholder = "이름을 알려주세요",
                            )
                        }
                    }
                },
                Story(
                    id = "dynamic-width",
                    title = "동적 너비",
                    description = "한글·영문·빈 문자열에서 실제 글리프 폭으로 계산되는 너비를 비교.",
                ) {
                    HilitTheme {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            EditableBottomOutlinedTextField(initialValue = "가나다")
                            EditableBottomOutlinedTextField(initialValue = "WWW")
                            EditableBottomOutlinedTextField(initialValue = "iii")
                            EditableBottomOutlinedTextField(initialValue = "", placeholder = "")
                        }
                    }
                },
                Story(
                    id = "constrained-width",
                    title = "제한된 너비",
                    description = "긴 입력이 부모 최대 폭에 도달하면 내부에서 가로 스크롤하는 상태.",
                ) {
                    HilitTheme {
                        EditableBottomOutlinedTextField(
                            initialValue = "부모 너비보다 긴 한 줄 입력을 계속 작성해 보세요",
                            modifier = Modifier.widthIn(max = 240.dp),
                        )
                    }
                },
            ),
    )

@Composable
private fun EditableBottomOutlinedTextField(
    initialValue: String,
    modifier: Modifier = Modifier,
    placeholder: String = "이름을 알려주세요",
) {
    var value by remember(initialValue) { mutableStateOf(initialValue) }

    HilitBottomOutlinedTextField(
        value = value,
        onValueChange = { value = it },
        placeholder = placeholder,
        modifier = modifier,
    )
}
