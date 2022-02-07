package com.soyle.stories.desktop.view.project.workspace.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.soyle.stories.desktop.view.di.koin
import com.soyle.stories.desktop.view.project.workspace.tool.ToolViewModel
import com.soyle.stories.desktop.view.project.workspace.window.child.WindowChildViewModel
import com.soyle.stories.domain.project.Project
import com.soyle.stories.project.closeProject.CloseProjectController

class WorkspaceWindowViewModel(
    val projectId: Project.Id,
    val child: WindowChildViewModel,
    private val closeProjectController: CloseProjectController
) {

    val isOpen: Boolean get() = child.isOpen
    val isPrimary: Boolean get() = child.isPrimary

    fun closeWindow() {
        if (isPrimary) closeProjectController.closeProject(projectId)
        else child.openTools.forEach(ToolViewModel::close)
    }

}

@Composable
fun rememberWorkspaceWindow(
    projectId: Project.Id,
    child: WindowChildViewModel,
    closeProjectController: CloseProjectController = koin.get()
) = remember(child) {
    WorkspaceWindowViewModel(
        projectId,
        child,
        closeProjectController
    )
}
