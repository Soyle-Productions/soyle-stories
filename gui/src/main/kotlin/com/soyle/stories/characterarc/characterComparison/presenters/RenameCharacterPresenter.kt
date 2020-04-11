package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import java.util.*

class RenameCharacterPresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : RenameCharacter.OutputPort {


	override fun receiveRenameCharacterFailure(failure: CharacterException) {}

	override fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
		view.update {
			copy(
			  isInvalid = true
			)
		}
	}

}