package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.layout.openTool.OpenToolController
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

    }

    init {
        with(en) {

            When("the Value Opposition Web Tool is opened") {
                val projectScope = ProjectSteps.getProjectScope(double)!!
                val themeId = ThemeSteps.getCreatedThemes(double).first().id.uuid.toString()
                val controller = projectScope.get<OpenToolController>()
                interact {
                    controller.openValueOppositionWeb(themeId)
                }
            }

            Then("the Value Web Tool should be open") {
                val projectScope = ProjectSteps.getProjectScope(double)!!
                val scope = projectScope.toolScopes.filterIsInstance<ValueOppositionWebsScope>().first()
                findComponentsInScope<ValueOppositionWebs>(scope).first()
            }
            Then("the Value Opposition Web Tool should show a special empty message") {
                val projectScope = ProjectSteps.getProjectScope(double)!!
                val scope = projectScope.toolScopes.filterIsInstance<ValueOppositionWebsScope>().first()
                val tool = scope.get<ValueOppositionWebs>()
                val emptyDisplay = from(tool.root).lookup(".empty-display").query<Node>()
                val valueWebList = from(tool.root).lookup(".value-web-list").query<Parent>()
                assertTrue(emptyDisplay.visibleProperty().get())
                assertFalse(valueWebList.visibleProperty().get())
            }
            Then("the Value Opposition Web Tool should list all {int} value webs") { count: Int ->
                val projectScope = ProjectSteps.getProjectScope(double)!!
                val scope = projectScope.toolScopes.filterIsInstance<ValueOppositionWebsScope>().first()
                val tool = scope.get<ValueOppositionWebs>()
                val emptyDisplay = from(tool.root).lookup(".empty-display").query<Node>()
                assertFalse(emptyDisplay.visibleProperty().get())
                val valueWebList = from(tool.root).lookup(".value-web-list").query<Parent>()
                assertEquals(count, valueWebList.childrenUnmodifiable.size) {
                    "Wrong number of children in value web list.\n" +
                            "Children: ${valueWebList.childrenUnmodifiable}\n" +
                            "Model: ${scope.get<ValueOppositionWebsModel>().valueWebs}"
                }
                assertTrue(valueWebList.visibleProperty().get())
            }
        }
    }

}