package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialog
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialogModel
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.decorators
import tornadofx.uiComponent
import java.util.*

class CreateValueWebDialogSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {

        val validValueWebName = "Valid Value Web Name ${UUID.randomUUID()}"

        fun getOpenDialog(double: SoyleStoriesTestDouble): CreateValueWebDialog?
        {
            return listWindows().asSequence().mapNotNull {
                if (it?.isShowing != true) return@mapNotNull null
                it.scene.root.uiComponent<CreateValueWebDialog>()
            }.firstOrNull()
        }

        fun openDialog(double: SoyleStoriesTestDouble)
        {
            val scope = ProjectSteps.givenProjectHasBeenOpened(double)
            val theme = ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first()
            interact {
                scope.get<CreateValueWebDialog>().show(theme.id.uuid.toString())
            }
        }

        fun givenDialogHasBeenOpened(double: SoyleStoriesTestDouble): CreateValueWebDialog {
            return getOpenDialog(double) ?: run {
                openDialog(double)
                getOpenDialog(double)!!
            }
        }

    }

    init {
        with(en) {

            Given("the Create Value Web Dialog has been opened") {
                givenDialogHasBeenOpened(double)
            }
            Given("a valid value web name has been entered in the Create Value Web Dialog Name Field") {
                val dialog = givenDialogHasBeenOpened(double)
                val nameField = from(dialog.root).lookup(".text-field").queryTextInputControl()
                if (nameField.text.isBlank()) interact {
                    nameField.text = validValueWebName
                }
            }
            Given("an invalid value web name has been entered in the Create Value Web Dialog Name Field") {
                val dialog = givenDialogHasBeenOpened(double)
                val nameField = from(dialog.root).lookup(".text-field").queryTextInputControl()
                if (nameField.text.isNotBlank()) interact {
                    nameField.text = ""
                }
            }

            Then("the Create Value Web Dialog should be closed") {
                assertNull(getOpenDialog(double))
            }
            Then("the Create Value Web Dialog should show an error message") {
                val dialog = getOpenDialog(double)!!
                val nameField = from(dialog.root).lookup(".text-field").queryTextInputControl()
                assertTrue(nameField.decorators.isNotEmpty()) {
                    "Name field does not have error message.  Model error message: ${dialog.scope.get<CreateValueWebDialogModel>().errorMessage}"
                }
            }
            Then("a new value web should be created with the supplied name") {
                assertNotNull(ThemeSteps.getCreatedThemes(double).first().valueWebs.find {
                    it.name == validValueWebName
                })
            }
            Then("a new value web should not be created") {
                assertNull(ThemeSteps.getCreatedThemes(double).firstOrNull()?.valueWebs?.find {
                    it.name == validValueWebName
                })
            }

        }
    }

}