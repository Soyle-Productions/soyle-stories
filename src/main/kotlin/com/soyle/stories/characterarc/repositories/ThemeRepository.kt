package com.soyle.stories.characterarc.repositories

import com.soyle.stories.entities.Theme

/**
 * Created by Brendan
 * Date: 2/26/2020
 * Time: 4:22 PM
 */
interface ThemeRepository {
    suspend fun addNewTheme(theme: Theme)
    suspend fun getThemeById(themeId: Theme.Id): Theme?
}