package com.soyle.stories.theme.themeList

data class ThemeListViewModel(
    val emptyMessage: String,
    val createFirstThemeButtonLabel: String,
    val themes: List<ThemeListItemViewModel>,
    val createThemeButtonLabel: String
)

class ThemeListItemViewModel(val themeId: String, val themeName: String, val symbols: List<SymbolListItemViewModel>)
class SymbolListItemViewModel(val symbolId: String, val symbolName: String)