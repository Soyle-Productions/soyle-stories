package com.soyle.stories.theme.themeList

import com.soyle.stories.di.get
import com.soyle.stories.theme.deleteSymbolDialog.DeleteSymbolDialog
import com.soyle.stories.theme.deleteThemeDialog.DeleteThemeDialog
import javafx.scene.control.ContextMenu
import tornadofx.action
import tornadofx.item

internal fun ThemeList.symbolItemContextMenu(model: ThemeListModel) = ContextMenu().apply {
    item("Delete") {
        action {
            val item = model.selectedItem.get()
            if (item is SymbolListItemViewModel) {
                scope.get<DeleteSymbolDialog>().show(item.symbolId, item.symbolName)
            }
        }
    }
}