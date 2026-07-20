package catalog.controls.runtime

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class CatalogControlsUiTest {
    @Test
    fun `Control 카드가 변수명과 타입을 표시하고 물음표를 hover할 때만 설명을 표시한다`() =
        runComposeUiTest {
            setContent {
                MaterialTheme {
                    CatalogTextControl(
                        name = "text",
                        value = "initial",
                        onValueChange = {},
                    )
                }
            }

            onNodeWithText("text").assertExists()
            onNodeWithText("String").assertExists()
            onNodeWithText(CatalogControlType.STRING.tooltipText).assertDoesNotExist()

            onNodeWithContentDescription("String 타입 설명")
                .performMouseInput { moveTo(center) }
            onNodeWithText(CatalogControlType.STRING.tooltipText).assertExists()

            onNodeWithContentDescription("String 타입 설명")
                .performMouseInput { exit() }
            onNodeWithText(CatalogControlType.STRING.tooltipText).assertDoesNotExist()
        }

    @Test
    fun `Catalog 타입 설명이 숫자 범위와 디자이너용 의미를 제공한다`() {
        assertEquals("String | 문자열입니다.", CatalogControlType.STRING.tooltipText)
        assertEquals(
            "Boolean | 참 또는 거짓으로만 표현되는 데이터입니다.",
            CatalogControlType.BOOLEAN.tooltipText,
        )
        assertTrue(CatalogControlType.BYTE.tooltipText.contains("-128부터 127까지"))
        assertTrue(CatalogControlType.SHORT.tooltipText.contains("-32,768부터 32,767까지"))
        assertTrue(
            CatalogControlType.INT.tooltipText.contains(
                "-2,147,483,648부터 2,147,483,647까지",
            ),
        )
        assertTrue(
            CatalogControlType.LONG.tooltipText.contains(
                "-9,223,372,036,854,775,808부터 9,223,372,036,854,775,807까지",
            ),
        )
        assertTrue(CatalogControlType.FLOAT.tooltipText.contains("3.4028235 × 10³⁸"))
        assertTrue(CatalogControlType.DOUBLE.tooltipText.contains("1.7976931348623157 × 10³⁰⁸"))
        assertEquals(
            "Enum | 여러 선택지 중 하나를 고를 수 있는 데이터입니다.",
            CatalogControlType.ENUM.tooltipText,
        )
    }

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
                        type = CatalogControlType.INT,
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
