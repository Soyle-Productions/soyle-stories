package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.entities.Theme
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import io.cucumber.java8.En
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.MenuButton
import javafx.scene.control.TextInputControl
import javafx.scene.layout.HBox
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.Field
import tornadofx.decorators
import tornadofx.uiComponent
import java.util.*

class CreateSymbolDialogSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {

        val validSymbolName = "Valid Symbol Name ${UUID.randomUUID()}"

        fun getOpenDialog(): CreateSymbolDialog?
        {
            return listWindows().asSequence()
                .mapNotNull {
                    if (! it.isShowing) return@mapNotNull null
                    it.scene.root.uiComponent<CreateSymbolDialog>()
                }
                .firstOrNull()
        }

        fun isDialogOpen() = getOpenDialog() != null

        fun openDialog(double: SoyleStoriesTestDouble, themeId: String? = null)
        {
            val scope = ProjectSteps.getProjectScope(double)!!
            interact {
                scope.get<CreateSymbolDialog>().show(themeId)
            }
        }

        fun givenDialogHasBeenOpened(double: SoyleStoriesTestDouble): CreateSymbolDialog
        {
            return getOpenDialog() ?: run {
                openDialog(double)
                getOpenDialog()!!
            }
        }

        fun givenDialogHasBeenOpenedWithTheme(double: SoyleStoriesTestDouble): CreateSymbolDialog
        {
            ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double)
            val themeId = ThemeSteps.getCreatedThemes(double).first().id.uuid.toString()
            return getOpenDialog() ?: run {
                openDialog(double, themeId)
                getOpenDialog()!!
            }
        }

        fun getThemeDropdown(dialog: CreateSymbolDialog): MenuButton
        {
            return from(dialog.root).lookup(".theme-link .menu-button").query()
        }

        fun getThemeNameField(dialog: CreateSymbolDialog): TextInputControl
        {
            return from(dialog.root).lookup(".theme-link .text-field").queryTextInputControl()
        }

    }

    private var selectedThemeId: String? = null

    init {
        with(en) {
            Given("the Create Symbol Dialog has been opened") {
                givenDialogHasBeenOpenedWithTheme(double)
            }
            Given("a valid Symbol Name has been entered into the Create Symbol Dialog Name field") {
                val dialog = givenDialogHasBeenOpened(double)
                val nameField = from(dialog.root).lookup(".text-field").queryTextInputControl()
                interact {
                    nameField.text = validSymbolName
                }
            }
            Given("an invalid Symbol Name has been entered into the Create Symbol Dialog Name field") {
                val dialog = givenDialogHasBeenOpenedWithTheme(double)
                val nameField = from(dialog.root).lookup(".text-field").queryTextInputControl()
                interact {
                    nameField.text = ""
                }
            }
            Given("the Create Symbol Dialog has been opened without a Theme") {
                givenDialogHasBeenOpened(double)
            }
            Given("a valid Theme Name has been entered into the Create Symbol Dialog Theme field") {
                val dialog = getOpenDialog()!!
                val textfield = getThemeNameField(dialog)
                interact {
                    textfield.text = CreateThemeDialogSteps.validThemeName
                }
            }
            Given("an invalid Theme Name has been entered into the Create Symbol Dialog Theme field") {
                val dialog = getOpenDialog()!!
                val textfield = getThemeNameField(dialog)
                interact {
                    textfield.text = ""
                }
            }
            Given("a Theme has been selected in the Create Symbol Dialog Theme field") {
                val dialog = getOpenDialog()!!
                val dropdown = getThemeDropdown(dialog)
                val firstItem = dropdown.items.first()
                selectedThemeId = firstItem.id
                interact {
                    firstItem.fire()
                }
            }

            When("the Create Symbol Dialog is opened with a Theme") {
                val themeId = ThemeSteps.getCreatedThemes(double).first().id.uuid.toString()
                openDialog(double, themeId)
            }
            When("the Create Symbol Dialog is opened without a Theme") {
                openDialog(double)
            }

            Then("the Create Symbol Dialog Theme field should not be visible") {
                val dialog = getOpenDialog()!!
                from(dialog.root).lookup(".theme-link").query<Node>().visibleProperty().get()
                    .let(::assertFalse)
            }
            Then("the Create Symbol Dialog Theme field should be visible") {
                val dialog = getOpenDialog()!!
                from(dialog.root).lookup(".theme-link").query<Node>().visibleProperty().get()
                    .let(::assertTrue)
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
            Then("the Create Symbol Dialog should show an error message for the Symbol Name field") {
                val dialog = getOpenDialog()!!
                assertTrue(from(dialog.root).lookup(".text-field").queryTextInputControl().decorators.isNotEmpty())
            }
            Then("the Create Symbol Dialog should show an error message for the Theme Name field") {
                val dialog = getOpenDialog()!!
                assertTrue(getThemeNameField(dialog).decorators.isNotEmpty())
            }
            Then("a new Symbol should not be created") {
                val theme = ThemeSteps.getCreatedThemes(double).firstOrNull()
                assertNull(theme?.symbols?.find {
                    it.name == validSymbolName
                })
            }
            Then("the Create Symbol Dialog Theme field should be a dropdown") {
                val dialog = getOpenDialog()!!
                val dropdown = getThemeDropdown(dialog)
                val textfield = getThemeNameField(dialog)
                dropdown.visibleProperty().get().let(::assertTrue)
                textfield.visibleProperty().get().let(::assertFalse)
            }
            Then("the Create Symbol Dialog Theme field should be a text field") {
                val dialog = getOpenDialog()!!
                val dropdown = getThemeDropdown(dialog)
                val textfield = getThemeNameField(dialog)
                dropdown.visibleProperty().get().let(::assertFalse)
                textfield.visibleProperty().get().let(::assertTrue)
            }
            Then("the Create Symbol Dialog Theme field label should say {string}") { expectedLabel: String ->
                val dialog = getOpenDialog()!!
                val field = from(dialog.root).lookup(".theme-link").query<Parent>().childrenUnmodifiable
                    .asSequence()
                    .filter { it is Field && it.visibleProperty().get() }
                    .firstOrNull() as? Field
                assertEquals(expectedLabel, field!!.text)
            }
            Then("the Create Symbol Dialog Theme toggle button should say {string}") { expectedLabel: String ->
                val dialog = getOpenDialog()!!
                val button = from(dialog.root).lookup(".theme-link > .button").query<Button>()
                assertEquals(expectedLabel, button.text)
            }
            Then("the Create Symbol Dialog Theme toggle button should be disabled") {
                val dialog = getOpenDialog()!!
                val button = from(dialog.root).lookup(".theme-link > .button").query<Button>()
                button.disableProperty().get().let(::assertTrue)
            }
            Then("a new Symbol should be created with the supplied name in the new Theme") {
                ThemeSteps.getCreatedThemes(double).find {
                    it.name == CreateThemeDialogSteps.validThemeName
                }!!.symbols.find {
                    it.name == validSymbolName
                }!!
            }
            Then("the Create Symbol Dialog Theme field should list all themes") {
                val dialog = getOpenDialog()!!
                val dropdown = getThemeDropdown(dialog)
                assertEquals(ThemeSteps.getCreatedThemes(double).size, dropdown.items.size)
            }
            Then("the new Theme should be listed in the Create Symbol Dialog Theme list") {
                val dialog = getOpenDialog()!!
                val dropdown = getThemeDropdown(dialog)
                assertEquals(ThemeSteps.getCreatedThemes(double).size, dropdown.items.size)
            }
            Then("the deleted Theme should not be listed in the Create Symbol Dialog Theme list") {
                val dialog = getOpenDialog()!!
                val dropdown = getThemeDropdown(dialog)
                assertEquals(ThemeSteps.getCreatedThemes(double).size, dropdown.items.size)
            }
            Then("a new Symbol should be created with the supplied name in the selected Theme") {
                val selectedTheme = ThemeSteps.getCreatedThemes(double).find {
                    it.id.uuid.toString() == selectedThemeId
                }!!
                assertNotNull(selectedTheme.symbols.find {
                    it.name == validSymbolName
                })
            }
            Then("the renamed Theme should show the new name in the Create Symbol Dialog Theme list") {
                val dialog = getOpenDialog()!!
                val dropdown = getThemeDropdown(dialog)
                val renameRequest = ThemeListToolSteps.renameRequest!!
                val themeItem = dropdown.items.find {
                    it.id == (renameRequest.first as Theme.Id).uuid.toString()
                }!!
                assertEquals(renameRequest.second, themeItem.text)
            }
        }
    }

}