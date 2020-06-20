package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import io.cucumber.java8.En
import javafx.scene.Node
import javafx.scene.layout.HBox
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.decorators
import tornadofx.uiComponent
import java.util.*

class CreateSymbolDialogSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {

        fun getOpenDialog(): CreateSymbolDialog?
        {
            return listWindows().asSequence()
                .mapNotNull {
                    if (! it.isShowing) return@mapNotNull null
                    it.scene.root.uiComponent<CreateSymbolDialog>()
                }
                .firstOrNull()
        }

        fun openDialog(double: SoyleStoriesTestDouble)
        {
            val scope = ProjectSteps.getProjectScope(double)!!
            val themeId = ThemeSteps.getCreatedThemes(double).first().id.uuid.toString()
            interact {
                scope.get<CreateSymbolDialog>().show(themeId)
            }
        }

        fun givenDialogHasBeenOpened(double: SoyleStoriesTestDouble): CreateSymbolDialog
        {
            ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double)
            // the above statement won't be needed once we allow for opening the dialog without a theme
            return getOpenDialog() ?: run {
                openDialog(double)
                getOpenDialog()!!
            }
        }

    }

    private val validSymbolName = "Valid Symbol Name ${UUID.randomUUID()}"

    init {
        with(en) {
            Given("the Create Symbol Dialog has been opened") {
                givenDialogHasBeenOpened(double)
            }
            Given("a valid Symbol Name has been entered into the Create Symbol Dialog Name field") {
                val dialog = givenDialogHasBeenOpened(double)
                val nameField = from(dialog.root).lookup(".text-field").queryTextInputControl()
                interact {
                    nameField.text = validSymbolName
                }
            }
            Given("an invalid Symbol Name has been entered into the Create Symbol Dialog Name field") {
                val dialog = givenDialogHasBeenOpened(double)
                val nameField = from(dialog.root).lookup(".text-field").queryTextInputControl()
                interact {
                    nameField.text = ""
                }
            }

            When("the Create Symbol Dialog is opened with a Theme") {
                openDialog(double)
            }

            Then("the Create Symbol Dialog Theme field should not be visible") {
                val dialog = getOpenDialog()!!
                from(dialog.root).lookup(".theme-link").query<Node>().visibleProperty().get()
                    .let(::assertFalse)
            }
            Then("the Create Symbol Dialog should be closed") {
                assertNull(getOpenDialog())
            }
            Then("a new Symbol should be created with the supplied name") {
                val theme = ThemeSteps.getCreatedThemes(double).first()
                assertNotNull(theme.symbols.find {
                    it.name == validSymbolName
                })
            }
            Then("the Create Symbol Dialog should not be closed") {
                assertNotNull(getOpenDialog())
            }
            Then("the Create Symbol Dialog should be open") {
                assertNotNull(getOpenDialog())
            }
            Then("the Create Symbol Dialog should show an error message") {
                val dialog = getOpenDialog()!!
                assertTrue(from(dialog.root).lookup(".text-field").queryTextInputControl().decorators.isNotEmpty())
            }
            Then("a new Symbol should not be created") {
                val theme = ThemeSteps.getCreatedThemes(double).first()
                assertNull(theme.symbols.find {
                    it.name == validSymbolName
                })
            }
        }
    }

}