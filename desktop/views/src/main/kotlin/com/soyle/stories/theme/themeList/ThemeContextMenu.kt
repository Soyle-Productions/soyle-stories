package com.soyle.stories.theme.themeList

import com.soyle.stories.di.get
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import com.soyle.stories.theme.deleteThemeDialog.DeleteThemeDialog
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import tornadofx.action
import tornadofx.bind
import tornadofx.item

internal fun ThemeList.themeItemContextMenu(model: ThemeListModel, viewListener: ThemeListViewListener) = ContextMenu().apply {
    fun MenuItem.actionForSelectedThemeItem(action: (ThemeListItemViewModel) -> Unit) {
        action {
            val selectedItem = model.selectedItem.value
            if (selectedItem is ThemeListItemViewModel) {
                action(selectedItem)
            }
        }
    }
    item("Compare Values") {
        actionForSelectedThemeItem {
            viewListener.openValueWeb(it.themeId)
        }
    }
    item("Examine Central Conflict") {
        id = "examine_conflict"
        actionForSelectedThemeItem {
            viewListener.openCentralConflict(it.themeId)
        }
    }
    item("Outline the Moral Argument") {
        actionForSelectedThemeItem {
            viewListener.openMoralArgument(it.themeId)
        }
    }
    item("Create Symbol") {
        actionForSelectedThemeItem {
            scope.get<CreateSymbolDialog>().show(it.themeId, null, currentWindow)
        }
    }
    item("Rename") {
        actionForSelectedThemeItem {
            editThemeName(it.themeId)
        }
    }
    item("Delete") {
        actionForSelectedThemeItem {
            scope.get<DeleteThemeDialog>().show(it.themeId, it.themeName)
        }
    }
}