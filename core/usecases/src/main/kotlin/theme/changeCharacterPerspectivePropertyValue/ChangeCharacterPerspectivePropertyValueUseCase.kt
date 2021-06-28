package com.soyle.stories.usecase.theme.changeCharacterPerspectivePropertyValue

import arrow.core.Either
import arrow.core.right
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue.*
import java.util.*

class ChangeCharacterPerspectivePropertyValueUseCase(
    private val themeRepository: ThemeRepository
) : ChangeCharacterPerspectivePropertyValue {

    override suspend fun invoke(request: RequestModel, output: OutputPort) {
        val response = try {
            changeCharacterPerspectivePropertyValue(request)
        } catch (t: Exception) {
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
        (themeRepository.getThemeById(Theme.Id(themeId))
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
                themeRepository.updateTheme(it)
            })
    }

    private fun changeAppropriateProperty(theme: Theme, perspectiveCharacter: MajorCharacter, request: RequestModel): Either<Exception, Theme> {
        return when (request.property) {
            Property.Attack -> theme.withCharacterAttackingMajorCharacter(
                Character.Id(request.targetCharacterId),
                request.value,
                perspectiveCharacter.id
            ).right()
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