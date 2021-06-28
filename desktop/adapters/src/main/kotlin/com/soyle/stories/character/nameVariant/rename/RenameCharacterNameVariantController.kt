package com.soyle.stories.character.nameVariant.rename

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterException
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.nameVariant.rename.RenameCharacterNameVariant
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

class RenameCharacterNameVariantController(
    private val threadTransformer: ThreadTransformer,
    private val renameCharacterNameVariant: RenameCharacterNameVariant,
    private val renameCharacterNameVariantOutput: RenameCharacterNameVariant.OutputPort
) {

    fun renameCharacterNameVariant(
        characterId: Character.Id,
        existingVariant: NonBlankString,
        replacementVariant: NonBlankString
    ): Deferred<CharacterException?>
    {
        val request = RenameCharacterNameVariant.RequestModel(
            characterId, existingVariant, replacementVariant
        )
        val deferred = CompletableDeferred<CharacterException?>()
        threadTransformer.async {
            deferred.complete(renameCharacterNameVariant.invoke(request, renameCharacterNameVariantOutput))
        }
        return deferred
    }

}