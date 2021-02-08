package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.scene.items.SceneItemViewModel

data class SymbolsInSceneViewModel(
    val targetScene: SceneItemViewModel?,
    val themesInScene: List<ThemeInScene>,
    val availableThemesToTrack: List<AvailableTheme>
) {

    data class ThemeInScene(
        val themeId: Theme.Id,
        val themeName: String,
        val symbolsInScene: List<SymbolInScene>
    )

    data class SymbolInScene(
        val symbolId: Symbol.Id,
        val symbolName: String,
        val isPinned: Boolean,
        val isUnused: Boolean
    )

    data class AvailableTheme(
        val themeId: Theme.Id,
        val themeName: String,
        val symbolsInScene: List<AvailableSymbol>
    )

    class AvailableSymbol(
        val symbolId: Symbol.Id,
        val symbolName: String
    )

}