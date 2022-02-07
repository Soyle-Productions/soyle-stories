package com.soyle.stories.desktop.view.project.workspace.window.child

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.soyle.stories.desktop.view.project.workspace.tool.ToolViewModel

class ToolStackViewModel(
    val tools: List<ToolViewModel>,
    override val isPrimary: Boolean
) : WindowChildViewModel() {

    override val isOpen: Boolean get() = isPrimary || openTools.isNotEmpty()

    override val openTools: List<ToolViewModel>
        get() = tools.filter(ToolViewModel::isOpen)
}

@Composable
fun rememberToolStack(
    vararg tools: ToolViewModel,
    isPrimary: Boolean = remember { false }
) = rememberToolStack(tools.toList(), isPrimary)

@Composable
fun rememberToolStack(
    tools: List<ToolViewModel>,
    isPrimary: Boolean = remember { false }
) = remember(tools, isPrimary) {
    ToolStackViewModel(tools, isPrimary)
}