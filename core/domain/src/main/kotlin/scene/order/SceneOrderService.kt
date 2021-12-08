package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString

class SceneOrderService {

    fun createScene(
        sceneOrder: SceneOrder,
        name: NonBlankString,
        storyEventId: StoryEvent.Id,
        proseId: Prose.Id,
        index: Int = -1
    ): SceneOrderUpdate<Successful<SceneCreated>> {
        val sceneUpdate = Scene.create(sceneOrder.projectId, name, storyEventId, proseId) as Successful
        val update = sceneOrder.withScene(sceneUpdate.change, index)
        if (update is UnSuccessfulSceneOrderUpdate) {
            return update
        } else {
            return SuccessfulSceneOrderUpdate(update.sceneOrder, sceneUpdate)
        }
    }

}