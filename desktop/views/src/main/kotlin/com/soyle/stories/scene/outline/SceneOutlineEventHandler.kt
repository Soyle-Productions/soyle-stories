package com.soyle.stories.scene.outline

import com.soyle.stories.domain.scene.events.StoryEventAddedToScene
import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene
import com.soyle.stories.scene.outline.item.OutlinedStoryEventItem
import com.soyle.stories.scene.target.SceneTargeted
import com.soyle.stories.scene.target.SceneTargetedReceiver
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class SceneOutlineEventHandler(
    private val guiContext: CoroutineContext,

    private val outlineSceneController: OutlineSceneController,

    private val viewModel: SceneOutlineViewModel
) : SceneTargetedReceiver, StoryEventAddedToSceneReceiver, StoryEventRemovedFromSceneReceiver {

    override suspend fun receiveSceneTargeted(event: SceneTargeted) {
        outlineSceneController.outlineScene(event.sceneId)
    }

    override suspend fun receiveStoryEventAddedToScene(event: StoryEventAddedToScene) {
        if (event.sceneId == viewModel.sceneId) {
            val newItem = OutlinedStoryEventItem(event.storyEventId).apply { name = event.storyEventName }
            withContext(guiContext) {
                viewModel.setItems(viewModel.items() + newItem)
            }
        }
    }

    override suspend fun receiveStoryEventRemovedFromScene(event: StoryEventRemovedFromScene) {
        if (event.sceneId == viewModel.sceneId) {
            withContext(guiContext) {
                viewModel.setItems(viewModel.items().filterNot { it.storyEventId == event.storyEventId })
            }
        }
    }
}