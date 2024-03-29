package com.soyle.stories.theme.themeList

data class ThemeListViewModel(
    val emptyMessage: String,
    val createFirstThemeButtonLabel: String,
    val themes: List<ThemeListItemViewModel>,
    val createThemeButtonLabel: String,
    val createSymbolButtonLabel: String,
    val deleteButtonLabel: String
)

data class ThemeListItemViewModel(val themeId: String, val themeName: String, val symbols: List<SymbolListItemViewModel>)
data class SymbolListItemViewModel(val symbolId: String, val symbolName: String)