package com.soyle.stories.usecase.character.createPerspectiveCharacter

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.character.buildNewCharacter.CreatedCharacter
import com.soyle.stories.usecase.character.createPerspectiveCharacter.CreatePerspectiveCharacter
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import java.util.*

class CreatePerspectiveCharacterUseCase(
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository
) : CreatePerspectiveCharacter {

    override suspend fun invoke(themeId: UUID, name: NonBlankString, output: CreatePerspectiveCharacter.OutputPort) {
        val theme = getTheme(themeId)

        val character = createCharacter(theme.projectId, name)
        includeCharacterInTheme(character, theme)

        output.createdPerspectiveCharacter(responseModel(character, theme))
    }

    private suspend fun getTheme(themeId: UUID) = (themeRepository.getThemeById(Theme.Id(themeId))
        ?: throw ThemeDoesNotExist(themeId))

    private suspend fun createCharacter(
        projectId: Project.Id,
        name: NonBlankString
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
            theme.withCharacterIncluded(character.id, character.name.value, character.media)
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
        CreatedCharacter(character.id.uuid, character.name.value, null)

    private fun characterIncludedInTheme(
        theme: Theme,
        character: Character
    ): CharacterIncludedInTheme {
        return CharacterIncludedInTheme(
            theme.id.uuid,
            theme.name,
            character.id.uuid,
            character.name.value,
            true
        )
    }

}