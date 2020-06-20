package com.soyle.stories.theme.themeList

import com.soyle.stories.di.get
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import com.soyle.stories.theme.deleteThemeDialog.DeleteThemeDialog
import javafx.scene.control.ContextMenu
import tornadofx.action
import tornadofx.bind
import tornadofx.item

internal fun ThemeList.themeItemContextMenu(model: ThemeListModel, viewListener: ThemeListViewListener) = ContextMenu().apply {
    item("Compare Characters") {
        action {
            val selectedItem = model.selectedItem.value
            if (selectedItem is ThemeListItemViewModel) {
                viewListener.openCharacterComparison(selectedItem.themeId)
            }
        }
    }
    item("Create Symbol") {
        action {
            val selectedItem = model.selectedItem.value
            if (selectedItem is ThemeListItemViewModel) {
                scope.get<CreateSymbolDialog>().show(selectedItem.themeId, currentWindow)
            }
        }
    }
    item("Rename") {
        action {
            val selectedItem = model.selectedItem.value
            if (selectedItem is ThemeListItemViewModel) {
                editThemeName(selectedItem.themeId)
            }
        }
    }
    item("Delete") {
        action {
            val item = model.selectedItem.get()
            if (item is ThemeListItemViewModel) {
                scope.get<DeleteThemeDialog>().show(item.themeId, item.themeName)
            }
        }
    }
}