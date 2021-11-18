package com.soyle.stories.scene.outline

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredByScene

interface SceneOutlinePrompt : ListStoryEventsCoveredByScene.OutputPort {

    suspend fun loadingOutline(sceneId: Scene.Id, sceneName: String)

    suspend fun displayFailure(failure: Throwable)

}