package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.usecase.character.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import kotlinx.coroutines.*

class BuildNewCharacterControllerImpl(
    private val projectId: Project.Id,
    private val threadTransformer: ThreadTransformer,
    private val buildNewCharacter: BuildNewCharacter,
    private val buildNewCharacterOutputPort: BuildNewCharacter.OutputPort,
) : BuildNewCharacterController, CoroutineScope by CoroutineScope(threadTransformer.guiContext) {

    override fun createCharacter(
        prompt: CreateCharacterPrompt
    ): Deferred<Result<Character.Id>> {
        return async {
            attemptCreateCharacter(prompt = prompt)
        }
    }

    private suspend fun attemptCreateCharacter(
        previousAttempt: String? = null,
        prompt: CreateCharacterPrompt
    ): Result<Character.Id> {
        val name = prompt.requestName(previousAttempt)
        return withContext(threadTransformer.asyncContext) {
            buildNewCharacter.invoke(projectId, name, buildNewCharacterOutputPort)
        }
    }

}