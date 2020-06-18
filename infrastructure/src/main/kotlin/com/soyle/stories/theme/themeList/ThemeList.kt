package com.soyle.stories.theme.themeList

import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialog
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.TreeItem
import javafx.scene.layout.Priority
import tornadofx.*

class ThemeList : View() {

    private val viewListener = resolve<ThemeListViewListener>()
    private val model = resolve<ThemeListModel>()

    override val root: Parent = stackpane {
        hgrow = Priority.SOMETIMES
        vgrow = Priority.ALWAYS
        emptyListDisplay(
            model.hasThemes,
            model.emptyMessage,
            model.createFirstThemeButtonLabel
        ) {
            scope.get<CreateThemeDialog>().show(currentWindow)
        }
        vbox {
            visibleWhen { model.hasThemes }
            managedProperty().bind(visibleProperty())
            minWidth = 200.0
            minHeight = 100.0
            vgrow = Priority.ALWAYS
            treeview<Any?>(TreeItem(null)) {
                isShowRoot = false
                vgrow = Priority.ALWAYS
                cellFormat {
                    text = when (it) {
                        is ThemeListItemViewModel -> it.themeName
                        is SymbolListItemViewModel -> it.symbolName
                        else -> throw IllegalArgumentException("Invalid value type")
                    }
                }
                populate { parentItem: TreeItem<Any?> ->
                    when (val itemValue = parentItem.value) {
                        null -> model.themes
                        is ThemeListItemViewModel -> itemValue.symbols
                        else -> emptyList()
                    }
                }
            }
            hbox(alignment = Pos.CENTER, spacing = 10.0) {
                isFillHeight = false
                padding = Insets(5.0, 0.0, 5.0, 0.0)
                addClass("action-bar")
                button(model.createThemeButtonLabel) {
                    id = "actionBar_create"
                    isDisable = false
                    action {
                        scope.get<CreateThemeDialog>().show(currentWindow)
                    }
                    isMnemonicParsing = false
                }
            }
        }
    }

    init {
        model.itemProperty.onChangeOnce {
            owningTab?.tabPane?.requestLayout()
        }
        viewListener.getValidState()
    }

}