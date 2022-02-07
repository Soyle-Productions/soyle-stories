package com.soyle.stories.scene.outline

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.scene.outline.item.OutlinedStoryEventItem
import com.soyle.stories.storyevent.coverage.StoryEventCoveredBySceneReceiver
import com.soyle.stories.storyevent.coverage.uncover.StoryEventUncoveredBySceneReceiver
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class SceneOutlineEventHandler(
    private val guiContext: CoroutineContext,

    private val outlineSceneController: OutlineSceneController,

    private val viewModel: SceneOutlineViewModel
) : StoryEventCoveredBySceneReceiver, StoryEventUncoveredBySceneReceiver {
    fun outlineScene(scene: Scene.Id?) {
        if (scene != null) outlineSceneController.outlineScene(scene)
    }

    override suspend fun receiveStoryEventCoveredByScene(event: StoryEventCoveredByScene) {
        if (event.sceneId == viewModel.sceneId) {
            val newItem = OutlinedStoryEventItem(event.storyEventId).apply { name = event.storyEventName }
            withContext(guiContext) {
                viewModel.setItems(viewModel.items() + newItem)
            }
        }
    }

    override suspend fun receiveStoryEventUncoveredByScene(event: StoryEventUncoveredFromScene) {
        if (event.previousSceneId == viewModel.sceneId) {
            withContext(guiContext) {
                viewModel.setItems(viewModel.items().filterNot { it.storyEventId == event.storyEventId })
            }
        }
    }
}