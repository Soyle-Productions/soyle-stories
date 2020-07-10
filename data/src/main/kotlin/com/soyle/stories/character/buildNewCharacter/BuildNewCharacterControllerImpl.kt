package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.common.ThreadTransformer

class BuildNewCharacterControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val buildNewCharacter: BuildNewCharacter,
    private val buildNewCharacterOutputPort: BuildNewCharacter.OutputPort
) : BuildNewCharacterController {

    override fun buildNewCharacter(name: String, onError: (Throwable) -> Unit) {
        threadTransformer.async {
            try {
                buildNewCharacter.invoke(name, buildNewCharacterOutputPort)
            } catch (t: Throwable) { onError(t) }
        }
    }

}