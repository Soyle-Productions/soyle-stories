package com.soyle.stories.scene.outline

import com.soyle.stories.di.get
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.usecase.scene.storyevent.list.StoryEventsInScene


class SceneOutlinePromptPresenter(
    projectScope: ProjectScope,
    private val openToolController: OpenToolController
) : SceneOutlinePrompt {

    private var sceneId: Scene.Id? = null
    private val viewModel = projectScope.get<SceneOutlineViewModel>()

    override suspend fun loadingOutline(sceneId: Scene.Id, sceneName: String) {
        this.sceneId = sceneId
        viewModel.reset(sceneId, sceneName)
        openToolController.scene.openSceneOutline()
    }

    override suspend fun receiveStoryEventsCoveredByScene(storyEventsInScene: StoryEventsInScene) {
        if (sceneId != storyEventsInScene.sceneId) return
        viewModel.setItems(storyEventsInScene)
    }

    override suspend fun displayFailure(failure: Throwable) {
        viewModel.failed(failure)
    }
}