package com.soyle.stories.theme.usecases.changeCharacterPropertyValue

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.CharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.Context
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue.*
import java.util.*

class ChangeCharacterPropertyValueUseCase(
    private val context: Context
) : ChangeCharacterPropertyValue {

    override suspend fun invoke(request: RequestModel, output: OutputPort) {
        val response = try {
            changeCharacterPropertyValue(request)
        } catch (t: ThemeException) {
            return output.receiveChangeCharacterPropertyValueFailure(t)
        }
        output.receiveChangeCharacterPropertyValueResponse(response)
    }

    private suspend fun changeCharacterPropertyValue(request: RequestModel): ResponseModel {
        val theme = getTheme(request.themeId)
        val characterInTheme = getCharacterInTheme(theme, request.characterId)
        updatePropertyForCharacterInTheme(request, characterInTheme, theme)
        return convertRequestToResponse(request)
    }

    private suspend fun updatePropertyForCharacterInTheme(
        request: RequestModel,
        characterInTheme: CharacterInTheme,
        theme: Theme
    ) {
        when (request.property) {
            Property.Archetype -> theme.changeArchetype(characterInTheme, request.value).fold(
                { throw it },
                { context.themeRepository.updateTheme(it) }
            )
            Property.VariationOnMoral -> theme.changeVariationOnMoral(characterInTheme, request.value).fold(
                { throw it },
                { context.themeRepository.updateTheme(it) }
            )
            Property.Ability -> theme.withCharacterHoldingPosition(characterInTheme.id, request.value).let {
                context.themeRepository.updateTheme(it)
            }
        }
    }

    private suspend fun getTheme(themeId: UUID) =
        (context.themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId))

    private fun getCharacterInTheme(theme: Theme, characterId: UUID): CharacterInTheme {
        return theme.getIncludedCharacterById(Character.Id(characterId))
            ?: throw CharacterNotInTheme(theme.id.uuid, characterId)
    }

    private fun convertRequestToResponse(request: RequestModel): ResponseModel {
        return ResponseModel(request.themeId, request.characterId, request.property, request.value)
    }
}