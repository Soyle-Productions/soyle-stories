package com.soyle.stories.desktop.view.theme.themeList

import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.theme.themeList.SymbolListItemViewModel
import com.soyle.stories.theme.themeList.ThemeList
import com.soyle.stories.theme.themeList.ThemeListItemViewModel
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import org.testfx.api.FxRobot

class ThemeListDriver(private val themeList: ThemeList) : FxRobot() {

    fun getTree(): TreeView<Any?> = from(themeList.root).lookup(".tree-view").query<TreeView<Any?>>()

    fun getThemeItemOrError(themeId: Theme.Id): TreeItem<Any?> =
        getThemeItem(themeId) ?: error("No item in theme list with id $themeId")

    fun getThemeItem(themeId: Theme.Id): TreeItem<Any?>?
    {
        return getTree().root.children.asSequence().mapNotNull {
            val value = it.value as? ThemeListItemViewModel
            if (value != null && value.themeId == themeId.uuid.toString()) it as TreeItem<Any?>
            else null
        }.firstOrNull()
    }

    fun getThemeItemOrError(themeName: String): TreeItem<Any?> =
        getThemeItem(themeName) ?: error("No item in theme list with name $themeName")

    fun getThemeItem(themeName: String): TreeItem<Any?>?
    {
        return getTree().root.children.asSequence().mapNotNull {
            val value = it.value as? ThemeListItemViewModel
            if (value != null && value.themeName == themeName) it as TreeItem<Any?>
            else null
        }.firstOrNull()
    }

    fun getSymbolItemOrError(themeItem: TreeItem<Any?>, symbolName: String): TreeItem<Any?> =
        getSymbolItem(themeItem, symbolName) ?: error("No symbol in theme list with name $symbolName")

    fun getSymbolItem(themeItem: TreeItem<Any?>, symbolName: String): TreeItem<Any?>?
    {
        return themeItem.children.asSequence().mapNotNull {
            val value = it.value as? SymbolListItemViewModel
            if (value != null && value.symbolName == symbolName) it as TreeItem<Any?>
            else null
        }.firstOrNull()
    }

}