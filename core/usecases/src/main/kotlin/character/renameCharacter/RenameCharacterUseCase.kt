package com.soyle.stories.usecase.character.renameCharacter

import arrow.core.identity
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterRenamed
import com.soyle.stories.domain.prose.MentionTextReplaced
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class RenameCharacterUseCase(
    private val characterRepository: CharacterRepository,
    private val themeRepository: ThemeRepository,
    private val proseRepository: ProseRepository
) : RenameCharacter {

    override suspend fun invoke(characterId: UUID, name: NonBlankString, output: RenameCharacter.OutputPort) {
        val character = characterRepository.getCharacterOrError(characterId)
        if (character.name == name) return
        output.receiveRenameCharacterResponse(
            changeNameForAllInstancesOfCharacter(name, character)
        )
    }

    private suspend fun changeNameForAllInstancesOfCharacter(
        name: NonBlankString,
        character: Character
    ): RenameCharacter.ResponseModel {
        return RenameCharacter.ResponseModel(
            renameCharacter(character, name),
            renameCharacterInThemes(character, name),
            replaceProseMentionText(character, name)
        )
    }

    private suspend fun renameCharacter(character: Character, name: NonBlankString): CharacterRenamed {
        characterRepository.updateCharacter(character.withName(name))
        return CharacterRenamed(character.id, name.value)
    }

    private suspend fun renameCharacterInThemes(character: Character, name: NonBlankString): List<UUID> {
        val themes = themeRepository.getThemesWithCharacterIncluded(character.id)
        if (themes.isEmpty()) return emptyList()

        val updatedThemes = themes.map { theme ->
            theme.getIncludedCharacterById(character.id)?.let {
                theme.withCharacterRenamed(it, name.value).fold(
                    { throw it },
                    ::identity
                )
            } ?: theme
        }

        themeRepository.updateThemes(updatedThemes)
        return themes.map { it.id.uuid }
    }

    private suspend fun replaceProseMentionText(character: Character, name: NonBlankString): List<MentionTextReplaced> {
        val entityId = character.id.mentioned()
        val updates = proseRepository.getProseThatMentionEntity(entityId)
            .map {
                it.withMentionTextReplaced(entityId, name.value)
            }
        proseRepository.replaceProse(updates.map { it.prose })
        return updates.mapNotNull { it.event }
    }
}
