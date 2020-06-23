package com.soyle.stories.theme.themeList

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.theme.renameTheme.RenameThemeController
import com.soyle.stories.theme.usecases.listSymbolsByTheme.ListSymbolsByTheme
import java.util.*

class ThemeListController(
    projectId: String,
    private val threadTransformer: ThreadTransformer,
    private val listSymbolsByTheme: ListSymbolsByTheme,
    private val listSymbolsByThemeOutputPort: ListSymbolsByTheme.OutputPort,
    private val openToolController: OpenToolController,
    private val renameThemeController: RenameThemeController
) : ThemeListViewListener {

    private val projectId = UUID.fromString(projectId)

    override fun getValidState() {
        threadTransformer.async {
            listSymbolsByTheme.invoke(projectId, listSymbolsByThemeOutputPort)
        }
    }

    override fun openValueWeb(themeId: String) {
        openToolController.openValueOppositionWeb(themeId)
    }

    override fun openCharacterComparison(themeId: String) {
        openToolController.openCharacterComparison(themeId, null)
    }

    override fun renameTheme(themeId: String, newName: String) {
        renameThemeController.renameTheme(themeId, newName)
    }

}