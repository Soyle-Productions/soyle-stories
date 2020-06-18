package com.soyle.stories.theme

import com.soyle.stories.common.async
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import com.soyle.stories.theme.themeList.ThemeList
import io.cucumber.java8.En
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Labeled
import javafx.scene.control.TreeView
import javafx.scene.layout.HBox
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest
import java.awt.Label

class ThemeListToolSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {
        fun getOpenTool(double: SoyleStoriesTestDouble): ThemeList?
        {
            val projectScope = ProjectSteps.getProjectScope(double) ?: return null
            return findComponentsInScope<ThemeList>(projectScope).singleOrNull()?.takeIf {
                it.currentStage?.isShowing == true
            }
        }

        fun isToolOpen(double: SoyleStoriesTestDouble): Boolean = getOpenTool(double) != null

        fun toggleOpen(double: SoyleStoriesTestDouble)
        {
            val scope = ProjectSteps.getProjectScope(double)!!
            interact {
                async(scope) {
                    scope.get<LayoutViewListener>().toggleToolOpen(com.soyle.stories.layout.config.fixed.ThemeList)
                }
            }
        }

        fun givenToolHasBeenOpened(double: SoyleStoriesTestDouble): ThemeList
        {
            ProjectSteps.givenProjectHasBeenOpened(double)
            return getOpenTool(double) ?: kotlin.run {
                toggleOpen(double)
                getOpenTool(double)!!
            }
        }

        fun givenToolHasBeenClosed(double: SoyleStoriesTestDouble)
        {
            ProjectSteps.givenProjectHasBeenOpened(double)
            if (isToolOpen(double)) toggleOpen(double)
            assertFalse(isToolOpen(double))
        }

        fun getOpenEmptyDisplay(double: SoyleStoriesTestDouble): Node?
        {
            val tool = getOpenTool(double) ?: return null
            return from(tool.root).lookup(".empty-display").query<Node>().takeIf { it.visibleProperty().value }!!
        }

    }

    init {
        with(en) {

            Given("the Theme List tool has been opened") {
                givenToolHasBeenOpened(double)
            }

            When("the Theme List tool is opened") {
                if (isToolOpen(double)) toggleOpen(double)
                toggleOpen(double)
            }
            When("the Theme List Create First Theme button is selected") {
                val emptyDisplay = getOpenEmptyDisplay(double)!!
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

            Then("the Theme List tool should show a special empty message") {
                val emptyDisplay = getOpenEmptyDisplay(double)!!
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

        }
    }

}