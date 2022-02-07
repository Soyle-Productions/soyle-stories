package com.soyle.stories.scene.charactersInScene.includeCharacterInScene

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.scene.storyevent.list.StoryEventsInScene
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent

interface SelectStoryEventPrompt {
    suspend fun selectStoryEvent(character: String, storyEvents: StoryEventsInScene): Selection?
    suspend fun done()

    data class Selection(
        val existingStoryEvents: List<StoryEventSelection>,
        val createStoryEventSelection: CreateStoryEventSelection?
    )

    @JvmInline
    value class StoryEventSelection(
        val storyEventId: StoryEvent.Id
    )

    class CreateStoryEventSelection(
        val name: NonBlankString,
        val time: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Absolute?
    )

}