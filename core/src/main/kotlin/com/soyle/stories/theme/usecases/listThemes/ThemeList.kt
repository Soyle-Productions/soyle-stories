package com.soyle.stories.theme.usecases.listThemes

import com.soyle.stories.theme.usecases.ThemeItem

class ThemeList(val themes: List<ThemeItem>) {
    fun isEmpty() = themes.isEmpty()
}