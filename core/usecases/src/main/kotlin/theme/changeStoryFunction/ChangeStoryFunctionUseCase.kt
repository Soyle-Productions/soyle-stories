package com.soyle.stories.usecase.theme.changeStoryFunction

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.domain.theme.characterInTheme.StoryFunction
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class ChangeStoryFunctionUseCase(
    private val themeRepository: ThemeRepository
) : ChangeStoryFunction {

    override suspend fun invoke(request: ChangeStoryFunction.RequestModel, outputPort: ChangeStoryFunction.OutputPort) {
        val responseModel = try {
            changeStoryFunction(request)
        } catch (e: Exception) {
            return outputPort.receiveChangeStoryFunctionFailure(e)
        }
        outputPort.receiveChangeStoryFunctionResponse(responseModel)
    }

    private suspend fun changeStoryFunction(
        request: ChangeStoryFunction.RequestModel
    ): ChangeStoryFunction.ResponseModel {
        val theme = getThemeById(request.themeId)
        val perspectiveCharacter = theme.findCharacter(request.perspectiveCharacterId)
        val targetCharacter = theme.findCharacter(request.targetCharacterId)

        if (perspectiveCharacter !is MajorCharacter) throw notMajorCharacter(theme, perspectiveCharacter)


        theme.applyStoryFunctionIfNotAlready(
            perspectiveCharacter,
            targetCharacter.id,
            StoryFunction.valueOf(request.storyFunction.name)
        )

        return ChangeStoryFunction.ResponseModel(
            theme.id.uuid,
            perspectiveCharacter.id.uuid,
            targetCharacter.id.uuid,
            request.storyFunction.name
        )
    }

    private suspend fun getThemeById(themeId: UUID): Theme {
        return themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)
    }

    private fun Theme.findCharacter(characterId: UUID): CharacterInTheme {
        return getIncludedCharacterById(Character.Id(characterId))
            ?: throw  CharacterNotInTheme(
                id.uuid,
                characterId
            )
    }

    private fun notMajorCharacter(
        theme: Theme,
        characterInTheme: CharacterInTheme
    ): CharacterIsNotMajorCharacterInTheme {
        return CharacterIsNotMajorCharacterInTheme(characterInTheme.id.uuid, theme.id.uuid)
    }

    private suspend fun Theme.applyStoryFunctionIfNotAlready(
        perspectiveCharacter: MajorCharacter,
        targetCharacterId: Character.Id,
        storyFunction: StoryFunction
    ) {
        if (!perspectiveCharacter.hasStoryFunctionForTargetCharacter(storyFunction, targetCharacterId)) {
            replaceStoryFunction(perspectiveCharacter, targetCharacterId, storyFunction)
                .fold(
                    { throw it },
                    { themeRepository.updateTheme(it) }
                )
        }
    }

    private fun Theme.replaceStoryFunction(
        majorCharacter: MajorCharacter,
        targetCharacterId: Character.Id,
        storyFunction: StoryFunction
    ) = clearStoryFunctions(majorCharacter, targetCharacterId)
        .map {
            val updatedPerceivedCharacter = it.getMajorCharacterById(majorCharacter.id)!!
            it.withCharacterAsStoryFunctionForMajorCharacter(
                targetCharacterId,
                storyFunction,
                updatedPerceivedCharacter.id
            )
        }
}