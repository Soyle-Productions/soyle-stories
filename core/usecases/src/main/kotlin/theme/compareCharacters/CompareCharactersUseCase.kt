package com.soyle.stories.usecase.theme.compareCharacters

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class CompareCharactersUseCase(
    private val themeRepository: ThemeRepository
) : CompareCharacters {

    override suspend fun invoke(themeId: UUID, focusCharacterId: UUID?, outputPort: CompareCharacters.OutputPort) {
        val response = try {
            compareCharacters(themeId, focusCharacterId)
        } catch (t: Exception) {
            return outputPort.receiveCompareCharactersFailure(t)
        }
        outputPort.receiveCharacterComparison(response)
    }

    private suspend fun compareCharacters(themeId: UUID, focusCharacterId: UUID?): CompareCharacters.ResponseModel {
        val theme = getThemeById(themeId)
        return CompareCharacters.ResponseModel(themeId, theme.characters.map {

        })
    }

    private suspend fun getThemeById(themeId: UUID): Theme {
        return themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)
    }

    private fun Theme.getFocusCharacter(focusCharacterId: UUID): MajorCharacter {
        return getMajorCharacterById(Character.Id(focusCharacterId))
            ?: throw CharacterNotInTheme(id.uuid, focusCharacterId)
    }
}