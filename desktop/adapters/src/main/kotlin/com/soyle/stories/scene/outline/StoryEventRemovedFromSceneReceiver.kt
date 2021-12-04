package com.soyle.stories.scene.outline

import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene

interface StoryEventRemovedFromSceneReceiver {
    suspend fun receiveStoryEventRemovedFromScene(event: StoryEventRemovedFromScene)
}