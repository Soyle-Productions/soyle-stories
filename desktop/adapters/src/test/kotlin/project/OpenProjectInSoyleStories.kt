package com.soyle.stories.desktop.adapter.project

import com.soyle.stories.desktop.adapter.tools.OpenToolInProject
import com.soyle.stories.desktop.adapter.tools.ToolsListEvent
import com.soyle.stories.desktop.adapter.tools.toolsReducer
import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.scene.list.ListAllScenes

data class OpenProjectInSoyleStories(
    val id: Project.Id,
    val name: String,
    val sceneList: List<ListAllScenes.SceneListItem>? = null,
    val tools: List<OpenToolInProject> = listOf()
)

fun projectsReducer(state: List<OpenProjectInSoyleStories>, action: ProjectListEvent) =
    when (action) {
        is ProjectOpened -> state + OpenProjectInSoyleStories(action.projectId, action.projectName)
        is ProjectClosed -> state.filterNot { it.id == action.projectId }
        is ProjectEvent -> state.map { projectReducer(it, action) }
        else -> state
    }

private fun projectReducer(state: OpenProjectInSoyleStories, action: ProjectEvent) =
    when (action) {
        is ProjectRenamed -> {
            if (state.id == action.projectId) state.copy(name = action.newName)
            else state
        }
        is ToolsListEvent -> state.copy(
            tools = toolsReducer(state.tools, action)
        )
        else -> state
    }
