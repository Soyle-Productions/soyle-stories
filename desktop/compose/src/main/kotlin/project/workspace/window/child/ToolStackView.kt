package com.soyle.stories.desktop.view.project.workspace.window.child

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun ToolStackView(
    viewModel: ToolStackViewModel,
    modifier: Modifier = Modifier
) {
    val openTools = viewModel.openTools
    if (openTools.isEmpty()) {
        if (! viewModel.isPrimary) return
        Box(modifier = modifier) {
            Text("Primary Stack")
        }
        return
    }

    val (selectedIndex, setSelectedIndex) = remember { mutableStateOf(0) }

    Column(
        modifier = modifier
    ) {
        TabRow(
            selectedTabIndex = selectedIndex,
        ) {
            openTools.forEachIndexed { index, tool ->
                Tab(index == selectedIndex, onClick = { setSelectedIndex(index) }, text = { Text(tool.name) })
            }
        }
        openTools[selectedIndex].let {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(it.name)
            }
        }
    }
}