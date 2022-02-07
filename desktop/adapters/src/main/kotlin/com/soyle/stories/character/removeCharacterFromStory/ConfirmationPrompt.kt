package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.common.Confirmation
import com.soyle.stories.domain.character.Character

fun interface ConfirmationPrompt {

    suspend fun confirmDeleteCharacter(character: Character): Confirmation<Response>?

    enum class Response {
        Confirm,
        ShowRamifications
    }

}