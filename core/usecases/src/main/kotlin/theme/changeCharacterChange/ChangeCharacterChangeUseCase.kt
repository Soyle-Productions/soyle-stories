package com.soyle.stories.usecase.theme.changeCharacterChange

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class ChangeCharacterChangeUseCase(
    private val themeRepository: ThemeRepository
) : ChangeCharacterChange {

    override suspend fun invoke(request: ChangeCharacterChange.RequestModel, output: ChangeCharacterChange.OutputPort) {
        val theme = themeRepository.getThemeOrError(Theme.Id(request.themeId))
        val character = getMajorCharacterInTheme(theme, request.characterId)
        themeRepository.updateTheme(theme.withCharacterChangeAs(character.id, request.characterChange))
        output.characterChangeChanged(response(request))
    }

    private fun response(
        request: ChangeCharacterChange.RequestModel
    ): ChangeCharacterChange.ResponseModel {
        return ChangeCharacterChange.ResponseModel(
            ChangedCharacterChange(request.themeId, request.characterId, request.characterChange)
        )
    }

    private fun getMajorCharacterInTheme(theme: Theme, characterId: UUID): MajorCharacter {
        val characterInTheme = getCharacterInTheme(theme, characterId)
        if (characterInTheme !is MajorCharacter)
            throw CharacterIsNotMajorCharacterInTheme(characterId, theme.id.uuid)
        return characterInTheme
    }

    private fun getCharacterInTheme(theme: Theme, characterId: UUID): CharacterInTheme {
        return theme.getIncludedCharacterById(Character.Id(characterId))
            ?: throw CharacterNotInTheme(theme.id.uuid, characterId)
    }
}