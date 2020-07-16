package com.soyle.stories.characterarc.repositories

import com.soyle.stories.entities.Theme

interface ThemeRepository {
    suspend fun addNewTheme(theme: Theme)
    suspend fun getThemeById(id: Theme.Id): Theme?
}