package com.soyle.stories.storyevent.storyEventDetails.presenters

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.gui.View
import com.soyle.stories.storyevent.storyEventDetails.StoryEventDetailsViewModel
import com.soyle.stories.usecase.character.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.usecase.character.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsOpponent

class BuildNewCharacterPresenter(
  private val view: View.Nullable<StoryEventDetailsViewModel>
) : BuildNewCharacter.OutputPort {
	override suspend fun receiveBuildNewCharacterResponse(response: CharacterItem) {
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

	override fun receiveBuildNewCharacterFailure(failure: Exception) {
		// no-op
	}

	override suspend fun characterIsOpponent(response: CharacterUsedAsOpponent) {
		/* no-op */
	}
}