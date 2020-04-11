package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStory
import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import java.util.*

internal class RemoveCharacterFromLocalStoryPresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : RemoveCharacterFromLocalStory.OutputPort {

	override fun receiveRemoveCharacterFromLocalStoryResponse(response: RemoveCharacterFromLocalStory.ResponseModel) {
		if (themeId !in response.updatedThemes) return
		view.update {
			val focusOptions = focusCharacterOptions.filterNot { it.characterId == response.characterId.toString() }
			val focusedCharacter = focusedCharacter?.takeUnless { it.characterId == response.characterId.toString() }
			  ?: focusOptions.firstOrNull()
			copy(
			  focusedCharacter = focusedCharacter,
			  focusCharacterOptions = focusOptions,
			  isInvalid = true
			)
		}
	}

	override fun receiveRemoveCharacterFromLocalStoryFailure(failure: CharacterException) {}
}