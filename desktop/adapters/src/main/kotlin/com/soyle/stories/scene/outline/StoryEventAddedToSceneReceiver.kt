package com.soyle.stories.scene.outline

import com.soyle.stories.domain.scene.events.StoryEventAddedToScene

fun interface StoryEventAddedToSceneReceiver {
    suspend fun receiveStoryEventAddedToScene(event: StoryEventAddedToScene)
}