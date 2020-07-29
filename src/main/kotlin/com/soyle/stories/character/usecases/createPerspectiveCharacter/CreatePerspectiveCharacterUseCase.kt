package com.soyle.stories.character.usecases.createPerspectiveCharacter

import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.character.usecases.buildNewCharacter.CreatedCharacter
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import java.util.*

class CreatePerspectiveCharacterUseCase(
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository
) : CreatePerspectiveCharacter {

    override suspend fun invoke(themeId: UUID, name: String, output: CreatePerspectiveCharacter.OutputPort) {
        val theme = getTheme(themeId)

        val character = createCharacter(theme.projectId, name)
        includeCharacterInTheme(character, theme)

        output.createdPerspectiveCharacter(responseModel(character, theme))
    }

    private suspend fun getTheme(themeId: UUID) = (themeRepository.getThemeById(Theme.Id(themeId))
        ?: throw ThemeDoesNotExist(themeId))

    private suspend fun createCharacter(
        projectId: Project.Id,
        name: String
    ): Character {
        val character = Character(projectId, name, null)
        characterRepository.addNewCharacter(character)
        return character
    }

    private suspend fun includeCharacterInTheme(
        character: Character,
        theme: Theme
    ) {
        themeRepository.updateTheme(
            theme.withCharacterIncluded(character.id, character.name, character.media)
                 .withCharacterPromoted(character.id)
        )
    }

    private fun responseModel(
        character: Character,
        theme: Theme
    ): CreatePerspectiveCharacter.ResponseModel {
        return CreatePerspectiveCharacter.ResponseModel(
            createdCharacter(character),
            characterIncludedInTheme(theme, character)
        )
    }

    private fun createdCharacter(character: Character) =
        CreatedCharacter(character.id.uuid, character.name, null)

    private fun characterIncludedInTheme(
        theme: Theme,
        character: Character
    ): CharacterIncludedInTheme {
        return CharacterIncludedInTheme(
            theme.id.uuid,
            theme.name,
            character.id.uuid,
            character.name,
            true
        )
    }

}