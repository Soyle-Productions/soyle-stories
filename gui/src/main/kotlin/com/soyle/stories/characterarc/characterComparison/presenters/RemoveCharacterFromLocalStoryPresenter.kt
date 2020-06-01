package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import java.util.*

internal class RemoveCharacterFromLocalStoryPresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : RemoveCharacterFromStory.OutputPort {

	override fun receiveRemoveCharacterFromStoryResponse(response: RemoveCharacterFromStory.ResponseModel) {
		if (themeId !in response.affectedThemeIds) return
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

	override fun receiveRemoveCharacterFromStoryFailure(failure: Exception) {}
}