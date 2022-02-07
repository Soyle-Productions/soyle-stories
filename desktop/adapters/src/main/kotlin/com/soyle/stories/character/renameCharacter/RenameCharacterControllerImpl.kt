package com.soyle.stories.character.renameCharacter

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.name.rename.RenameCharacter
import kotlinx.coroutines.Job

class RenameCharacterControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val renameCharacter: RenameCharacter,
    private val renameCharacterOutputPort: RenameCharacter.OutputPort
) : RenameCharacterController {

    override fun renameCharacter(characterId: Character.Id, currentName: NonBlankString, newName: NonBlankString): Job {
        val request = RenameCharacter.RequestModel(characterId, currentName, newName)
        return threadTransformer.async {
            renameCharacter.invoke(
                request,
                renameCharacterOutputPort
            )
        }
    }
}