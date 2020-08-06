package com.soyle.stories.characterarc.repositories

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme

interface ThemeRepository {
    suspend fun addNewTheme(theme: Theme)
    suspend fun getThemeById(id: Theme.Id): Theme?
    suspend fun listAllThemesInProject(projectId: Project.Id): List<Theme>
    suspend fun updateTheme(theme: Theme)
}