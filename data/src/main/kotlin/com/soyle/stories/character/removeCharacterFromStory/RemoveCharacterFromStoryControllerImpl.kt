package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.common.ThreadTransformer
import java.util.*

class RemoveCharacterFromStoryControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val removeCharacterFromStory: RemoveCharacterFromStory,
    private val removeCharacterFromStoryOutput: RemoveCharacterFromStory.OutputPort
) : RemoveCharacterFromStoryController {

    override fun removeCharacter(characterId: String) {
        val preparedCharacterId = UUID.fromString(characterId)
        threadTransformer.async {
            removeCharacterFromStory.invoke(preparedCharacterId, removeCharacterFromStoryOutput)
        }
    }

}