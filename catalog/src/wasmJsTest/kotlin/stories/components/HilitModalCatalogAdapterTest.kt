package stories.components.designsystem

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.dminus14.designsystem.theme.HilitTheme
import stories.components.designsystem.hilitmodal.HilitModalCatalogAdapter
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class HilitModalCatalogAdapterTest {
    @Test
    fun `Controls 편집을 위해 Modal을 닫고 다시 열 수 있다`() =
        runComposeUiTest {
            setContent {
                HilitTheme {
                    HilitModalCatalogAdapter(
                        title = "안내",
                        message = "합성 메시지",
                        confirmText = "확인",
                        dismissible = true,
                    )
                }
            }

            onNodeWithText("확인").performClick()
            onNodeWithText("Modal 열기").performClick()

            onNodeWithText("안내").assertExists()
        }
}
