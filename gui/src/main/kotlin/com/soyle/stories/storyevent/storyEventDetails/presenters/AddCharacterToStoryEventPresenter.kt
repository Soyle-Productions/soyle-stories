package com.soyle.stories.storyevent.storyEventDetails.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.storyEventDetails.StoryEventDetailsViewModel
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent

class AddCharacterToStoryEventPresenter(
  private val view: View.Nullable<StoryEventDetailsViewModel>
) : AddCharacterToStoryEvent.OutputPort {

	override fun receiveAddCharacterToStoryEventResponse(response: AddCharacterToStoryEvent.ResponseModel) {
		view.updateOrInvalidated {

			val includedCharacterIds = includedCharacters.map { it.characterId }.toSet()
			if (includedCharacterIds.contains(response.characterId.toString())) return@updateOrInvalidated this

			copy(
			  includedCharacters = includedCharacters + listOfNotNull(characters.find {
				  it.characterId == response.characterId.toString()
			  })
			)
		}
	}

	override fun receiveAddCharacterToStoryEventFailure(failure: Exception) {

	}
}