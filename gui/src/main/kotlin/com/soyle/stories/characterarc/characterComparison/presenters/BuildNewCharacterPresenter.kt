package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.characterarc.characterComparison.CharacterComparisonViewModel
import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import java.util.*

internal class BuildNewCharacterPresenter(
  private val view: CharacterComparisonView
) : BuildNewCharacter.OutputPort {

	override fun receiveBuildNewCharacterResponse(response: CharacterItem) {
		view.update {
			if (hasCharacter(response.characterId.toString())) return@update this
			copy(
			  availableCharactersToAdd = availableCharactersToAdd + CharacterItemViewModel(
				response.characterId.toString(),
				response.characterName
			  )
			)
		}
	}

	private fun CharacterComparisonViewModel.hasCharacter(characterId: String): Boolean {
		return subTools.getOrNull(0)?.items?.find { it.characterId == characterId } != null
	}

	override fun receiveBuildNewCharacterFailure(failure: CharacterException) {}

}