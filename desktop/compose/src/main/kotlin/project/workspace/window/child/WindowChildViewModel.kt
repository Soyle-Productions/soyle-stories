package com.soyle.stories.desktop.view.project.workspace.window.child

import androidx.compose.runtime.Composable
import com.soyle.stories.desktop.view.project.workspace.tool.ToolViewModel

sealed class WindowChildViewModel {
    abstract val isOpen: Boolean
    abstract val isPrimary: Boolean
    abstract val openTools: List<ToolViewModel>
}