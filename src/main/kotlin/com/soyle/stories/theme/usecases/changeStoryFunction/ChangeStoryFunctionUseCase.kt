/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 10:25 AM
 */
package com.soyle.stories.theme.usecases.changeStoryFunction

import arrow.core.flatMap
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.CharacterInTheme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.entities.theme.StoryFunction
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.Context
import com.soyle.stories.theme.ThemeDoesNotExist
import java.util.*

class ChangeStoryFunctionUseCase(
    private val context: Context
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
        return context.themeRepository.getThemeById(Theme.Id(themeId))
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
                    { context.themeRepository.updateTheme(it) }
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