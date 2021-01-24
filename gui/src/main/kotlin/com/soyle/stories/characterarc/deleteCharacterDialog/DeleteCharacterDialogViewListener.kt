package com.soyle.stories.characterarc.deleteCharacterDialog

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.entities.Character

interface DeleteCharacterDialogViewListener {
    fun getValidState(request: RemoveCharacterFromStory.ConfirmationRequest)
    fun deleteCharacter(characterId: Character.Id, shouldShowAgain: Boolean)
}