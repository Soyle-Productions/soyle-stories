package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.entities.Character
import java.util.*

class RemoveCharacterFromStoryControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val removeCharacterFromStory: RemoveCharacterFromStory,
    private val removeCharacterFromStoryOutput: RemoveCharacterFromStory.OutputPort
) : RemoveCharacterFromStoryController {

    override fun requestRemoveCharacter(characterId: String) {
        val preparedCharacterId = UUID.fromString(characterId)
        threadTransformer.async {
            removeCharacterFromStory.invoke(preparedCharacterId, false, removeCharacterFromStoryOutput)
        }
    }

    override fun confirmRemoveCharacter(characterId: Character.Id) {
        threadTransformer.async {
            removeCharacterFromStory.invoke(characterId.uuid, true, removeCharacterFromStoryOutput)
        }
    }

}