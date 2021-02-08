package com.soyle.stories.theme.themeList

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.theme.renameSymbol.RenameSymbolController
import com.soyle.stories.theme.changeThemeDetails.renameTheme.RenameThemeController
import com.soyle.stories.usecase.theme.listSymbolsByTheme.ListSymbolsByTheme
import java.util.*

class ThemeListController(
    projectId: String,
    private val threadTransformer: ThreadTransformer,
    private val listSymbolsByTheme: ListSymbolsByTheme,
    private val listSymbolsByThemeOutputPort: ListSymbolsByTheme.OutputPort,
    private val openToolController: OpenToolController,
    private val renameThemeController: RenameThemeController,
    private val renameSymbolController: RenameSymbolController
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
        openToolController.openCharacterValueComparison(themeId)
    }

    override fun openCentralConflict(themeId: String) {
        openToolController.openCentralConflict(themeId, null)
    }

    override fun openMoralArgument(themeId: String) {
        openToolController.openMoralArgument(themeId)
    }

    override fun renameTheme(themeId: String, newName: NonBlankString) {
        renameThemeController.renameTheme(themeId, newName)
    }

    override fun renameSymbol(symbolId: String, newName: NonBlankString) {
        renameSymbolController.renameSymbol(symbolId, newName) {}
    }

}