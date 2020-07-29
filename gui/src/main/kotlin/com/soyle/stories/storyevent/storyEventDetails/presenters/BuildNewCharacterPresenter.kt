package com.soyle.stories.storyevent.storyEventDetails.presenters

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.storyEventDetails.StoryEventDetailsViewModel
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.OpponentCharacter

class BuildNewCharacterPresenter(
  private val view: View.Nullable<StoryEventDetailsViewModel>
) : BuildNewCharacter.OutputPort {
	override fun receiveBuildNewCharacterResponse(response: CharacterItem) {
		view.updateOrInvalidated {
			val characters = characters + CharacterItemViewModel(response.characterId.toString(), response.characterName, "")
			copy(
			  characters = characters,
			  availableCharacters = characters.filterNot {
				  it.characterId in includedCharacterIds
			  }
			)
		}
	}

	override suspend fun characterIncludedInTheme(response: CharacterIncludedInTheme) {
		// do nothing
	}

	override fun receiveBuildNewCharacterFailure(failure: CharacterException) {
		// no-op
	}

	override suspend fun characterIsOpponent(response: OpponentCharacter) {

		// do nothing
	}
}