package com.soyle.stories.theme.usecases.useCharacterAsMainOpponent

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.CharacterInTheme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.entities.theme.StoryFunction
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.CharacterUsedAsOpponent

class UseCharacterAsMainOpponentUseCase(
    private val themeRepository: ThemeRepository
) : UseCharacterAsMainOpponent {

    override suspend fun invoke(
        request: UseCharacterAsMainOpponent.RequestModel,
        output: UseCharacterAsMainOpponent.OutputPort
    ) {
        val theme = getTheme(request)
        val perspectiveCharacter = getPerspectiveCharacter(theme, request)
        val opponentCharacter = getCharacterInTheme(theme, request)
        val previousMainOpponent = changeMainOpponent(theme, perspectiveCharacter, opponentCharacter)
        output.characterUsedAsMainOpponent(
            responseModel(opponentCharacter, perspectiveCharacter, theme, previousMainOpponent)
        )
    }

    private suspend fun getTheme(request: UseCharacterAsMainOpponent.RequestModel) =
        (themeRepository.getThemeById(Theme.Id(request.themeId))
            ?: throw ThemeDoesNotExist(request.themeId))

    private fun getPerspectiveCharacter(
        theme: Theme,
        request: UseCharacterAsMainOpponent.RequestModel
    ): MajorCharacter {
        if (!theme.containsCharacter(Character.Id(request.perspectiveCharacterId)))
            throw CharacterNotInTheme(request.themeId, request.perspectiveCharacterId)

        return theme.getMajorCharacterById(Character.Id(request.perspectiveCharacterId))
            ?: throw CharacterIsNotMajorCharacterInTheme(request.perspectiveCharacterId, request.themeId)
    }

    private fun getCharacterInTheme(
        theme: Theme,
        request: UseCharacterAsMainOpponent.RequestModel
    ) = (theme.getIncludedCharacterById(Character.Id(request.opponentCharacterId))
        ?: throw CharacterNotInTheme(request.themeId, request.opponentCharacterId))

    private suspend fun changeMainOpponent(
        originalTheme: Theme,
        perspectiveCharacter: MajorCharacter,
        opponentCharacter: CharacterInTheme
    ): CharacterInTheme? {
        val (theme, currentMainOpponent) = removeCurrentMainOpponent(originalTheme, perspectiveCharacter, opponentCharacter)
        setCharacterAsMainAntagonist(theme, opponentCharacter.id, perspectiveCharacter.id)
        return currentMainOpponent
    }

    private fun removeCurrentMainOpponent(
        theme: Theme,
        perspectiveCharacter: MajorCharacter,
        opponentCharacter: CharacterInTheme
    ): Pair<Theme, CharacterInTheme?> {
        val currentMainOpponent = findCurrentMainOpponent(theme, perspectiveCharacter, opponentCharacter)

        if (currentMainOpponent != null) {
            return theme.withCharacterAsStoryFunctionForMajorCharacter(
                currentMainOpponent.id,
                StoryFunction.Antagonist,
                perspectiveCharacter.id
            ) to currentMainOpponent
        }
        return theme to null
    }

    private fun findCurrentMainOpponent(
        theme: Theme,
        perspectiveCharacter: MajorCharacter,
        opponentCharacter: CharacterInTheme
    ): CharacterInTheme? {
        return theme.characters.asSequence()
            .filterNot { it.id == perspectiveCharacter.id }
            .filterNot { it.id == opponentCharacter.id }
            .find { perspectiveCharacter.hasStoryFunctionForTargetCharacter(StoryFunction.MainAntagonist, it.id) }
    }

    private suspend fun setCharacterAsMainAntagonist(
        theme: Theme,
        opponentCharacterId: Character.Id,
        perspectiveCharacterId: Character.Id
    ) {
        themeRepository.updateTheme(
            theme.withCharacterAsStoryFunctionForMajorCharacter(
                opponentCharacterId,
                StoryFunction.MainAntagonist,
                perspectiveCharacterId
            )
        )
    }

    private fun responseModel(
        opponentCharacter: CharacterInTheme,
        perspectiveCharacter: MajorCharacter,
        theme: Theme,
        previousMainOpponent: CharacterInTheme?
    ): UseCharacterAsMainOpponent.ResponseModel {
        return UseCharacterAsMainOpponent.ResponseModel(
            mainOpponent(opponentCharacter, perspectiveCharacter, theme),
            previousMainOpponent(previousMainOpponent, perspectiveCharacter, theme)
        )
    }

    private fun mainOpponent(
        opponentCharacter: CharacterInTheme,
        perspectiveCharacter: MajorCharacter,
        theme: Theme
    ): CharacterUsedAsMainOpponent {
        return CharacterUsedAsMainOpponent(
            opponentCharacter.id.uuid, opponentCharacter.name, perspectiveCharacter.id.uuid, theme.id.uuid
        )
    }

    private fun previousMainOpponent(
        currentMainOpponent: CharacterInTheme?,
        perspectiveCharacter: MajorCharacter,
        theme: Theme
    ) = currentMainOpponent?.let {
        CharacterUsedAsOpponent(
            it.id.uuid, it.name, perspectiveCharacter.id.uuid, theme.id.uuid
        )
    }


}