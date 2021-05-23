package com.soyle.stories.usecase.character.nameVariant.rename

import arrow.core.extensions.sequence.unzip.unzipWith
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterDoesNotHaveNameVariant
import com.soyle.stories.domain.character.CharacterException
import com.soyle.stories.domain.character.CharacterUpdate
import com.soyle.stories.domain.character.events.CharacterNameVariantRenamed
import com.soyle.stories.domain.prose.ProseUpdate
import com.soyle.stories.domain.prose.events.MentionTextReplaced
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.prose.ProseRepository
import java.util.*

class RenameCharacterNameVariantUseCase(
    private val characterRepository: CharacterRepository,
    private val proseRepository: ProseRepository
) : RenameCharacterNameVariant {

    override suspend fun invoke(
        request: RenameCharacterNameVariant.RequestModel,
        output: RenameCharacterNameVariant.OutputPort
    ): CharacterException? {

        val characterUpdate = characterRepository
            .getCharacterOrError(request.characterId.uuid)
            .withNameVariantModified(request.currentVariant, request.newVariant)

        return when (characterUpdate) {
            is CharacterUpdate.Updated -> characterUpdate
                .andVariantReplacedInProse(request.currentVariant.value to request.newVariant.value)
                .also { commitChanges(it) }
                .let { RenameCharacterNameVariant.ResponseModel(it.first.event, it.second.map { it.event!! }) }
                .let { output.characterArcNameVariantRenamed(it) }
                .let { null }
            is CharacterUpdate.WithoutChange -> {
                val failure = characterUpdate.reason
                if (failure is CharacterDoesNotHaveNameVariant) throw failure as Exception
                failure
            }
        }
    }

    private suspend fun CharacterUpdate.Updated<CharacterNameVariantRenamed>.andVariantReplacedInProse(
        replacement: Pair<String, String>
    ): Pair<CharacterUpdate.Updated<CharacterNameVariantRenamed>, List<ProseUpdate<MentionTextReplaced?>>> {
        val mentionedCharacterId = character.id.mentioned()
        return this to proseRepository.getProseThatMentionEntity(mentionedCharacterId)
            .asSequence()
            .map { it.withMentionTextReplaced(mentionedCharacterId, replacement) }
            .filter { it.event != null }
            .toList()
    }

    private suspend fun commitChanges(changes: Pair<CharacterUpdate.Updated<*>, List<ProseUpdate<*>>>) {
        characterRepository.updateCharacter(changes.first.character)
        proseRepository.replaceProse(changes.second.map { it.prose })
    }

}