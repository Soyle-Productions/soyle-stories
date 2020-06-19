package com.soyle.stories.theme.themeList

import com.soyle.stories.di.get
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialog
import javafx.scene.control.ContextMenu
import tornadofx.Scope
import tornadofx.action
import tornadofx.item

internal fun ThemeList.themeListContextMenu() = ContextMenu().apply {
    item("Create New Theme") {
        action {
            scope.get<CreateThemeDialog>().show(currentWindow)
        }
    }
}