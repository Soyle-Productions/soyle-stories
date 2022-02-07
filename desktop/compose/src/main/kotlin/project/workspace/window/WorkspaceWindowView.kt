package com.soyle.stories.desktop.view.project.workspace.window

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import com.soyle.stories.desktop.view.project.workspace.window.child.WindowChildView

@Composable
fun WorkspaceWindowView(
    viewModel: WorkspaceWindowViewModel
) {
    if (viewModel.isOpen) {
        Window(onCloseRequest = viewModel::closeWindow) {
            WindowChildView(viewModel.child)
        }
    }
}