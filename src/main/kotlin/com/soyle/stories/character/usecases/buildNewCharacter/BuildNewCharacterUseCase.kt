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
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import java.util.*

class BuildNewCharacterUseCase(
    private val characterRepository: CharacterRepository,
    private val themeRepository: ThemeRepository
) : BuildNewCharacter {

    override suspend fun invoke(projectId: UUID, name: String, outputPort: BuildNewCharacter.OutputPort) {
        val response = try {
            execute(Project.Id(projectId), name)
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

        val characterItem = execute(theme.projectId, name)
        val themeWithCharacter = theme.withCharacterIncluded(
            Character.Id(characterItem.characterId),
            characterItem.characterName,
            characterItem.mediaId?.let(Media::Id)
        )
        themeRepository.updateThemes(listOf(themeWithCharacter))

        themeWithCharacter.characters.map {
            CharacterItem(it.id.uuid, it.name, null)
        }
        outputPort.characterIncludedInTheme(
            CharacterIncludedInTheme(
                theme.id.uuid,
                "",
                characterItem.characterId,
                characterItem.characterName,
                false
            )
        )
        outputPort.receiveBuildNewCharacterResponse(characterItem)
    }

    private suspend fun execute(projectId: Project.Id, name: String): CharacterItem {
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