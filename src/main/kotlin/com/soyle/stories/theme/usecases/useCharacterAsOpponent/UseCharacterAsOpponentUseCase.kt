package com.soyle.stories.theme.usecases.useCharacterAsOpponent

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.CharacterInTheme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.entities.theme.StoryFunction
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.UseCharacterAsOpponent.*
import java.util.*

class UseCharacterAsOpponentUseCase(
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository
) : UseCharacterAsOpponent {

    override suspend fun invoke(request: RequestModel, output: OutputPort) {
        var theme = getTheme(request.themeId)
        val perspectiveCharacter = getPerspectiveCharacter(theme, request.perspectiveCharacterId)

        val containsCharacter = theme.containsCharacter(Character.Id(request.opponentId))
        if (!containsCharacter) {
            theme = includeCharacterInTheme(request.opponentId, theme)
        }

        val opponentCharacter = theme.getIncludedCharacterById(Character.Id(request.opponentId))!!

        setCharacterAsOpponentForPerspectiveCharacter(theme, opponentCharacter, perspectiveCharacter)
        output.characterIsOpponent(
            responseModel(theme, opponentCharacter, perspectiveCharacter, containsCharacter)
        )
    }

    private fun responseModel(
        theme: Theme,
        opponentCharacter: CharacterInTheme,
        perspectiveCharacter: MajorCharacter,
        containsCharacter: Boolean
    ): ResponseModel {
        return ResponseModel(
            opponentCharacter(theme, opponentCharacter, perspectiveCharacter),
            characterIncludedInTheme(containsCharacter, theme, opponentCharacter)
        )
    }

    private fun characterIncludedInTheme(
        containsCharacter: Boolean,
        theme: Theme,
        opponentCharacter: CharacterInTheme
    ): CharacterIncludedInTheme? {
        return if (!containsCharacter) CharacterIncludedInTheme(
            theme.id.uuid,
            theme.name,
            opponentCharacter.id.uuid,
            opponentCharacter.name,
            false
        ) else null
    }

    private suspend fun includeCharacterInTheme(
        opponentId: UUID,
        theme: Theme
    ): Theme {
        val opponentCharacter = characterRepository.getCharacterById(Character.Id(opponentId))
            ?: throw CharacterDoesNotExist(opponentId)
        return theme.withCharacterIncluded(opponentCharacter.id, opponentCharacter.name, opponentCharacter.media)
    }

    private suspend fun getTheme(themeId: UUID) =
        (themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId))

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
        theme: Theme,
        opponentCharacter: CharacterInTheme,
        perspectiveCharacter: MajorCharacter
    ): OpponentCharacter {
        return OpponentCharacter(
            opponentCharacter.id.uuid,
            opponentCharacter.name,
            perspectiveCharacter.id.uuid,
            theme.id.uuid
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