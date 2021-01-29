package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.scene.items.SceneItemViewModel

data class SymbolsInSceneViewModel(
    val targetScene: SceneItemViewModel?,
    val themesInScene: List<ThemeInScene>
) {

    data class ThemeInScene(
        val themeId: Theme.Id,
        val themeName: String,
        val symbolsInScene: List<SymbolInScene>
    )

    class SymbolInScene(
        val symbolId: Symbol.Id,
        val symbolName: String
    )

}