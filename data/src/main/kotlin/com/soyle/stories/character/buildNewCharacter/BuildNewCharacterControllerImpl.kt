package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.createPerspectiveCharacter.CreatePerspectiveCharacter
import com.soyle.stories.common.ThreadTransformer
import java.util.*
import kotlin.concurrent.thread

class BuildNewCharacterControllerImpl(
    projectId: String,
    private val threadTransformer: ThreadTransformer,
    private val buildNewCharacter: BuildNewCharacter,
    private val buildNewCharacterOutputPort: BuildNewCharacter.OutputPort,
    private val createPerspectiveCharacter: CreatePerspectiveCharacter,
    private val createPerspectiveCharacterOutputPort: CreatePerspectiveCharacter.OutputPort
) : BuildNewCharacterController {

    private val projectId = UUID.fromString(projectId)

    override fun createCharacter(name: String, onError: (Throwable) -> Unit) {
        threadTransformer.async {
            try {
                buildNewCharacter.invoke(projectId, name, buildNewCharacterOutputPort)
            } catch (t: Throwable) { onError(t) }
        }
    }

    override fun createCharacterAndIncludeInTheme(name: String, includeInTheme: String, onError: (Throwable) -> Unit) {
        val preparedThemeId = UUID.fromString(includeInTheme)
        threadTransformer.async {
            try {
                buildNewCharacter.createAndIncludeInTheme(name, preparedThemeId, buildNewCharacterOutputPort)
            } catch (t: Throwable) { onError(t) }
        }
    }

    override fun createCharacterForUseAsOpponent(
        name: String,
        includeInTheme: String,
        opponentForCharacter: String,
        onError: (Throwable) -> Unit
    ) {
        val preparedThemeId = UUID.fromString(includeInTheme)
        val preparedPerspectiveCharacterId = UUID.fromString(opponentForCharacter)
        threadTransformer.async {
            try {
                buildNewCharacter.createAndUseAsOpponent(name, preparedThemeId, preparedPerspectiveCharacterId, buildNewCharacterOutputPort)
            } catch (t: Throwable) { onError(t) }
        }
    }

    override fun createCharacterAsMajorCharacter(name: String, includeInTheme: String, onError: (Throwable) -> Unit) {
        val preparedThemeId = UUID.fromString(includeInTheme)
        threadTransformer.async {
            try {
                createPerspectiveCharacter.invoke(preparedThemeId, name, createPerspectiveCharacterOutputPort)
            } catch (t: Throwable) { onError(t) }
        }
    }

}