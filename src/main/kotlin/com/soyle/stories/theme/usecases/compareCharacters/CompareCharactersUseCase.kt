package com.soyle.stories.theme.usecases.compareCharacters

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharacters.OutputPort
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharacters.ResponseModel
import java.util.*

class CompareCharactersUseCase(
    private val themeRepository: ThemeRepository
) : CompareCharacters {

    override suspend fun invoke(themeId: UUID, focusCharacterId: UUID?, outputPort: OutputPort) {
        val response = try {
            compareCharacters(themeId, focusCharacterId)
        } catch (t: ThemeException) {
            return outputPort.receiveCompareCharactersFailure(t)
        }
        outputPort.receiveCharacterComparison(response)
    }

    private suspend fun compareCharacters(themeId: UUID, focusCharacterId: UUID?): ResponseModel {
        val theme = getThemeById(themeId)
        return ResponseModel(themeId, theme.characters.map {
            Unit
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