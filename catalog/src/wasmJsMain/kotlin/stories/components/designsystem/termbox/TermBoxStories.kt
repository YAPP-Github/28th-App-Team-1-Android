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
                "checked 상태를 제공한다.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "type / text / checked를 조작하는 TermBox.",
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
