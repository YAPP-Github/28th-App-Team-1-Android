package stories.components.designsystem.termbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.term.TermBox
import com.dminus14.designsystem.component.term.TermBoxType
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val TermBoxStories =
    StoryGroup(
        path = "Components/TermBox",
        description =
            "약관 동의 행. AllAgree(sub7), Text(body3), Term(body3 + 보기) 타입과 " +
                "checked 상태를 제공한다. `보기`를 제외한 행 전체가 하나의 토글 영역이라 " +
                "체크박스·문구 어디를 눌러도 체크가 토글된다.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description =
                        "type / text / checked를 조작하는 TermBox. 행을 누르면 토글되고 " +
                            "Term 타입의 `보기`만 별도 콜백을 호출한다.",
                ) {
                    TermBoxCatalogAdapterControls(
                        initialArgs =
                            TermBoxCatalogAdapterArgs(
                                type = TermBoxType.AllAgree,
                                text = "모두 동의합니다.",
                                checked = false,
                            ),
                    )
                },
                Story(
                    id = "variants",
                    title = "타입 · 체크 상태",
                    description = "AllAgree / Text / Term × unchecked / checked를 비교한다.",
                ) {
                    HilitTheme {
                        Column(
                            modifier = Modifier.width(335.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            TermBoxVariant(
                                type = TermBoxType.AllAgree,
                                text = "모두 동의합니다.",
                                checked = false,
                            )
                            TermBoxVariant(
                                type = TermBoxType.AllAgree,
                                text = "모두 동의합니다.",
                                checked = true,
                            )
                            TermBoxVariant(
                                type = TermBoxType.Text,
                                text = "(필수) 만 14세 이상입니다.",
                                checked = false,
                            )
                            TermBoxVariant(
                                type = TermBoxType.Text,
                                text = "(필수) 만 14세 이상입니다.",
                                checked = true,
                            )
                            TermBoxVariant(
                                type = TermBoxType.Term,
                                text = "(필수) 서비스 이용약관 동의",
                                checked = false,
                            )
                            TermBoxVariant(
                                type = TermBoxType.Term,
                                text = "(필수) 서비스 이용약관 동의",
                                checked = true,
                            )
                        }
                    }
                },
            ),
    )

@Composable
private fun TermBoxVariant(
    type: TermBoxType,
    text: String,
    checked: Boolean,
) {
    TermBox(
        type = type,
        text = text,
        checked = checked,
        onClick = {},
        onViewClick = {},
    )
}
