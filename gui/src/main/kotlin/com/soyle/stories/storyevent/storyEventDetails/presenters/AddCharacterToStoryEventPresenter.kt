package com.soyle.stories.storyevent.storyEventDetails.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.storyEventDetails.StoryEventDetailsViewModel
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent
import java.util.*

class AddCharacterToStoryEventPresenter(
  private val storyEventId: UUID,
  private val view: View.Nullable<StoryEventDetailsViewModel>
) : AddCharacterToStoryEvent.OutputPort {

	override fun receiveAddCharacterToStoryEventResponse(response: AddCharacterToStoryEvent.ResponseModel) {
		if (response.storyEventId != storyEventId) return
		val responseId = response.characterId.toString()
		view.updateOrInvalidated {

			val includedCharacterIds = includedCharacterIds + responseId

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

	override fun receiveAddCharacterToStoryEventFailure(failure: Exception) {

	}
}