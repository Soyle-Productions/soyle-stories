package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.characterarc.characterComparison.CharacterComparisonViewModel
import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.OpponentCharacter
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

	override suspend fun characterIncludedInTheme(response: CharacterIncludedInTheme) {
		// do nothing
	}

	override suspend fun characterIsOpponent(response: OpponentCharacter) {
		// do nothing
	}

	override fun receiveBuildNewCharacterFailure(failure: CharacterException) {}

}