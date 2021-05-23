package com.soyle.stories.usecase.character.renameCharacter

import arrow.core.identity
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterRenamed
import com.soyle.stories.domain.prose.MentionTextReplaced
import com.soyle.stories.domain.prose.ProseUpdate
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.scene.SceneUpdate
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.domain.scene.events.RenamedCharacterInScene
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class RenameCharacterUseCase(
    private val characterRepository: CharacterRepository,
    private val themeRepository: ThemeRepository,
    private val sceneRepository: SceneRepository,
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
        val (renamedCharacter, renamedCharacterEvent) = renameCharacter(character, name)
        return RenameCharacter.ResponseModel(
            renamedCharacterEvent,
            renameCharacterInThemes(renamedCharacter),
            renameCharacterInScenes(renamedCharacter),
            replaceProseMentionText(character.name, renamedCharacter)
        )
    }


    private suspend fun renameCharacter(character: Character, name: NonBlankString): Pair<Character, CharacterRenamed> {
        val newCharacter = character.withName(name)
        characterRepository.updateCharacter(newCharacter)
        return newCharacter to CharacterRenamed(character.id, name.value)
    }

    private suspend fun renameCharacterInThemes(character: Character): List<UUID> {
        val themes = themeRepository.getThemesWithCharacterIncluded(character.id)
        if (themes.isEmpty()) return emptyList()

        val updatedThemes = themes.map { theme ->
            theme.getIncludedCharacterById(character.id)?.let {
                theme.withCharacterRenamed(it, character.name.value).fold(
                    { throw it },
                    ::identity
                )
            } ?: theme
        }

        themeRepository.updateThemes(updatedThemes)
        return themes.map { it.id.uuid }
    }

    private suspend fun renameCharacterInScenes(
        character: Character
    ) : List<RenamedCharacterInScene> {
        val sceneUpdates = sceneRepository.getScenesIncludingCharacter(character.id)
            .map {
                it.withCharacterRenamed(character)
            }

        sceneUpdates.forEach { sceneRepository.updateScene(it.scene) }
        return sceneUpdates.mapNotNull { (it as? Updated)?.event }
    }

    private suspend fun replaceProseMentionText(originalName: NonBlankString, character: Character): List<MentionTextReplaced> {
        val entityId = character.id.mentioned()
        val updates = proseRepository.getProseThatMentionEntity(entityId)
            .map { it.withMentionTextReplaced(entityId, originalName.value to character.name.value) }
            .filter { it.event != null }
        proseRepository.replaceProse(updates.map { it.prose })
        return updates.map { it.event!! }
    }
}
