package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.entities.Theme
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.deleteThemeDialog.DeleteThemeDialog
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.WriterSteps
import io.cucumber.java8.En
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.uiComponent

class DeleteThemeDialogSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {

        var requestedThemeId: Theme.Id? = null
            private set

        fun getOpenDialog(): DeleteThemeDialog?
        {
            return listWindows().mapNotNull {
                if (! it.isShowing) return@mapNotNull null
                it.scene.root.uiComponent<DeleteThemeDialog>()
            }.firstOrNull()
        }
        fun isDialogOpen(): Boolean = getOpenDialog() != null
        fun setDialogOpen(projectScope: ProjectScope, theme: Theme)
        {
            requestedThemeId = theme.id
            interact {
                projectScope.get<DeleteThemeDialog>().show(theme.id.uuid.toString(), theme.name)
            }
        }
        fun givenDialogHasBeenOpened(projectScope: ProjectScope, theme: Theme): DeleteThemeDialog
        {
            return getOpenDialog() ?: run {
                setDialogOpen(projectScope, theme)
                getOpenDialog()
            }!!
        }
    }

    init {
        with(en) {

            Given("the Confirm Delete Theme Dialog has been opened") {
                givenDialogHasBeenOpened(
                    ProjectSteps.getProjectScope(double)!!,
                    ThemeSteps.getCreatedThemes(double).first()
                )
            }
            Given("the Confirm Delete Theme Dialog do not show again check-box has been checked") {
                val dialog = givenDialogHasBeenOpened(
                    ProjectSteps.getProjectScope(double)!!,
                    ThemeSteps.getCreatedThemes(double).first()
                )
                val checkbox = from(dialog.root).lookup(".check-box").query<CheckBox>()!!
                if (! checkbox.isSelected) interact { checkbox.isSelected = true }
            }
            Given("the Confirm Delete Theme Dialog has been requested to not be shown") {
                WriterSteps.givenDialogRequestedToBeHidden(double, DialogType.DeleteTheme)
            }

            When("the Confirm Delete Theme Dialog {string} button is selected") { buttonLabel: String ->
                val dialog = getOpenDialog()!!
                val button = from(dialog.root).lookup(".button").queryAll<Button>().find {
                    it.text == buttonLabel
                }!!
                interact { button.fire() }
            }
            When("the Confirm Delete Theme Dialog is opened") {
                setDialogOpen(
                    ProjectSteps.getProjectScope(double)!!,
                    ThemeSteps.getCreatedThemes(double).first()
                )
            }

            Then("the Confirm Delete Theme Dialog should be open") {
                assertTrue(isDialogOpen())
            }
            Then("the Confirm Delete Theme Dialog should be closed") {
                assertNull(getOpenDialog())
            }
            Then("the Confirm Delete Theme Dialog should not be open") {
                assertNull(getOpenDialog())
            }
            Then("the Confirm Delete Theme Dialog should not open the next time a Theme is deleted") {
                val theme = ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first()
                setDialogOpen(
                    ProjectSteps.getProjectScope(double)!!, theme
                )
                assertNull(getOpenDialog())
            }

        }
    }

}