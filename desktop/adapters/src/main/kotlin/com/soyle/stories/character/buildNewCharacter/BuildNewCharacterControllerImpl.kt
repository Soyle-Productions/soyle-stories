package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.usecase.character.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.usecase.character.createPerspectiveCharacter.CreatePerspectiveCharacter
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import kotlinx.coroutines.Job
import java.util.*

class BuildNewCharacterControllerImpl(
    projectId: String,
    private val threadTransformer: ThreadTransformer,
    private val buildNewCharacter: BuildNewCharacter,
    private val buildNewCharacterOutputPort: BuildNewCharacter.OutputPort,
    private val createPerspectiveCharacter: CreatePerspectiveCharacter,
    private val createPerspectiveCharacterOutputPort: CreatePerspectiveCharacter.OutputPort
) : BuildNewCharacterController {

    private val projectId = UUID.fromString(projectId)

    override fun createCharacter(name: NonBlankString): Job {
        return threadTransformer.async {
            buildNewCharacter.invoke(projectId, name, buildNewCharacterOutputPort)
        }
    }

    override fun createCharacterAndIncludeInTheme(name: NonBlankString, includeInTheme: String, onError: (Throwable) -> Unit) {
        val preparedThemeId = UUID.fromString(includeInTheme)
        threadTransformer.async {
            try {
                buildNewCharacter.createAndIncludeInTheme(name, preparedThemeId, buildNewCharacterOutputPort)
            } catch (t: Throwable) { onError(t) }
        }
    }

    override fun createCharacterForUseAsOpponent(
        name: NonBlankString,
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

    override fun createCharacterAsMajorCharacter(name: NonBlankString, includeInTheme: String, onError: (Throwable) -> Unit) {
        val preparedThemeId = UUID.fromString(includeInTheme)
        threadTransformer.async {
            try {
                createPerspectiveCharacter.invoke(preparedThemeId, name, createPerspectiveCharacterOutputPort)
            } catch (t: Throwable) { onError(t) }
        }
    }

}