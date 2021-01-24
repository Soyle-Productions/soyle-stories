package com.soyle.stories.storyevent.storyEventDetails.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.addCharacterToStoryEvent.IncludedCharacterInStoryEventReceiver
import com.soyle.stories.storyevent.storyEventDetails.StoryEventDetailsViewModel
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.IncludedCharacterInStoryEvent
import java.util.*

class AddCharacterToStoryEventPresenter(
  private val storyEventId: UUID,
  private val view: View.Nullable<StoryEventDetailsViewModel>
) : IncludedCharacterInStoryEventReceiver {

	override suspend fun receiveIncludedCharacterInStoryEvent(includedCharacterInStoryEvent: IncludedCharacterInStoryEvent) {
		if (includedCharacterInStoryEvent.storyEventId != storyEventId) return
		val responseId = includedCharacterInStoryEvent.characterId.toString()
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
}