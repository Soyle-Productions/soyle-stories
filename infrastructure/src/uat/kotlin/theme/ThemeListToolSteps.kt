package com.soyle.stories.theme

import com.soyle.stories.common.async
import com.soyle.stories.common.editingCell
import com.soyle.stories.di.get
import com.soyle.stories.entities.Theme
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import com.soyle.stories.theme.deleteTheme.DeleteThemeController
import com.soyle.stories.theme.themeList.ThemeList
import com.soyle.stories.theme.themeList.ThemeListItemViewModel
import io.cucumber.java8.En
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.decorators
import tornadofx.item
import tornadofx.onUserSelect
import tornadofx.selectFirst
import java.awt.Label
import java.util.*

class ThemeListToolSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {

        var renameRequest: Pair<Theme.Id, String>? = null

        fun getOpenTool(double: SoyleStoriesTestDouble): ThemeList?
        {
            val scope = ProjectSteps.getProjectScope(double) ?: return null
            return findComponentsInScope<ThemeList>(scope).singleOrNull()?.takeIf {
                it.currentStage?.isShowing == true
            }
        }

        fun isToolOpen(double: SoyleStoriesTestDouble) = getOpenTool(double) != null

        fun toggleOpen(scope: ProjectScope)
        {
            interact {
                async(scope) {
                    scope.get<LayoutViewListener>().toggleToolOpen(com.soyle.stories.layout.config.fixed.ThemeList)
                }
            }
        }

        fun givenToolHasBeenOpened(double: SoyleStoriesTestDouble): ThemeList
        {
            return getOpenTool(double) ?: run {
                val scope = ProjectSteps.givenProjectHasBeenOpened(double)
                toggleOpen(scope)
                getOpenTool(double)!!
            }
        }

        fun givenToolHasBeenClosed(double: SoyleStoriesTestDouble)
        {
            if (getOpenTool(double) != null) {
                val scope = ProjectSteps.givenProjectHasBeenOpened(double)
                toggleOpen(scope)
            }
            assertNull(getOpenTool(double))
        }

        fun getOpenEmptyDisplay(tool: ThemeList): Node?
        {
            return from(tool.root).lookup(".empty-display").query<Node>().takeIf { it.visibleProperty().value }!!
        }

        fun getOpenSymbolContextMenu(double: SoyleStoriesTestDouble): ContextMenu?
        {
            val tool = getOpenTool(double) ?: return null
            return tool.symbolItemContextMenu.takeIf { it.isShowing }
        }

        fun rightClickSymbol(tool: ThemeList)
        {
            val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
            interact {
                treeView.selectionModel.select(treeView.root.children.find { it.children.isNotEmpty() }!!.children.first())
                tool.symbolItemContextMenu.show(treeView, Side.TOP, 0.0, 0.0)
            }
        }

        fun givenSymbolHasBeenRightClicked(double: SoyleStoriesTestDouble): ContextMenu
        {
            return getOpenSymbolContextMenu(double) ?: run {
                val tool = givenToolHasBeenOpened(double)
                rightClickSymbol(tool)
                getOpenSymbolContextMenu(double)!!
            }
        }

    }

    private val validThemeName = "Valid Theme Name ${UUID.randomUUID()}"

    init {
        with(en) {

            Given("the Theme List tool has been opened") {
                givenToolHasBeenOpened(double)
            }
            Given("the Theme List Theme Context Menu has been opened") {
                val tool = givenToolHasBeenOpened(double)
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<*>>()
                interact {
                    from(tool.root).lookup(".tree-view").query<TreeView<*>>().selectFirst()
                    tool.themeItemContextMenu.show(treeView, Side.TOP, 0.0, 0.0)
                }
            }
            Given("a Theme has been selected in the Theme List tool") {
                val tool = givenToolHasBeenOpened(double)
                interact {
                    from(tool.root).lookup(".tree-view").query<TreeView<*>>().selectFirst()
                }
            }
            Given("the Theme List Rename Theme Text Field has been opened") {
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                val item = treeView.root.children.first()
                renameRequest = Theme.Id(UUID.fromString((item.value as ThemeListItemViewModel).themeId)) to ""
                interact {
                    treeView.edit(item)
                }
            }
            Given("a valid Theme name has been entered in the Theme List Rename Theme Field") {
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                val inputBox = treeView.editingCell?.graphic as TextField
                renameRequest = renameRequest!!.copy(second = validThemeName)
                interact {
                    inputBox.text = validThemeName
                }
            }
            Given("an invalid Theme name has been entered in the Theme List Rename Theme Field") {
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                val inputBox = treeView.editingCell?.graphic as TextField
                renameRequest = renameRequest!!.copy(second = "")
                interact {
                    inputBox.text = ""
                }
            }
            Given("a symbol has been selected in the Theme List tool") {
                val tool = givenToolHasBeenOpened(double)
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                val firstThemeItem = treeView.root.children.first()
                val firstSymbolItem = firstThemeItem.children.first()
                interact {
                    treeView.selectionModel.select(firstSymbolItem)
                }
            }
            Given("a symbol has been right-clicked") {
                givenSymbolHasBeenRightClicked(double)
            }

            When("the Theme List tool is opened") {
                val scope = ProjectSteps.getProjectScope(double)!!
                if (getOpenTool(double) != null) {
                    toggleOpen(scope)
                }
                toggleOpen(scope)
            }
            When("the Theme List Create First Theme button is selected") {
                val tool = getOpenTool(double)!!
                val emptyDisplay = getOpenEmptyDisplay(tool)!!
                val button = from(emptyDisplay).lookup(".center-button").queryButton()
                interact { button.fire() }
            }
            When("the Theme List {string} button is selected") { bottomButtonLabel: String ->
                val tool = getOpenTool(double)!!
                val actionBar = from(tool.root).lookup(".action-bar").query<HBox>()
                val button = from(actionBar).lookup(".button").queryAll<Button>().find {
                    it.text == bottomButtonLabel
                }!!
                interact { button.fire() }
            }
            When("a Theme is right-clicked") {
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<*>>()
                interact {
                    tool.themeItemContextMenu.show(treeView, Side.TOP, 0.0, 0.0)
                }
            }
            When("the Theme List Theme Context Menu {string} option is selected") { optionLabel: String ->
                val tool = getOpenTool(double)!!
                val option = tool.themeItemContextMenu.items.find { it.text == optionLabel }!!
                interact { option.fire() }
            }
            When("the Theme rename is cancelled by Pressing Escape") {
                interact {
                    press(KeyCode.ESCAPE).release(KeyCode.ESCAPE)
                }
            }
            When("the Theme rename is cancelled by Clicking Away") {
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                interact {
                    clickOn(treeView)
                }
            }
            When("the Theme rename is committed by Pressing Enter") {
                interact {
                    press(KeyCode.ENTER).release(KeyCode.ENTER)
                }
            }
            When("the Theme rename is committed by Clicking Away") {
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                interact {
                    clickOn(treeView)
                }
            }
            When("a symbol is right-clicked") {
                val tool = getOpenTool(double)!!
                rightClickSymbol(tool)
            }
            When("the Theme List Symbol Context Menu {string} option is selected") { optionLabel: String ->
                val item = getOpenSymbolContextMenu(double)!!.items.find {
                    it.text == optionLabel
                }!!
                interact { item.fire() }
            }

            Then("the Theme List tool should show a special empty message") {
                val tool = getOpenTool(double)!!
                val emptyDisplay = getOpenEmptyDisplay(tool)!!
                assertTrue(from(emptyDisplay).lookup(".label").query<Labeled>().text.isNotBlank())
            }
            Then("the Theme List tool should show all {int} themes") { count: Int ->
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                assertEquals(count, treeView.root.children.size)
            }
            Then("the Theme List Tool should show the new theme") {
                val themes = ThemeSteps.getCreatedThemes(double)
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                assertEquals(themes.size, treeView.root.children.size)
            }
            Then("the Theme List Theme Context Menu should be open") {
                val tool = getOpenTool(double)!!
                assertTrue(tool.themeItemContextMenu.isShowing)
            }
            Then("the Theme List Theme Context Menu should have {string} as an option") { optionLabel: String ->
                val tool = getOpenTool(double)!!
                assertNotNull(tool.themeItemContextMenu.items.find { it.text == optionLabel })
            }
            Then("the Theme List Tool should not show the deleted theme") {
                val themes = ThemeSteps.getCreatedThemes(double)
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                assertEquals(themes.size, treeView.root.children.size)
            }
            Then("the Theme List Rename Theme Text Field should be open") {
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                assertNotNull(treeView.editingCell?.graphic as? TextField)
            }
            Then("the Theme List Rename Theme Text Field should not be open") {
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                assertNull(treeView.editingItem)
            }
            Then("the Theme List Rename Theme Text Field should show an error message") {
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                val inputBox = treeView.editingCell?.graphic as TextField
                assertTrue(inputBox.decorators.isNotEmpty()) { "No decorator on rename input box" }
            }
            Then("the Theme List Tool should show the new symbol") {
                val themes = ThemeSteps.getCreatedThemes(double).associateBy { it.id.uuid.toString() }
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                treeView.root.children.forEach {
                    val theme = themes.getValue((it.value as ThemeListItemViewModel).themeId)
                    assertEquals(theme.symbols.size, it.children.size)
                }
            }
            Then("the Theme List Symbol Context Menu should be open") {
                val tool = getOpenTool(double)!!
                assertTrue(tool.symbolItemContextMenu.isShowing)
            }
            Then("the Theme List Symbol Context Menu should have {string} as an option") { optionLabel: String ->
                val item = getOpenSymbolContextMenu(double)!!.items.find {
                    it.text == optionLabel
                }
                assertNotNull(item)
            }
            Then("the Theme List Tool should not show the deleted symbol") {
                val themes = ThemeSteps.getCreatedThemes(double).associateBy { it.id.uuid.toString() }
                val tool = getOpenTool(double)!!
                val treeView = from(tool.root).lookup(".tree-view").query<TreeView<Any?>>()
                treeView.root.children.forEach {
                    val theme = themes.getValue((it.value as ThemeListItemViewModel).themeId)
                    assertEquals(theme.symbols.size, it.children.size)
                }
            }

        }
    }

}