package com.soyle.stories.theme.usecases.compareCharacters

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.Context
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharacters.OutputPort
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharacters.ResponseModel
import java.util.*

/**
 * Created by Brendan
 * Date: 2/24/2020
 * Time: 5:04 PM
 */
class CompareCharactersUseCase(
    private val context: Context
) : CompareCharacters {

    override suspend fun invoke(themeId: UUID, focusCharacterId: UUID, outputPort: OutputPort) {
        val response = try {
            compareCharacters(themeId, focusCharacterId)
        } catch (t: ThemeException) {
            return outputPort.receiveCompareCharactersFailure(t)
        }
        outputPort.receiveCharacterComparison(response)
    }

    private suspend fun compareCharacters(themeId: UUID, focusCharacterId: UUID): ResponseModel {
        val theme = getThemeById(themeId)
        return CharacterComparor(
            theme = theme,
            focusCharacter = theme.getFocusCharacter(focusCharacterId),
            arcSections = getArcSectionsIn(theme)
        ).compareCharacters()
    }

    private suspend fun getThemeById(themeId: UUID): Theme {
        return context.themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)
    }

    private fun Theme.getFocusCharacter(focusCharacterId: UUID): MajorCharacter {
        return getMajorCharacterById(Character.Id(focusCharacterId))
            ?: throw CharacterNotInTheme(id.uuid, focusCharacterId)
    }

    private suspend fun getArcSectionsIn(theme: Theme): List<CharacterArcSection> =
        context.characterArcSectionRepository.getCharacterArcSectionsForTheme(theme.id)
}