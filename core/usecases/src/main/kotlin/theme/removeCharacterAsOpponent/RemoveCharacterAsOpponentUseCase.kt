package com.soyle.stories.usecase.theme.removeCharacterAsOpponent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.CharacterIsNotAnOpponentOfPerspectiveCharacter
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.domain.theme.characterInTheme.StoryFunction
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class RemoveCharacterAsOpponentUseCase(
    private val themeRepository: ThemeRepository
) : RemoveCharacterAsOpponent {

    override suspend fun invoke(
        request: RemoveCharacterAsOpponent.RequestModel,
        output: RemoveCharacterAsOpponent.OutputPort
    ) {
        val theme = themeRepository.getThemeOrError(Theme.Id(request.themeId))

        val perspectiveCharacter = getPerspectiveCharacter(theme, request.perspectiveCharacterId)

        val opponentId = getOpponentId(request, perspectiveCharacter)

        removeStoryFunction(theme, opponentId, perspectiveCharacter)

        output.removedCharacterAsOpponent(
            responseModel(opponentId, perspectiveCharacter, theme)
        )
    }

    private fun responseModel(
        opponentId: Character.Id,
        perspectiveCharacter: MajorCharacter,
        theme: Theme
    ): RemoveCharacterAsOpponent.ResponseModel {
        return RemoveCharacterAsOpponent.ResponseModel(
            CharacterRemovedAsOpponent(opponentId.uuid, perspectiveCharacter.id.uuid, theme.id.uuid)
        )
    }

    private suspend fun removeStoryFunction(
        theme: Theme,
        opponentId: Character.Id,
        perspectiveCharacter: MajorCharacter
    ) {
        themeRepository.updateTheme(
            theme.withoutCharacterAsStoryFunctionForPerspectiveCharacter(
                opponentId,
                perspectiveCharacter.id
            )
        )
    }

    private fun getOpponentId(
        request: RemoveCharacterAsOpponent.RequestModel,
        perspectiveCharacter: MajorCharacter
    ): Character.Id {
        val opponentId = Character.Id(request.opponentId)
        val storyFunction = perspectiveCharacter.getStoryFunctionsForCharacter(opponentId)
        if (!isOpponent(storyFunction))
            throw CharacterIsNotAnOpponentOfPerspectiveCharacter(
                request.themeId,
                request.opponentId,
                request.perspectiveCharacterId
            )
        return opponentId
    }

    private fun isOpponent(storyFunction: StoryFunction?) =
        storyFunction == StoryFunction.Antagonist || storyFunction == StoryFunction.MainAntagonist

    private fun getPerspectiveCharacter(
        theme: Theme,
        perspectiveCharacterId: UUID
    ): MajorCharacter {
        val perspectiveCharacter = theme.getIncludedCharacterById(Character.Id(perspectiveCharacterId))
            ?: throw CharacterNotInTheme(theme.id.uuid, perspectiveCharacterId)
        if (perspectiveCharacter !is MajorCharacter) throw CharacterIsNotMajorCharacterInTheme(
            perspectiveCharacterId,
            theme.id.uuid
        )
        return perspectiveCharacter
    }

}