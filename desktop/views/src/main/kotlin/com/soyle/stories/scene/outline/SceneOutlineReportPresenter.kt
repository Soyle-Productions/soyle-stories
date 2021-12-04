package com.soyle.stories.scene.outline

import com.soyle.stories.di.get
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.outline.item.OutlinedStoryEventItem
import com.soyle.stories.storyevent.coverage.CoverStoryEventController
import com.soyle.stories.usecase.scene.storyevent.list.StoryEventsInScene
import javafx.scene.control.MenuItem
import kotlinx.coroutines.CompletableDeferred
import tornadofx.action
import tornadofx.onChangeOnce


class SceneOutlineReportPresenter(
    private val projectScope: ProjectScope,
    private val openToolController: OpenToolController.OpenSceneToolController,
    private val coverStoryEventInSceneController: CoverStoryEventController
) {

    suspend fun createOutline(sceneId: Scene.Id, sceneName: String): SceneOutlineReport {
        val viewModel = projectScope.get<SceneOutlineViewModel>()
        viewModel.reset(sceneId, sceneName)
        openToolController.openSceneOutline()

        viewModel.setOnRequestingStoryEventsToCover(coverStoryEventInScene(sceneId, viewModel))

        return object : SceneOutlineReport {

            override suspend fun receiveStoryEventsCoveredByScene(storyEventsInScene: StoryEventsInScene) {
                viewModel.setItems(storyEventsInScene.map {
                    OutlinedStoryEventItem(it.storyEventId).apply { name = it.storyEventName }
                })
            }

            override suspend fun displayFailure(failure: Throwable) {
                viewModel.failed(failure)
            }
        }
    }

    private fun coverStoryEventInScene(sceneId: Scene.Id, viewModel: SceneOutlineViewModel): () -> Unit = {
        coverStoryEventInSceneController.coverStoryEventInScene(sceneId) { availableItems ->
            val deferred = CompletableDeferred<StoryEvent.Id?>()
            if (!viewModel.isRequestingStoryEventsToCover) deferred.complete(null)
            else {
                viewModel.availableItems().set(availableItems.map {
                    MenuItem(it.storyEventName).apply {
                        id = it.storyEventId.toString()
                        action { if (!deferred.isCompleted) deferred.complete(it.storyEventId) }
                    }
                })
                viewModel.requestingStoryEventsToCover().onChangeOnce {
                    if (it != true && !deferred.isCompleted) deferred.complete(null)
                }
            }
            deferred.await()
        }
    }

}