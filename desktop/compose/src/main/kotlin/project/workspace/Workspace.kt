package com.soyle.stories.desktop.view.project.workspace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import com.soyle.stories.desktop.view.project.workspace.layout.WorkspaceLayoutViewModel
import com.soyle.stories.desktop.view.project.workspace.layout.rememberWorkspaceLayout
import com.soyle.stories.desktop.view.project.workspace.tool.ToolViewModel
import com.soyle.stories.desktop.view.project.workspace.window.WorkspaceWindowView
import com.soyle.stories.desktop.view.project.workspace.window.child.Horizontal
import com.soyle.stories.desktop.view.project.workspace.window.child.Vertical
import com.soyle.stories.desktop.view.project.workspace.window.child.rememberToolStack
import com.soyle.stories.desktop.view.project.workspace.window.child.rememberWindowChildSplitter
import com.soyle.stories.desktop.view.project.workspace.window.rememberWorkspaceWindow
import com.soyle.stories.domain.project.Project
import com.soyle.stories.workspace.entities.Workspace
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects

@Composable
fun ApplicationScope.Workspace(
    projectItem: ListOpenProjects.OpenProjectItem
) {
    val workspaceLayout = rememberWorkspaceLayout(
        rememberWorkspaceWindow(
            Project.Id(projectItem.projectId),
            rememberWindowChildSplitter(
                Vertical,
                4 to rememberWindowChildSplitter(
                    Horizontal,
                    2 to rememberWindowChildSplitter(
                        Vertical,
                        1 to rememberToolStack(
                            ToolViewModel(open = false, "Properties"),
                            ToolViewModel(open = false, "Character Development"),
                            ToolViewModel(open = false, "Location Tracking")
                        ),
                        2 to rememberToolStack(
                            ToolViewModel(open = true, "Story Events"),
                            ToolViewModel(open = true, "Locations"),
                            ToolViewModel(open = true, "Characters")
                        )
                    ),
                    6 to rememberToolStack(isPrimary = true),
                    2 to rememberWindowChildSplitter(
                        Vertical,
                        2 to rememberToolStack(
                            ToolViewModel(open = true, "Scenes"),
                            ToolViewModel(open = true, "Themes"),
                            ToolViewModel(open = true, "Notes")
                        ),
                        2 to rememberToolStack(
                            ToolViewModel(open = true, "Scene Characters"),
                            ToolViewModel(open = true, "Scene Setting"),
                            ToolViewModel(open = true, "Scene Outline")
                        )
                    )
                ),
                2 to rememberToolStack(
                    ToolViewModel(open = true, "Timeline")
                )
            )
        ),
    )

    workspaceLayout.windows.forEach {
        WorkspaceWindowView(it)
    }
}