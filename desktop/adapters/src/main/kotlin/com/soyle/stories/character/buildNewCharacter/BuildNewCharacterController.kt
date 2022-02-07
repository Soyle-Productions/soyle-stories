package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.NonBlankString
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job

interface BuildNewCharacterController {

    /**
     * @return If result is `null`, the process was cancelled before completion.  Otherwise, the result will be the [Character.Id]
     * of the created character
     */
    fun createCharacter(prompt: CreateCharacterPrompt): Deferred<Result<Character.Id>?>

}