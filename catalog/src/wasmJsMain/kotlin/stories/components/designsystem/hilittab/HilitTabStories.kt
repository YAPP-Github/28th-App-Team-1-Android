package stories.components.designsystem.hilittab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.tab.HilitTab
import com.dminus14.designsystem.component.tab.HilitTabItem
import com.dminus14.designsystem.component.tab.HilitTabRow
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val HilitTabStories =
    StoryGroup(
        path = "Components/HilitTab",
        description = "단일 탭의 세 상태와 2~5개 동일 너비 탭 행.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "문구, 선택 여부와 활성 여부를 조작한다.",
                ) {
                    HilitTabCatalogAdapterControls(
                        initialArgs =
                            HilitTabCatalogAdapterArgs(
                                text = "텍스트",
                                selected = true,
                                enabled = true,
                            ),
                    )
                },
                Story(
                    id = "states",
                    title = "전체 상태",
                    description = "선택, 기본, 비활성 상태를 비교한다.",
                ) {
                    HilitTheme {
                        Row {
                            HilitTab(text = "선택", selected = true, onClick = {})
                            HilitTab(text = "기본", selected = false, onClick = {})
                            HilitTab(
                                text = "비활성",
                                selected = false,
                                enabled = false,
                                onClick = {},
                            )
                        }
                    }
                },
                Story(
                    id = "rows",
                    title = "탭 행",
                    description = "2~5개 탭의 동일 너비와 계산된 간격 및 선택 상호작용을 비교한다.",
                ) {
                    HilitTheme {
                        Column(
                            modifier = Modifier.width(480.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            (2..5).forEach { itemCount ->
                                TabRowExample(itemCount = itemCount)
                            }
                        }
                    }
                },
            ),
    )

@Composable
private fun TabRowExample(itemCount: Int) {
    var selectedIndex by remember(itemCount) { mutableIntStateOf(0) }
    val items = remember(itemCount) { List(itemCount) { HilitTabItem(text = "탭 ${it + 1}") } }

    HilitTabRow(
        items = items,
        selectedIndex = selectedIndex,
        onTabSelected = { selectedIndex = it },
    )
}
