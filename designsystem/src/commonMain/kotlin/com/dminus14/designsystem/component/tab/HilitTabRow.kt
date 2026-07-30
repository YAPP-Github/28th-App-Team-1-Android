package com.dminus14.designsystem.component.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

@Immutable
data class HilitTabItem(
    val text: String,
    val enabled: Boolean = true,
)

@Composable
fun HilitTabRow(
    items: List<HilitTabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    validateHilitTabRow(items = items, selectedIndex = selectedIndex)

    Row(
        modifier = modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(hilitTabRowGap(items.size)),
    ) {
        items.forEachIndexed { index, item ->
            HilitTab(
                text = item.text,
                selected = index == selectedIndex,
                enabled = item.enabled,
                onClick = { onTabSelected(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

internal fun hilitTabRowGap(itemCount: Int): Dp {
    require(itemCount in MIN_TAB_COUNT..MAX_TAB_COUNT) {
        "HilitTabRow item count must be between $MIN_TAB_COUNT and $MAX_TAB_COUNT."
    }
    return (TOTAL_TAB_ROW_SPACING.value / (itemCount - 1)).dp
}

internal fun validateHilitTabRow(
    items: List<HilitTabItem>,
    selectedIndex: Int,
) {
    require(items.size in MIN_TAB_COUNT..MAX_TAB_COUNT) {
        "HilitTabRow item count must be between $MIN_TAB_COUNT and $MAX_TAB_COUNT."
    }
    require(selectedIndex in items.indices) {
        "HilitTabRow selectedIndex must reference an item."
    }
    require(items[selectedIndex].enabled) {
        "HilitTabRow selected item must be enabled."
    }
}

private const val MIN_TAB_COUNT = 2
private const val MAX_TAB_COUNT = 5
private val TOTAL_TAB_ROW_SPACING = 52.dp

@Preview(name = "HilitTabRow")
@Composable
private fun HilitTabRowPreview() {
    HilitTheme {
        var selectedIndex by remember { mutableIntStateOf(0) }
        val items =
            listOf(
                HilitTabItem(text = "첫 번째"),
                HilitTabItem(text = "두 번째"),
                HilitTabItem(text = "세 번째"),
            )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HilitTabRow(
                items = items,
                selectedIndex = selectedIndex,
                onTabSelected = { selectedIndex = it },
            )
        }
    }
}
