package com.soyle.stories.theme.themeList

import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.common.makeEditable
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialog
import com.soyle.stories.theme.deleteSymbolDialog.DeleteSymbolDialog
import com.soyle.stories.theme.deleteThemeDialog.DeleteThemeDialog
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.Priority
import tornadofx.*

class ThemeList : View() {

    private val viewListener = resolve<ThemeListViewListener>()
    private val model = resolve<ThemeListModel>()

    private var treeview: TreeView<Any?> by singleAssign()

    val themeItemContextMenu = themeItemContextMenu(model, viewListener)
    val symbolItemContextMenu = symbolItemContextMenu(model)

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
            this@ThemeList.treeview = treeview<Any?>(TreeItem(null)) {
                isShowRoot = false
                vgrow = Priority.ALWAYS
                model.selectedItem.bind(selectionModel.selectedItemProperty().select { it.valueProperty() })
                makeEditable({ newName, item ->
                    val nonBlankName = NonBlankString.create(newName)
                    when (item) {
                        is ThemeListItemViewModel -> {
                            if (nonBlankName == null) "Theme name cannot be blank."
                            else null
                        }
                        is SymbolListItemViewModel -> {
                            if (nonBlankName == null) "Symbol name cannot be blank."
                            else null
                        }
                        else -> null
                    }
                }) { newName, item ->
                    val nonBlankName = NonBlankString.create(newName)
                    when (item) {
                        is ThemeListItemViewModel -> {
                            if (nonBlankName != null) viewListener.renameTheme(item.themeId, nonBlankName)
                        }
                        is SymbolListItemViewModel -> {
                            if (nonBlankName != null) viewListener.renameSymbol(item.symbolId, nonBlankName)
                        }
                    }
                    item
                }
                cellFormat {
                    when (it) {
                        is ThemeListItemViewModel -> {
                            text = it.themeName
                            contextMenu = themeItemContextMenu
                        }
                        is SymbolListItemViewModel -> {
                            text = it.symbolName
                            contextMenu = symbolItemContextMenu
                        }
                        else -> throw IllegalArgumentException("Invalid value type")
                    }
                    setOnMouseClicked { it.consume() }
                }
                root.children.bind(model.themes) { themeViewModel ->
                    TreeItem(themeViewModel as Any).apply {
                        themeViewModel.symbols.forEach { symbolViewModel ->
                            treeitem(symbolViewModel)
                        }
                    }
                }
                setOnMouseClicked {
                    selectionModel.clearSelection()
                }
                contextMenu = themeListContextMenu()
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
                button(model.createSymbolButtonLabel) {
                    isDisable = false
                    action {
                        scope.get<CreateSymbolDialog>().show((model.selectedItem.value as? ThemeListItemViewModel)?.themeId, null, currentWindow)
                    }
                    isMnemonicParsing = false
                }
                button(model.deleteButtonLabel) {
                    id = "actionBar_delete"
                    enableWhen { model.selectedItem.isNotNull }
                    action {
                        val item = model.selectedItem.get()
                        when (item) {
                            is ThemeListItemViewModel -> scope.get<DeleteThemeDialog>().show(item.themeId, item.themeName)
                            is SymbolListItemViewModel -> scope.get<DeleteSymbolDialog>().show(item.symbolId, item.symbolName)
                        }
                    }
                    isMnemonicParsing = false
                }
            }
        }
    }

    init {
        model.itemProperty().onChangeOnce {
            owningTab?.tabPane?.requestLayout()
        }
        viewListener.getValidState()
    }

    internal fun editThemeName(themeId: String)
    {
        val treeItem = treeview.root.children.find {
            when (val value = it.value) {
                is ThemeListItemViewModel -> value.themeId == themeId
                else -> false
            }
        } ?: return
        treeview.edit(treeItem)
    }

    internal fun editSymbolName(symbolId: String)
    {
        val treeItem = treeview.root.children.asSequence().flatMap { it.children.asSequence() }.find {
            when (val value = it.value) {
                is SymbolListItemViewModel -> value.symbolId == symbolId
                else -> false
            }
        } ?: return
        treeview.edit(treeItem)
    }

}