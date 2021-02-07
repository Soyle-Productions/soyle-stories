package com.soyle.stories.usecase.theme.changeCharacterPropertyValue

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.changeCharacterPropertyValue.ChangeCharacterPropertyValue.*
import java.util.*

class ChangeCharacterPropertyValueUseCase(
    private val themeRepository: ThemeRepository
) : ChangeCharacterPropertyValue {

    override suspend fun invoke(request: RequestModel, output: OutputPort) {
        val response = try {
            changeCharacterPropertyValue(request)
        } catch (t: Exception) {
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
                { themeRepository.updateTheme(it) }
            )
            Property.VariationOnMoral -> theme.changeVariationOnMoral(characterInTheme, request.value).fold(
                { throw it },
                { themeRepository.updateTheme(it) }
            )
            Property.Ability -> theme.withCharacterHoldingPosition(characterInTheme.id, request.value).let {
                themeRepository.updateTheme(it)
            }
        }
    }

    private suspend fun getTheme(themeId: UUID) =
        (themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId))

    private fun getCharacterInTheme(theme: Theme, characterId: UUID): CharacterInTheme {
        return theme.getIncludedCharacterById(Character.Id(characterId))
            ?: throw CharacterNotInTheme(theme.id.uuid, characterId)
    }

    private fun convertRequestToResponse(request: RequestModel): ResponseModel {
        return ResponseModel(request.themeId, request.characterId, request.property, request.value)
    }
}