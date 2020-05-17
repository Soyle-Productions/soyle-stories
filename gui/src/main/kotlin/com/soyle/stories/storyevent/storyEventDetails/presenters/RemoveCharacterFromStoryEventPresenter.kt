package com.soyle.stories.storyevent.storyEventDetails.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.storyEventDetails.StoryEventDetailsViewModel
import com.soyle.stories.storyevent.usecases.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEvent
import java.util.*

class RemoveCharacterFromStoryEventPresenter(
  private val storyEventId: UUID,
  private val view: View.Nullable<StoryEventDetailsViewModel>
) : RemoveCharacterFromStoryEvent.OutputPort {
	override fun receiveRemoveCharacterFromStoryEventResponse(response: RemoveCharacterFromStoryEvent.ResponseModel) {
		if (response.storyEventId != storyEventId) return
		val responseId = response.removedCharacterId.toString()
		view.updateOrInvalidated {

			val includedCharacterIds = includedCharacterIds - responseId

			copy(
			  includedCharacterIds = includedCharacterIds,
			  includedCharacters = characters.filter {
				  it.characterId in includedCharacterIds
			  },
			  availableCharacters = characters.filterNot {
				  it.characterId in includedCharacterIds
			  }
			)
		}
	}

	override fun receiveRemoveCharacterFromStoryEventFailure(failure: StoryEventException) {

	}
}