package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.entities.theme.valueWeb.ValueWeb
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.deleteValueWebDialog.DeleteValueWebDialog
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.WriterSteps
import io.cucumber.java8.En
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.uiComponent
import java.util.*

class DeleteValueWebDialogSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {

        var requestedValueWebId: ValueWeb.Id? = null

        fun getOpenDialog(): DeleteValueWebDialog?
        {
            return DeleteSymbolDialogSteps.listWindows().asSequence().mapNotNull {
                if (! it.isShowing) return@mapNotNull null
                it.scene.root.uiComponent<DeleteValueWebDialog>()
            }.firstOrNull()
        }

        fun openDialog(projectScope: ProjectScope, valueWebId: String, valueWebName: String)
        {
            requestedValueWebId = ValueWeb.Id(UUID.fromString(valueWebId))
            interact {
                projectScope.get<DeleteValueWebDialog>().show(valueWebId, valueWebName)
            }
        }

        fun givenDialogHasBeenOpened(projectScope: ProjectScope, valueWebId: String, valueWebName: String): DeleteValueWebDialog
        {
            return getOpenDialog() ?: run {
                openDialog(projectScope, valueWebId, valueWebName)
                getOpenDialog()!!
            }
        }
    }

    init {
        with(en) {
            Given("the Confirm Delete Value Web Dialog has been opened") {
                val projectScope = ProjectSteps.givenProjectHasBeenOpened(double)
                val theme = ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first()
                val valueWeb = ThemeSteps.givenANumberOfValueWebsHaveBeenCreated(1, theme.id.uuid.toString(), projectScope).first()
                givenDialogHasBeenOpened(
                    projectScope,
                    valueWeb.id.uuid.toString(),
                    valueWeb.name
                )
            }
            Given("the Confirm Delete Value Web Dialog do not show again check-box has been checked") {
                val projectScope = ProjectSteps.givenProjectHasBeenOpened(double)
                val theme = ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first()
                val valueWeb = ThemeSteps.givenANumberOfValueWebsHaveBeenCreated(1, theme.id.uuid.toString(), projectScope).first()
                val dialog = givenDialogHasBeenOpened(
                    projectScope,
                    valueWeb.id.uuid.toString(),
                    valueWeb.name
                )
                val checkbox = from(dialog.root).lookup(".check-box").query<CheckBox>()!!
                if (! checkbox.isSelected) interact { checkbox.isSelected = true }
            }
            Given("the Confirm Delete Value Web Dialog has been requested to not be shown") {
                WriterSteps.givenDialogRequestedToBeHidden(double, DialogType.DeleteValueWeb)
            }

            When("the Confirm Delete Value Web Dialog {string} button is selected") { buttonLabel: String ->
                val dialog = getOpenDialog()!!
                val button = from(dialog.root).lookup(".button").queryAll<Button>().find {
                    it.text == buttonLabel
                }!!
                interact { button.fire() }
            }
            When("the Confirm Delete Value Web Dialog is opened") {
                val projectScope = ProjectSteps.getProjectScope(double)!!
                val theme = ThemeSteps.getCreatedThemes(double).first()
                val valueWeb = theme.valueWebs.first()
                openDialog(
                    projectScope,
                    valueWeb.id.uuid.toString(),
                    valueWeb.name
                )
            }

            Then("the Confirm Delete Value Web Dialog should be open") {
                assertNotNull(getOpenDialog())
            }
            Then("the Confirm Delete Value Web Dialog should be closed") {
                assertNull(getOpenDialog())
            }
            Then("the Confirm Delete Value Web Dialog should not be open") {
                assertNull(getOpenDialog())
            }
            Then("the Confirm Delete Value Web Dialog should not open the next time a value web is deleted") {
                val projectScope = ProjectSteps.givenProjectHasBeenOpened(double)
                val theme = ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first()
                val valueWeb = ThemeSteps.givenANumberOfValueWebsHaveBeenCreated(1, theme.id.uuid.toString(), projectScope).first()
                openDialog(
                    projectScope,
                    valueWeb.id.uuid.toString(),
                    valueWeb.name
                )
                assertNull(getOpenDialog())
            }
        }
    }

}