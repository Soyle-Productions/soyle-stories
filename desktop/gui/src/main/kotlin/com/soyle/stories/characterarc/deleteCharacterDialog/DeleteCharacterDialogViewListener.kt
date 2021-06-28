package com.soyle.stories.characterarc.deleteCharacterDialog

import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.domain.character.Character

interface DeleteCharacterDialogViewListener {
    fun getValidState(request: RemoveCharacterFromStory.ConfirmationRequest)
    fun deleteCharacter(characterId: Character.Id, shouldShowAgain: Boolean)
}