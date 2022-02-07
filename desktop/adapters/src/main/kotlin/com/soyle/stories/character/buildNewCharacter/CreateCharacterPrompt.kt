package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.domain.validation.NonBlankString

fun interface CreateCharacterPrompt {
    suspend fun requestName(previousAttempt: String?): NonBlankString
}