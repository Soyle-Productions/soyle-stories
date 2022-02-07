package com.soyle.stories.desktop.view.project.workspace.window.child

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.HorizontalSplitPane

@Composable
fun WindowChildSplitterView(
    viewModel: WindowChildSplitterViewModel,
    modifier: Modifier = Modifier,
) {
    val openChildren = viewModel.children.filter { it.second.isOpen }
    if (openChildren.isEmpty()) return

    when (viewModel.orientation) {
        Horizontal -> Row(modifier = modifier) {
            openChildren.forEachIndexed { index, (weight, child) ->
                WindowChildView(child, modifier = Modifier.weight(weight.toFloat()))
                if (index != openChildren.lastIndex) {
                    Box(Modifier.fillMaxHeight().width(2.dp).background(Color.Red))
                }
            }
        }
        else -> Column(modifier = modifier) {
            openChildren.forEachIndexed { index, (weight, child) ->
                WindowChildView(child, modifier = Modifier.weight(weight.toFloat()))
                if (index != openChildren.lastIndex) {
                    Box(Modifier.fillMaxWidth().height(2.dp).background(Color.Red))
                }
            }
        }
    }
}