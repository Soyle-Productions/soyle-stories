package com.soyle.stories.character.nameVariant.list

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.character.nameVariant.list.ListCharacterNameVariants
import kotlinx.coroutines.Job

class ListCharacterNameVariantsController(
    private val threadTransformer: ThreadTransformer,
    private val listCharacterNameVariants: ListCharacterNameVariants
) {

    fun listCharacterNameVariants(characterId: Character.Id, output: ListCharacterNameVariants.OutputPort): Job
    {
        return threadTransformer.async {
            listCharacterNameVariants.invoke(characterId, output)
        }
    }

}