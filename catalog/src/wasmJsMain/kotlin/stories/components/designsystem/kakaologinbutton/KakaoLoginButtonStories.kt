package stories.components.designsystem.kakaologinbutton

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.button.KakaoLoginButton
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val KakaoLoginButtonStories =
    StoryGroup(
        path = "Components/KakaoLoginButton",
        description =
            "카카오 로그인 Large 버튼. 카카오 옐로우 배경에 KakaoLogo 아이콘과 " +
                "sub7 / hilitBlack900 텍스트를 가운데 정렬한다.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "고정 문구 \"카카오톡으로 로그인\"을 사용하는 기본 상태.",
                ) {
                    HilitTheme {
                        KakaoLoginButton(
                            modifier = Modifier.width(360.dp),
                            onClick = {},
                        )
                    }
                },
            ),
    )
