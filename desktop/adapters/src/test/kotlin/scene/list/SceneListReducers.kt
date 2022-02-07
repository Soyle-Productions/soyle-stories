package com.soyle.stories.desktop.adapter.scene.list

import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.events.SceneRemoved
import com.soyle.stories.domain.scene.events.SceneRenamed
import com.soyle.stories.usecase.scene.list.ListAllScenes

fun sceneListReducer(state: List<ListAllScenes.SceneListItem>, action: Any): List<ListAllScenes.SceneListItem> =
    when (action) {
        is SceneCreated -> state + ListAllScenes.SceneListItem(action.sceneId, action.name, action.proseId)
        is SceneRemoved -> state.filterNot { it.scene == action.sceneId }
        else -> state.map {
            sceneItemReducer(it, action)
        }
    }

fun sceneItemReducer(state: ListAllScenes.SceneListItem, action: Any): ListAllScenes.SceneListItem =
    when (action) {
        is SceneRenamed -> {
            if (state.scene != action.sceneId) state
            else ListAllScenes.SceneListItem(state.scene, action.sceneName, state.prose)
        }
        else -> state
    }