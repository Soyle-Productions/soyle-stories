package com.soyle.stories.theme.repositories

import com.soyle.stories.entities.Theme

/**
 * Created by Brendan
 * Date: 2/24/2020
 * Time: 5:25 PM
 */
interface ThemeRepository {
    suspend fun getThemeById(id: Theme.Id): Theme?
    suspend fun updateTheme(theme: Theme)
    suspend fun deleteTheme(theme: Theme)
}