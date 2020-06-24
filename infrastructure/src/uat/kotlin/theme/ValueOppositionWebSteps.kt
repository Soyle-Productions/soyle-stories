package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebs
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsModel
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsScope
import io.cucumber.java8.En
import javafx.scene.Node
import javafx.scene.Parent
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest

class ValueOppositionWebSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {
        fun getOpenTool(double: SoyleStoriesTestDouble): ValueOppositionWebs?
        {
            val projectScope = ProjectSteps.getProjectScope(double) ?: return null
            val scope = projectScope.toolScopes.filterIsInstance<ValueOppositionWebsScope>().firstOrNull() ?: return null
            return findComponentsInScope<ValueOppositionWebs>(scope).firstOrNull()
        }

        fun openTool(projectScope: ProjectScope, themeId: String)
        {
            val controller = projectScope.get<OpenToolController>()
            interact {
                controller.openValueOppositionWeb(themeId)
            }
        }

        fun givenToolHasBeenOpened(double: SoyleStoriesTestDouble): ValueOppositionWebs
        {
            return getOpenTool(double) ?: run {
                openTool(
                    ProjectSteps.givenProjectHasBeenOpened(double),
                    ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first().id.uuid.toString()
                )
                getOpenTool(double)!!
            }
        }
    }

    init {
        with(en) {

            Given("the Value Opposition Web Tool has been opened") {
                givenToolHasBeenOpened(double)
            }

            When("the Value Opposition Web Tool is opened") {
                openTool(
                    ProjectSteps.getProjectScope(double)!!,
                    ThemeSteps.getCreatedThemes(double).first().id.uuid.toString()
                )
            }
            When("the Value Opposition Web Tool Create Value Web button is selected") {
                val tool = getOpenTool(double)!!
                val button = from(tool.root).lookup(".create-value-web-button").queryButton()
                interact {
                    button.fire()
                }
            }

            Then("the Value Web Tool should be open") {
                assertNotNull(getOpenTool(double))
            }
            Then("the Value Opposition Web Tool should show a special empty message") {
                val tool = getOpenTool(double)!!
                val emptyDisplay = from(tool.root).lookup(".empty-display").query<Node>()
                val populated = from(tool.root).lookup(".populated").query<Parent>()
                assertTrue(emptyDisplay.visibleProperty().get())
                assertFalse(populated.visibleProperty().get())
            }
            Then("the Value Opposition Web Tool should list all {int} value webs") { count: Int ->
                val tool = getOpenTool(double)!!
                val emptyDisplay = from(tool.root).lookup(".empty-display").query<Node>()
                assertFalse(emptyDisplay.visibleProperty().get())
                val valueWebList = from(tool.root).lookup(".value-web-list").query<Parent>()
                assertEquals(count, valueWebList.childrenUnmodifiable.size) {
                    "Wrong number of children in value web list.\n" +
                            "Children: ${valueWebList.childrenUnmodifiable}\n" +
                            "Model: ${tool.scope.get<ValueOppositionWebsModel>().valueWebs}"
                }
                val populated = from(tool.root).lookup(".populated").query<Parent>()
                assertTrue(populated.visibleProperty().get())
            }
            Then("the Value Opposition Web Tool should show the created value web") {
                val tool = getOpenTool(double)!!
                val valueWebList = from(tool.root).lookup(".value-web-list").query<Parent>()
                assertEquals(ThemeSteps.getCreatedThemes(double).first().valueWebs.size, valueWebList.childrenUnmodifiable.size) {
                    "Wrong number of children in value web list.\n" +
                            "Children: ${valueWebList.childrenUnmodifiable}\n" +
                            "Model: ${tool.scope.get<ValueOppositionWebsModel>().valueWebs}"
                }
            }
        }
    }

}