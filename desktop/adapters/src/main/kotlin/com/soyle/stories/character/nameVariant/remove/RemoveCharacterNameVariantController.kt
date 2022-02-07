package com.soyle.stories.character.nameVariant.remove

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.exceptions.CharacterException
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.name.remove.RemoveCharacterNameVariant
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

class RemoveCharacterNameVariantController(
    private val threadTransformer: ThreadTransformer,
    private val removeCharacterNameVariant: RemoveCharacterNameVariant,
    private val removeCharacterNameVariantOutput: RemoveCharacterNameVariant.OutputPort
) {

    fun removeCharacterNameVariant(characterId: Character.Id, variant: NonBlankString): Deferred<CharacterException?>
    {
        val deferred = CompletableDeferred<CharacterException?>()
        threadTransformer.async {
            val failure = removeCharacterNameVariant.invoke(characterId, variant, removeCharacterNameVariantOutput)
            deferred.complete(failure)
        }
        return deferred
    }
}