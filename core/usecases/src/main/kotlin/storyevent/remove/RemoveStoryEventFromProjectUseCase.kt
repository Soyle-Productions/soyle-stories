package com.soyle.stories.usecase.storyevent.remove

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene
import com.soyle.stories.domain.scene.order.SceneOrderService
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import java.util.*

class RemoveStoryEventFromProjectUseCase(
    private val storyEventRepository: StoryEventRepository,
    private val sceneRepository: SceneRepository
) : RemoveStoryEventFromProject {

    override suspend fun invoke(storyEventId: StoryEvent.Id, output: RemoveStoryEventFromProject.OutputPort) {
        val storyEvent = storyEventRepository.getStoryEventOrError(storyEventId)
        val sceneId = storyEvent.sceneId
        val sceneUpdate = if (sceneId != null) {
            val scene = sceneRepository.getSceneOrError(sceneId.uuid)
            scene.withoutStoryEvent(storyEventId)
        } else null

        storyEventRepository.removeStoryEvent(storyEventId)
        sceneUpdate?.let { sceneRepository.updateScene(it.scene) }

        output.storyEventRemovedFromProject(
            RemoveStoryEventFromProject.ResponseModel(
                StoryEventNoLongerHappens(storyEventId),
                (sceneUpdate as? Updated)?.event
            )
        )
    }
}