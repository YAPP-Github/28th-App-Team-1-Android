package catalog.controls.runtime

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class CatalogControlsUiTest {
    @Test
    fun `문자열 Control이 값을 변경한다`() =
        runComposeUiTest {
            var value by mutableStateOf("initial")
            setContent {
                MaterialTheme {
                    CatalogTextControl(
                        name = "text",
                        value = value,
                        onValueChange = { value = it },
                    )
                }
            }

            onNode(hasSetTextAction()).performTextReplacement("updated")

            assertEquals("updated", value)
        }

    @Test
    fun `Boolean Control이 값을 변경한다`() =
        runComposeUiTest {
            var value by mutableStateOf(false)
            setContent {
                MaterialTheme {
                    CatalogBooleanControl(
                        name = "enabled",
                        value = value,
                        onValueChange = { value = it },
                    )
                }
            }

            onNode(isToggleable()).performClick()

            assertTrue(value)
        }

    @Test
    fun `숫자 Control이 입력 원문을 유지하고 오류를 표시한다`() =
        runComposeUiTest {
            var rawValue by mutableStateOf("1")
            var lastValidValue by mutableStateOf(1)
            var errorMessage by mutableStateOf<String?>(null)
            setContent {
                MaterialTheme {
                    CatalogNumberControl(
                        name = "count",
                        rawValue = rawValue,
                        errorMessage = errorMessage,
                        onValueChange = { changedValue ->
                            rawValue = changedValue
                            val parsed = parseCatalogInt(changedValue)
                            errorMessage = parsed.errorMessage
                            parsed.value?.let { lastValidValue = it }
                        },
                    )
                }
            }

            onNode(hasSetTextAction()).performTextReplacement("-")

            assertEquals("-", rawValue)
            assertEquals(1, lastValidValue)
            assertFalse(errorMessage.isNullOrBlank())
            onNodeWithText(errorMessage.orEmpty()).assertExists()
        }

    @Test
    fun `enum Control이 하나의 항목을 선택한다`() =
        runComposeUiTest {
            var value by mutableStateOf(TestStyle.Primary)
            setContent {
                MaterialTheme {
                    CatalogEnumControl(
                        name = "style",
                        value = value,
                        options = TestStyle.entries.toList(),
                        onValueChange = { value = it },
                    )
                }
            }

            onNodeWithText(TestStyle.Primary.name).performClick()
            onNodeWithText(TestStyle.Secondary.name).performClick()

            assertEquals(TestStyle.Secondary, value)
        }

    @Test
    fun `초기 인자 오류를 해당 Story 레이아웃 안에 표시한다`() =
        runComposeUiTest {
            setContent {
                MaterialTheme {
                    CatalogControlledStoryLayout(
                        preview = { CatalogPreviewUnavailable() },
                        controls = { CatalogControlsError("ratio=NaN") },
                    )
                }
            }

            onNodeWithText("초기 인자가 올바르지 않아 Preview를 표시할 수 없습니다.").assertExists()
            onNodeWithText("ratio=NaN").assertExists()
        }

    private enum class TestStyle {
        Primary,
        Secondary,
    }
}
