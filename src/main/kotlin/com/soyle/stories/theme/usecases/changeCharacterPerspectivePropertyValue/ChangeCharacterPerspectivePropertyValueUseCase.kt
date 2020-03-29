package com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue

import arrow.core.Either
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.CharacterInTheme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue.*
import java.util.*

class ChangeCharacterPerspectivePropertyValueUseCase(
    private val context: Context
) : ChangeCharacterPerspectivePropertyValue {

    override suspend fun invoke(request: RequestModel, output: OutputPort) {
        val response = try {
            changeCharacterPerspectivePropertyValue(request)
        } catch (t: ThemeException) {
            return output.receiveChangeCharacterPerspectivePropertyValueFailure(t)
        }
        output.receiveChangeCharacterPerspectivePropertyValueResponse(response)
    }

    private suspend fun changeCharacterPerspectivePropertyValue(request: RequestModel): ResponseModel {
        val theme = getTheme(request.themeId)
        val perspectiveCharacter = getMajorCharacterInTheme(theme, request.perspectiveCharacterId)
        changeProperty(theme, perspectiveCharacter, request)
        return convertRequestToResponse(request)
    }

    private suspend fun getTheme(themeId: UUID) =
        (context.themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId))

    private fun getMajorCharacterInTheme(theme: Theme, characterId: UUID): MajorCharacter {
        return getCharacterInTheme(theme, characterId) as? MajorCharacter
            ?: throw CharacterIsNotMajorCharacterInTheme(characterId, theme.id.uuid)
    }

    private fun getCharacterInTheme(theme: Theme, characterId: UUID): CharacterInTheme {
        return theme.getIncludedCharacterById(Character.Id(characterId))
            ?: throw CharacterNotInTheme(theme.id.uuid, characterId)
    }

    private suspend fun changeProperty(theme: Theme, perspectiveCharacter: MajorCharacter, request: RequestModel) {
        changeAppropriateProperty(theme, perspectiveCharacter, request)
            .fold({ throw it}, {
                context.themeRepository.updateTheme(it)
            })
    }

    private fun changeAppropriateProperty(theme: Theme, perspectiveCharacter: MajorCharacter, request: RequestModel): Either<ThemeException, Theme> {
        return when (request.property) {
            Property.Attack -> theme.changeAttack(perspectiveCharacter, Character.Id(request.targetCharacterId), request.value)
            Property.Similarities -> theme.changeSimilarities(perspectiveCharacter.id, Character.Id(request.targetCharacterId), request.value)
        }
    }

    private fun convertRequestToResponse(request: RequestModel): ResponseModel {
        return ResponseModel(
            request.themeId,
            request.perspectiveCharacterId,
            request.targetCharacterId,
            request.property,
            request.value
        )
    }
}