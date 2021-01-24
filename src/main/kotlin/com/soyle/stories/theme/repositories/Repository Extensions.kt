package com.soyle.stories.theme.repositories

import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.ThemeDoesNotExist
import java.util.*

suspend fun ThemeRepository.getThemeOrError(themeId: Theme.Id): Theme =
    getThemeById(themeId) ?: throw ThemeDoesNotExist(themeId.uuid)