package com.soyle.stories.characterarc.deleteCharacterDialog

import com.soyle.stories.entities.Character

interface DeleteCharacterDialogViewListener {
    fun confirmCharacter(name: String)
    fun deleteCharacter(characterId: Character.Id, shouldShowAgain: Boolean)
}