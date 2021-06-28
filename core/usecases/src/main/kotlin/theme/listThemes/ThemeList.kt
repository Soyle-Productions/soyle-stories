package com.soyle.stories.usecase.theme.listThemes

import com.soyle.stories.usecase.theme.ThemeItem

class ThemeList(val themes: List<ThemeItem>) {
    fun isEmpty() = themes.isEmpty()
}