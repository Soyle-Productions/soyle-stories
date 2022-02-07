package com.soyle.stories.character.nameVariant.addNameVariant

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.name.create.AddCharacterNameVariant
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job

class AddCharacterNameVariantController(
    private val threadTransformer: ThreadTransformer,
    private val addCharacterNameVariant: AddCharacterNameVariant,
    private val addCharacterNameVariantOutput: AddCharacterNameVariant.OutputPort
) {
    fun addCharacterNameVariant(characterId: Character.Id, variant: NonBlankString): Job {
        return threadTransformer.async {
            addCharacterNameVariant(characterId, variant, addCharacterNameVariantOutput)
        }
    }

    fun CoroutineExceptionHandler.addCharacterNameVariant(characterId: Character.Id, variant: NonBlankString): Job {
        val handler = this
        return threadTransformer.async {
            try {
                addCharacterNameVariant(characterId, variant, addCharacterNameVariantOutput)
            } catch (t: Throwable) {
                handler.handleException(coroutineContext, t)
            }
        }
    }
}