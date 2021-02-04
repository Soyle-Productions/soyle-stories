package com.soyle.stories.theme.createSymbolDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeController
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionController
import com.soyle.stories.theme.createTheme.CreateThemeController
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import com.soyle.stories.theme.usecases.listThemes.ListThemes
import java.util.*

class CreateSymbolDialogController(
    projectId: String,
    private val threadTransformer: ThreadTransformer,
    private val presenter: CreateSymbolDialogPresenter,
    private val listThemes: ListThemes,
    private val addSymbolToThemeController: AddSymbolToThemeController,
    private val createThemeController: CreateThemeController,
    private val addSymbolicItemToOppositionController: AddSymbolicItemToOppositionController
) : CreateSymbolDialogViewListener {

    private val projectId = UUID.fromString(projectId)

    override fun getValidState() {
        threadTransformer.async {
            listThemes.invoke(
                projectId,
                presenter
            )
        }
    }

    override fun createThemeAndSymbol(themeName: String, symbolName: String) {
        createThemeController.createThemeAndFirstSymbol(themeName, symbolName, presenter::presentError)
    }

    override fun createSymbol(themeId: String, name: String) {
        addSymbolToThemeController.addSymbolToTheme(themeId, name, presenter::presentError)
    }

    override fun linkToOpposition(symbolId: String, oppositionId: String) {
        addSymbolicItemToOppositionController.addSymbolToOpposition(oppositionId, symbolId)
    }

}