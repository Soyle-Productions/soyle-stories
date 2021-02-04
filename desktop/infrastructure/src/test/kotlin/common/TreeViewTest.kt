package com.soyle.stories.common

import com.soyle.stories.di.get
import com.soyle.stories.theme.ThemeNameCannotBeBlank
import com.soyle.stories.theme.themeList.SymbolListItemViewModel
import com.soyle.stories.theme.themeList.ThemeListItemViewModel
import com.soyle.stories.theme.themeList.ThemeListModel
import com.soyle.stories.theme.usecases.SymbolNameCannotBeBlank
import com.soyle.stories.theme.usecases.validateSymbolName
import com.soyle.stories.theme.usecases.validateThemeName
import javafx.scene.Scene
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.cellFormat
import tornadofx.treeitem
import tornadofx.vgrow

class TreeViewTest : ApplicationTest() {

    val stage = FxToolkit.registerPrimaryStage()
    lateinit var treeView:TreeView<String?>

    private var parentRendered = false
    private var childRendered = false

    init {
        interact {
            treeView = TreeView<String?>(TreeItem(null)).apply {
                isShowRoot = false
                vgrow = Priority.ALWAYS
                makeEditable { newName, item ->
                    item
                }
                cellFormat {
                    text = treeItem.value ?: ""
                    parentRendered = parentRendered || treeItem.value == "Parent"
                    childRendered = childRendered || treeItem.value == "Child"
                }

                root.treeitem {
                    value = "Parent"
                    treeitem {
                        value = "Child"
                    }
                }
            }
        }
    }

    @Test
    fun `all items rendered`() {
        interact {
            val vbox = VBox(treeView).apply {
                minWidth = 200.0
                minHeight = 100.0
            }
            if (stage.scene != null) stage.scene.root = vbox
            else stage.scene = Scene(vbox)

            stage.show()

            treeView.root.children.first().isExpanded = true
        }

        Thread.sleep(2000)

        assertTrue(parentRendered) { "Parent not rendered" }
        assertTrue(childRendered) { "Child not rendered" }

    }



}