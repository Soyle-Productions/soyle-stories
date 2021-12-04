package com.soyle.stories.storyevent.coverage

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem

fun interface StoryEventCoveragePrompt {
    suspend fun requestSceneSelection(selectedScene: Scene.Id?, sceneItems: List<SceneItem>): Scene.Id?
}