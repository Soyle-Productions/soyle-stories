package com.soyle.stories.usecase.storyevent.getStoryEventDetails

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.storyevent.StoryEvent
import java.util.*

interface GetStoryEventDetails {

    suspend operator fun invoke(storyEventId: StoryEvent.Id, output: OutputPort)

    class ResponseModel(
        val storyEventId: UUID,
        val storyEventName: String,
        val locationId: Location.Id?,
        val includedCharacterIds: List<Character.Id>
    )

    fun interface OutputPort {

        fun receiveGetStoryEventDetailsResponse(response: ResponseModel)
    }
}