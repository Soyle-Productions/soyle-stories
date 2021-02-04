package com.soyle.stories.character.repositories

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme

interface ThemeRepository {
    suspend fun getThemeById(id: Theme.Id): Theme?
    suspend fun getThemesWithCharacterIncluded(characterId: Character.Id): List<Theme>
    suspend fun updateThemes(themes: List<Theme>)
    suspend fun deleteThemes(themes: List<Theme>)
}