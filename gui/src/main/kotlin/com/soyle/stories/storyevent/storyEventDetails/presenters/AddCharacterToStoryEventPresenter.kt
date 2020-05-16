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

			val includedCharacterIds = includedCharacters.map { it.characterId }.toSet()

			val includedCharacters = includedCharacters + if (includedCharacterIds.contains(responseId)) emptyList()
			else listOfNotNull(characters.find { it.characterId == responseId })

			val newIncludedCharacterIds = includedCharacters.map { it.characterId }.toSet()

			copy(
			  includedCharacters = includedCharacters,
			  availableCharacters = characters.filterNot {
				  it.characterId in newIncludedCharacterIds
			  }
			)
		}
	}

	override fun receiveAddCharacterToStoryEventFailure(failure: Exception) {

	}
}