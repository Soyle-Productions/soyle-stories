package com.soyle.stories.desktop.view.project.workspace.window.child

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.soyle.stories.desktop.view.project.workspace.tool.ToolViewModel

const val Horizontal: Boolean = false
const val Vertical: Boolean = ! Horizontal

class WindowChildSplitterViewModel(
    val orientation: Boolean,
    val children: List<Pair<Int, WindowChildViewModel>>
) : WindowChildViewModel() {
    override val isOpen: Boolean
        get() = children.any { it.second.isOpen }

    override val isPrimary: Boolean
        get() = children.any { it.second.isPrimary }

    override val openTools: List<ToolViewModel>
        get() = children.flatMap { it.second.openTools }
}

@Composable
fun rememberWindowChildSplitter(
    orientation: Boolean = remember { Horizontal },
    vararg children: Pair<Int, WindowChildViewModel>
) = rememberWindowChildSplitter(orientation, children.toList())

@Composable
fun rememberWindowChildSplitter(
    orientation: Boolean = remember { Horizontal },
    children: List<Pair<Int, WindowChildViewModel>>
) = remember(children) {
    WindowChildSplitterViewModel(orientation, children)
}