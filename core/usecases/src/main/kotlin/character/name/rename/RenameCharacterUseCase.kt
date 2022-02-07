package com.soyle.stories.usecase.character.name.rename

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterDoesNotHaveNameVariant
import com.soyle.stories.domain.character.CharacterUpdate
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.prose.ProseRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class RenameCharacterUseCase(
    private val characters: CharacterRepository,
    private val prose: ProseRepository
) : RenameCharacter {

    override suspend fun invoke(
        request: RenameCharacter.RequestModel,
        output: RenameCharacter.OutputPort
    ): Result<Nothing?> {
        val character = characters.getCharacterById(request.characterId)
            ?: return characterDoesNotExist(request.characterId)

        val nameOperations = character.withName(request.name.value)
            ?: return characterDoesNotHaveName(request.characterId, request.name)

        val update = nameOperations.renamed(request.replacement)

        when (update) {
            is CharacterUpdate.Updated -> {
                output.characterRenamed(update.event)
            }
            is CharacterUpdate.WithoutChange -> return failure(update.reason as Throwable)
        }
        characters.updateCharacter(update.character)

        updateProseThatMentionCharacterName(request, output)

        return success(null)
    }

    private fun characterDoesNotExist(characterId: Character.Id) =
        failure<Nothing?>(CharacterDoesNotExist(characterId))

    private fun characterDoesNotHaveName(characterId: Character.Id, name: NonBlankString) =
        failure<Nothing?>(CharacterDoesNotHaveNameVariant(characterId, name.value))

    private suspend fun updateProseThatMentionCharacterName(
        request: RenameCharacter.RequestModel,
        output: RenameCharacter.OutputPort
    ) {
        coroutineScope {
            launch {
                val mentionedId = request.characterId.mentioned()
                prose.getProseThatMentionEntity(mentionedId)
                    .map {
                        it.withMentionTextReplaced(mentionedId,
                            request.name.value to request.replacement.value
                        )
                    }
                    .forEach {
                        val event = it.event
                        if (event != null) {
                            prose.replaceProse(it.prose)
                            output.mentionTextReplaced(event)
                        }
                    }
            }
        }
    }
}
