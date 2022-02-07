package com.soyle.stories.storyevent.coverage.uncover

import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.common.Confirmation
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent

fun interface ConfirmUncoverStoryEventPrompt {
    suspend fun confirmRemoveStoryEventFromScene(storyEvent: StoryEvent, scene: Scene): Confirmation<ConfirmationPrompt.Response>
}