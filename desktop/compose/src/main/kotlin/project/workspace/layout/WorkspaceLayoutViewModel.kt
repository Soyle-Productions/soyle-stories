package com.soyle.stories.desktop.view.project.workspace.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.soyle.stories.desktop.view.project.workspace.window.WorkspaceWindowViewModel

class WorkspaceLayoutViewModel(
    val windows: List<WorkspaceWindowViewModel>
) {


}

@Composable
fun rememberWorkspaceLayout(
    vararg windows: WorkspaceWindowViewModel
): WorkspaceLayoutViewModel =
    rememberWorkspaceLayout(remember { windows.toList() })

@Composable
fun rememberWorkspaceLayout(
    windows: List<WorkspaceWindowViewModel> = remember { emptyList() }
): WorkspaceLayoutViewModel = remember(windows) {
    WorkspaceLayoutViewModel(windows)
}
