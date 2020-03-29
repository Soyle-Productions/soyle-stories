/**
 * Created by Brendan
 * Date: 2/28/2020
 * Time: 3:48 PM
 */
package com.soyle.stories.character.repositories

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme

interface ThemeRepository {
    suspend fun getThemesWithCharacterIncluded(characterId: Character.Id): List<Theme>
    suspend fun updateThemes(themes: List<Theme>)
    suspend fun deleteThemes(themes: List<Theme>)
}