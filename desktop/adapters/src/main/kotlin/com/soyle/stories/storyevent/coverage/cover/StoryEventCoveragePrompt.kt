package com.soyle.stories.storyevent.coverage.cover

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.list.ListAllScenes
import com.soyle.stories.usecase.scene.list.SceneItem

fun interface StoryEventCoveragePrompt {
    suspend fun requestSceneSelection(selectedScene: Scene.Id?, sceneItems: List<ListAllScenes.SceneListItem>): Scene.Id?
}