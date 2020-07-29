package com.soyle.stories.character.usecases.buildNewCharacter

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.character.repositories.ThemeRepository
import com.soyle.stories.character.usecases.validateCharacterName
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Media
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.OpponentCharacter
import java.util.*

class BuildNewCharacterUseCase(
    private val characterRepository: CharacterRepository,
    private val themeRepository: ThemeRepository
) : BuildNewCharacter {

    override suspend fun invoke(projectId: UUID, name: String, outputPort: BuildNewCharacter.OutputPort) {
        val response = try {
            createCharacter(Project.Id(projectId), name)
        } catch (e: CharacterException) {
            return outputPort.receiveBuildNewCharacterFailure(e)
        }
        outputPort.receiveBuildNewCharacterResponse(response)
    }

    override suspend fun createAndIncludeInTheme(
        name: String,
        themeId: UUID,
        outputPort: BuildNewCharacter.OutputPort
    ) {
        val theme = themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)

        val characterItem = createCharacter(theme.projectId, name)
        val includedCharacter = includeInTheme(theme, characterItem)

        outputPort.characterIncludedInTheme(includedCharacter)
        outputPort.receiveBuildNewCharacterResponse(characterItem)
    }

    override suspend fun createAndUseAsOpponent(
        name: String,
        themeId: UUID,
        opponentOfCharacterId: UUID,
        outputPort: BuildNewCharacter.OutputPort
    ) {
        val theme = themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)

        val characterItem = createCharacter(theme.projectId, name)
        val characterIncluded = includeInTheme(theme, characterItem)

        if (! theme.containsCharacter(Character.Id(opponentOfCharacterId)))
            throw CharacterNotInTheme(themeId, opponentOfCharacterId)

        theme.getMajorCharacterById(Character.Id(opponentOfCharacterId))
            ?: throw CharacterIsNotMajorCharacterInTheme(opponentOfCharacterId, themeId)

        outputPort.receiveBuildNewCharacterResponse(characterItem)
        outputPort.characterIncludedInTheme(characterIncluded)
        outputPort.characterIsOpponent(OpponentCharacter(
            characterItem.characterId,
            characterItem.characterName,
            opponentOfCharacterId,
            themeId
        ))
    }

    private suspend fun includeInTheme(theme: Theme, characterItem: CharacterItem): CharacterIncludedInTheme
    {
        val themeWithCharacter = theme.withCharacterIncluded(
            Character.Id(characterItem.characterId),
            characterItem.characterName,
            characterItem.mediaId?.let(Media::Id)
        )
        themeRepository.updateThemes(listOf(themeWithCharacter))

        themeWithCharacter.characters.map {
            CharacterItem(it.id.uuid, it.name, null)
        }

        return CharacterIncludedInTheme(
            theme.id.uuid,
            "",
            characterItem.characterId,
            characterItem.characterName,
            false
        )
    }

    private suspend fun createCharacter(projectId: Project.Id, name: String): CharacterItem {
        validateCharacterName(name)

        val character = Character.buildNewCharacter(projectId, name)

        characterRepository.addNewCharacter(character)

        return CharacterItem(
            character.id.uuid,
            character.name,
            character.media?.uuid
        )
    }
}