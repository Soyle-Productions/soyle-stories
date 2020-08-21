package com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.entities.theme.characterInTheme.StoryFunction
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class ListAvailableCharactersToUseAsOpponentsUseCase(
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository
) : ListAvailableCharactersToUseAsOpponents {

    override suspend fun invoke(
        themeId: UUID,
        perspectiveCharacterId: UUID,
        output: ListAvailableCharactersToUseAsOpponents.OutputPort
    ) {
        val theme = getTheme(themeId)
        val perspectiveCharacter = getPerspectiveCharacter(theme, perspectiveCharacterId)
        output.receiveAvailableCharactersToUseAsOpponents(
            availableCharactersToUseAsOpponents(theme, perspectiveCharacter)
        )
    }

    private suspend fun getTheme(themeId: UUID) = (themeRepository.getThemeById(Theme.Id(themeId))
        ?: throw ThemeDoesNotExist(themeId))

    private fun getPerspectiveCharacter(
        theme: Theme,
        perspectiveCharacterId: UUID
    ): MajorCharacter {
        if (!theme.containsCharacter(Character.Id(perspectiveCharacterId)))
            throw CharacterNotInTheme(theme.id.uuid, perspectiveCharacterId)

        return theme.getMajorCharacterById(Character.Id(perspectiveCharacterId))
            ?: throw CharacterIsNotMajorCharacterInTheme(perspectiveCharacterId, theme.id.uuid)
    }

    private suspend fun availableCharactersToUseAsOpponents(
        theme: Theme,
        perspectiveCharacter: MajorCharacter
    ): AvailableCharactersToUseAsOpponents {
        return AvailableCharactersToUseAsOpponents(
            theme.id.uuid,
            perspectiveCharacter.id.uuid,
            (
                allCharactersNotYetInTheme(theme) +
                otherCharactersInThemeNotYetAntagonistsToCharacter(theme, perspectiveCharacter)
            ).toList()
        )
    }

    private suspend fun allCharactersNotYetInTheme(theme: Theme): Sequence<AvailableCharacterToUseAsOpponent> {
        return characterRepository.listCharactersInProject(theme.projectId)
            .asSequence()
            .filterNot { theme.containsCharacter(it.id) }
            .map { AvailableCharacterToUseAsOpponent(it.id.uuid, it.name, it.media?.uuid, false) }
    }

    private fun otherCharactersInThemeNotYetAntagonistsToCharacter(
        theme: Theme,
        perspectiveCharacter: MajorCharacter
    ): Sequence<AvailableCharacterToUseAsOpponent> {
        return theme.characters
            .asSequence()
            .filterNot { it.id == perspectiveCharacter.id }
            .filterNot { perspectiveCharacter.getStoryFunctionsForCharacter(it.id) == StoryFunction.Antagonist }
            .map { AvailableCharacterToUseAsOpponent(it.id.uuid, it.name, null, true) }
    }

}