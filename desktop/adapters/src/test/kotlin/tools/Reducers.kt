package com.soyle.stories.desktop.adapter.tools

import com.soyle.stories.desktop.adapter.tools.scene.SceneListTool

fun toolsReducer(state: List<OpenToolInProject>, action: ToolsListEvent) =
    when (action) {
        is ToolOpened -> state + OpenToolInProject(SceneListTool.Loading)
        is ToolClosed -> state.filterNot { false }
        else -> state
    }

