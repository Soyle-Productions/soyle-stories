package com.soyle.stories.theme.usecases.removeCharacterFromComparison

import arrow.core.identity
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.theme.Context
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparison.OutputPort
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparison.ResponseModel
import java.util.*

class RemoveCharacterFromComparisonUseCase(
    private val context: Context
) : RemoveCharacterFromComparison {

    override suspend fun invoke(themeId: UUID, characterId: UUID, outputPort: OutputPort) {
        val response: ResponseModel = try {
            removeCharacterFromComparison(themeId, characterId)
        } catch (t: ThemeException) {
            return outputPort.receiveRemoveCharacterFromComparisonFailure(t)
        }
        outputPort.receiveRemoveCharacterFromComparisonResponse(response)
    }

    private suspend fun removeCharacterFromComparison(themeId: UUID, characterId: UUID): ResponseModel {
        val theme = getTheme(themeId)
        val needToRemoveCharacterArc = determineIfCharacterArcMustBeRemoved(theme, characterId)
        val updatedTheme = getThemeAfterCharacterRemoved(theme, characterId)
        val shouldDelete = determineIfThemeShouldBeDeleted(updatedTheme)
        persistTheme(updatedTheme, shouldDelete)
        removeCharacterArcIfNecessary(theme.id, Character.Id(characterId), needToRemoveCharacterArc)
        return ResponseModel(themeId, characterId, shouldDelete)
    }

    private suspend fun getTheme(themeId: UUID): Theme
    {
        return context.themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)
    }

    private fun getThemeAfterCharacterRemoved(theme: Theme, characterId: UUID): Theme
    {
        return theme.removeCharacter(Character.Id(characterId))
            .fold({ throw it }, ::identity)
    }

    private fun determineIfCharacterArcMustBeRemoved(theme: Theme, characterId: UUID): Boolean
    {
        return theme.getMajorCharacterById(Character.Id(characterId)) != null
    }

    private fun determineIfThemeShouldBeDeleted(theme: Theme): Boolean
    {
        return theme.characters.none { it is MajorCharacter }
    }

    private suspend fun persistTheme(theme: Theme, delete: Boolean)
    {
        if (delete) {
            context.themeRepository.deleteTheme(theme)
        } else {
            context.themeRepository.updateTheme(theme)
        }
    }

    private suspend fun removeCharacterArcIfNecessary(themeId: Theme.Id, characterId: Character.Id, isNecessary: Boolean) {
        if (isNecessary) context.characterArcRepository.removeCharacterArc(themeId, characterId)
    }
}