package com.soyle.stories.theme.usecases.useCharacterAsOpponent

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.character.repositories.getCharacterOrError
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.entities.theme.characterInTheme.StoryFunction
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.repositories.getThemeOrError
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import java.util.*

class UseCharacterAsOpponentUseCase(
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository
) : ListAvailableCharactersToUseAsOpponents, UseCharacterAsOpponent, UseCharacterAsMainOpponent {

    /**
     * ListAvailableCharactersToUseAsOpponents
     */
    override suspend fun invoke(
        themeId: UUID,
        perspectiveCharacterId: UUID,
        output: ListAvailableCharactersToUseAsOpponents.OutputPort
    ) {
        val theme = themeRepository.getThemeOrError(Theme.Id(themeId))
        val perspectiveCharacter = theme.getMajorCharacterByIdOrError(Character.Id(perspectiveCharacterId))
        val response = getAvailableCharactersToUseAsOpponents(theme, perspectiveCharacter)
        output.receiveAvailableCharactersToUseAsOpponents(response)
    }

    /**
     * UseCharacterAsOpponent
     */
    override suspend fun invoke(
        request: UseCharacterAsOpponent.RequestModel,
        output: UseCharacterAsOpponent.OutputPort
    ) {
        val theme = themeRepository.getThemeOrError(Theme.Id(request.themeId))
        val perspectiveCharacter = theme.getMajorCharacterByIdOrError(Character.Id(request.perspectiveCharacterId))

        val themeWithOpponent = theme.withCharacterIncludedIfNotAlready(request.opponentId)
        val opponentCharacter = themeWithOpponent.getIncludedCharacterByIdOrError(Character.Id(request.opponentId))

        setCharacterAsOpponentForPerspectiveCharacter(themeWithOpponent, opponentCharacter, perspectiveCharacter)
        val response = createResponseModel(theme, opponentCharacter, perspectiveCharacter, !theme.containsCharacter(opponentCharacter.id))
        output.characterIsOpponent(response)
    }

    /**
     * UseCharacterAsMainOpponent
     */
    override suspend fun invoke(
        request: UseCharacterAsMainOpponent.RequestModel,
        output: UseCharacterAsMainOpponent.OutputPort
    ) {
        val theme = themeRepository.getThemeOrError(Theme.Id(request.themeId))
        val perspectiveCharacter = theme.getMajorCharacterByIdOrError(Character.Id(request.perspectiveCharacterId))
        val opponentCharacter = theme.getIncludedCharacterByIdOrError(Character.Id(request.opponentCharacterId))

        val previousMainOpponent = changeMainOpponent(theme, perspectiveCharacter, opponentCharacter)
        output.characterUsedAsMainOpponent(
            responseModel(opponentCharacter, perspectiveCharacter, theme, previousMainOpponent)
        )
    }

    private suspend fun Theme.withCharacterIncludedIfNotAlready(characterId: UUID): Theme {
        return if (!containsCharacter(Character.Id(characterId))) {
            withCharacterIncluded(characterId)
        } else {
            this
        }
    }

    private suspend fun changeMainOpponent(
        originalTheme: Theme,
        perspectiveCharacter: MajorCharacter,
        opponentCharacter: CharacterInTheme
    ): CharacterInTheme? {
        val (theme, currentMainOpponent) = removeCurrentMainOpponent(
            originalTheme,
            perspectiveCharacter,
            opponentCharacter
        )
        setCharacterAsMainAntagonist(theme, opponentCharacter.id, perspectiveCharacter.id)
        return currentMainOpponent
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

    private fun responseModel(
        opponentCharacter: CharacterInTheme,
        perspectiveCharacter: MajorCharacter,
        theme: Theme,
        previousMainOpponent: CharacterInTheme?
    ): UseCharacterAsMainOpponent.ResponseModel {
        return UseCharacterAsMainOpponent.ResponseModel(
            reportCharacterUsedAsMainOpponent(opponentCharacter, perspectiveCharacter, theme),
            reportCharacterUsedAsOpponentIfExists(theme, previousMainOpponent, perspectiveCharacter)
        )
    }

    private fun reportCharacterUsedAsMainOpponent(
        opponentCharacter: CharacterInTheme,
        perspectiveCharacter: MajorCharacter,
        theme: Theme
    ): CharacterUsedAsMainOpponent {
        return CharacterUsedAsMainOpponent(
            opponentCharacter.id.uuid, opponentCharacter.name, perspectiveCharacter.id.uuid, theme.id.uuid
        )
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

    private fun createResponseModel(
        theme: Theme,
        opponentCharacter: CharacterInTheme,
        perspectiveCharacter: MajorCharacter,
        hadToIncludeOpponent: Boolean
    ): UseCharacterAsOpponent.ResponseModel {
        return UseCharacterAsOpponent.ResponseModel(
            reportCharacterUsedAsOpponent(theme, opponentCharacter, perspectiveCharacter),
            reportCharacterIncludedInThemeIfNeeded(hadToIncludeOpponent, theme, opponentCharacter)
        )
    }

    private fun reportCharacterIncludedInThemeIfNeeded(
        hadToIncludeOpponent: Boolean,
        theme: Theme,
        opponentCharacter: CharacterInTheme
    ): CharacterIncludedInTheme? =
        if (hadToIncludeOpponent) reportCharacterIncludedInTheme(theme, opponentCharacter) else null

    private fun reportCharacterIncludedInTheme(
        theme: Theme,
        opponentCharacter: CharacterInTheme
    ): CharacterIncludedInTheme {
        return CharacterIncludedInTheme(
            theme.id.uuid,
            theme.name,
            opponentCharacter.id.uuid,
            opponentCharacter.name,
            false
        )
    }

    private suspend fun Theme.withCharacterIncluded(
        opponentId: UUID,
    ): Theme {
        val opponentCharacter = characterRepository.getCharacterOrError(opponentId)
        return withCharacterIncluded(opponentCharacter.id, opponentCharacter.name, opponentCharacter.media)
    }

    private fun reportCharacterUsedAsOpponentIfExists(
        theme: Theme,
        opponentCharacter: CharacterInTheme?,
        perspectiveCharacter: MajorCharacter
    ) = opponentCharacter?.let { reportCharacterUsedAsOpponent(theme, it, perspectiveCharacter) }

    private fun reportCharacterUsedAsOpponent(
        theme: Theme,
        opponentCharacter: CharacterInTheme,
        perspectiveCharacter: MajorCharacter
    ): CharacterUsedAsOpponent {
        return CharacterUsedAsOpponent(
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
        setCharacterAsStoryFunctionForMajorCharacter(
            opponentCharacter.id,
            StoryFunction.Antagonist,
            perspectiveCharacter.id,
            theme
        )
    }

    private suspend fun setCharacterAsMainAntagonist(
        theme: Theme,
        opponentCharacterId: Character.Id,
        perspectiveCharacterId: Character.Id
    ) {
        setCharacterAsStoryFunctionForMajorCharacter(
            opponentCharacterId,
            StoryFunction.MainAntagonist,
            perspectiveCharacterId,
            theme
        )
    }

    private suspend fun setCharacterAsStoryFunctionForMajorCharacter(
        opponentCharacterId: Character.Id,
        storyFunction: StoryFunction,
        majorCharacterId: Character.Id,
        theme: Theme
    ) {
        themeRepository.updateTheme(
            theme.withCharacterAsStoryFunctionForMajorCharacter(
                opponentCharacterId,
                storyFunction,
                majorCharacterId
            )
        )
    }

    private suspend fun getAvailableCharactersToUseAsOpponents(
        theme: Theme,
        perspectiveCharacter: MajorCharacter
    ): AvailableCharactersToUseAsOpponents {

        val allCharactersNotYetInTheme = getAllCharactersNotYetInTheme(theme)
        val otherCharactersInThemeNotYetAntagonistsToCharacter =
            getCharactersInThemeNotAntagonisticTowardsMajorCharacter(theme, perspectiveCharacter)

        return AvailableCharactersToUseAsOpponents(
            theme.id.uuid,
            perspectiveCharacter.id.uuid,
            (allCharactersNotYetInTheme + otherCharactersInThemeNotYetAntagonistsToCharacter).toList()
        )
    }

    private suspend fun getAllCharactersNotYetInTheme(theme: Theme): Sequence<AvailableCharacterToUseAsOpponent> {
        return characterRepository.listCharactersInProject(theme.projectId)
            .asSequence()
            .filterNot { theme.containsCharacter(it.id) }
            .map { AvailableCharacterToUseAsOpponent(it.id.uuid, it.name, it.media?.uuid, false) }
    }

    private fun getCharactersInThemeNotAntagonisticTowardsMajorCharacter(
        theme: Theme,
        perspectiveCharacter: MajorCharacter
    ): Sequence<AvailableCharacterToUseAsOpponent> {
        return theme.characters
            .asSequence()
            .filterNot { it.id == perspectiveCharacter.id }
            .filterNot { it.isAntagonisticTowards(perspectiveCharacter) }
            .map { AvailableCharacterToUseAsOpponent(it.id.uuid, it.name, null, true) }
    }

}