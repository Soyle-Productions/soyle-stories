package com.soyle.stories.theme.usecases.useCharacterAsOpponent

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.CharacterInTheme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.entities.theme.StoryFunction
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.UseCharacterAsOpponent.*
import java.util.*

class UseCharacterAsOpponentUseCase(
    private val themeRepository: ThemeRepository
) : UseCharacterAsOpponent {

    override suspend fun invoke(request: RequestModel, output: OutputPort) {
        val theme = getTheme(request.themeId)
        val perspectiveCharacter = getPerspectiveCharacter(theme, request.perspectiveCharacterId)
        val opponentCharacter = getCharacterInTheme(theme, request.opponentId)
        setCharacterAsOpponentForPerspectiveCharacter(theme, opponentCharacter, perspectiveCharacter)
        output.characterIsOpponent(opponentCharacter(opponentCharacter, perspectiveCharacter))
    }

    private suspend fun getTheme(themeId: UUID) =
        (themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId))

    private fun getCharacterInTheme(
        theme: Theme,
        opponentId: UUID
    ) = (theme.getIncludedCharacterById(Character.Id(opponentId))
        ?: throw CharacterNotInTheme(theme.id.uuid, opponentId))

    private fun getPerspectiveCharacter(
        theme: Theme,
        perspectiveCharacterId: UUID
    ): MajorCharacter {
        if (!theme.containsCharacter(Character.Id(perspectiveCharacterId))) {
            throw CharacterNotInTheme(theme.id.uuid, perspectiveCharacterId)
        }

        return theme.getMajorCharacterById(Character.Id(perspectiveCharacterId))
            ?: throw CharacterIsNotMajorCharacterInTheme(perspectiveCharacterId, theme.id.uuid)
    }

    private fun opponentCharacter(
        opponentCharacter: CharacterInTheme,
        perspectiveCharacter: MajorCharacter
    ): OpponentCharacter {
        return OpponentCharacter(
            opponentCharacter.id.uuid,
            opponentCharacter.name,
            perspectiveCharacter.id.uuid
        )
    }

    private suspend fun setCharacterAsOpponentForPerspectiveCharacter(
        theme: Theme,
        opponentCharacter: CharacterInTheme,
        perspectiveCharacter: MajorCharacter
    ) {
        themeRepository.updateTheme(
            theme.withCharacterAsStoryFunctionForMajorCharacter(
                opponentCharacter.id,
                StoryFunction.Antagonist,
                perspectiveCharacter.id
            )
        )
    }

}