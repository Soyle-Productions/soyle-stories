package com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.StoryFunction
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class ListAvailableCharactersToUseAsOpponentsUseCase(
    private val themeRepository: ThemeRepository
) : ListAvailableCharactersToUseAsOpponents {

    override suspend fun invoke(
        themeId: UUID,
        perspectiveCharacterId: UUID,
        output: ListAvailableCharactersToUseAsOpponents.OutputPort
    ) {
        val theme = themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)

        if (! theme.containsCharacter(Character.Id(perspectiveCharacterId)))
            throw CharacterNotInTheme(themeId, perspectiveCharacterId)

        val perspectiveCharacter = theme.getMajorCharacterById(Character.Id(perspectiveCharacterId))
            ?: throw CharacterIsNotMajorCharacterInTheme(perspectiveCharacterId, themeId)

        output.receiveAvailableCharactersToUseAsOpponents(
            AvailableCharactersToUseAsOpponents(
                theme.id.uuid,
                perspectiveCharacter.id.uuid,
                theme.characters.asSequence()
                    .filterNot { it.id == perspectiveCharacter.id }
                    .filterNot { perspectiveCharacter.getStoryFunctionsForCharacter(it.id) == StoryFunction.Antagonist }
                    .map(::CharacterItem).toList()
            )
        )
    }

}