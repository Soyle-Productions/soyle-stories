package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.deleteSymbolDialog.DeleteSymbolDialog
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.WriterSteps
import io.cucumber.java8.En
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import org.junit.jupiter.api.Assertions.assertNull
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.uiComponent
import java.util.*

class DeleteSymbolDialogSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {

        var requestedSymbolId: Symbol.Id? = null

        fun getOpenDialog(): DeleteSymbolDialog?
        {
            return listWindows().asSequence().mapNotNull {
                if (! it.isShowing) return@mapNotNull null
                it.scene.root.uiComponent<DeleteSymbolDialog>()
            }.firstOrNull()
        }

        fun openDialog(projectScope: ProjectScope, symbolId: String, symbolName: String)
        {
            requestedSymbolId = Symbol.Id(UUID.fromString(symbolId))
            interact {
                projectScope.get<DeleteSymbolDialog>().show(symbolId, symbolName)
            }
        }

        fun givenDialogHasBeenOpened(projectScope: ProjectScope, symbolId: String, symbolName: String): DeleteSymbolDialog
        {
            return getOpenDialog() ?: run {
                openDialog(projectScope, symbolId, symbolName)
                getOpenDialog()!!
            }
        }
    }

    init {
        with(en) {
            Given("the Confirm Delete Symbol Dialog has been opened") {
                val projectScope = ProjectSteps.givenProjectHasBeenOpened(double)
                val theme = ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first()
                val symbol = ThemeSteps.givenANumberOfSymbolsHaveBeenCreated(1, theme.id.uuid.toString(), projectScope).first()
                givenDialogHasBeenOpened(
                    projectScope,
                    symbol.id.uuid.toString(),
                    symbol.name
                )
            }
            Given("the Confirm Delete Symbol Dialog do not show again check-box has been checked") {
                val projectScope = ProjectSteps.givenProjectHasBeenOpened(double)
                val theme = ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first()
                val symbol = ThemeSteps.givenANumberOfSymbolsHaveBeenCreated(1, theme.id.uuid.toString(), projectScope).first()
                val dialog = givenDialogHasBeenOpened(
                    projectScope,
                    symbol.id.uuid.toString(),
                    symbol.name
                )
                val checkbox = from(dialog.root).lookup(".check-box").query<CheckBox>()!!
                if (! checkbox.isSelected) interact { checkbox.isSelected = true }
            }
            Given("the Confirm Delete Symbol Dialog has been requested to not be shown") {
                WriterSteps.givenDialogRequestedToBeHidden(double, DialogType.DeleteSymbol)
            }

            When("the Confirm Delete Symbol Dialog {string} button is selected") { buttonLabel: String ->
                val dialog = getOpenDialog()!!
                val button = from(dialog.root).lookup(".button").queryAll<Button>().find {
                    it.text == buttonLabel
                }!!
                interact { button.fire() }
            }
            When("the Confirm Delete Symbol Dialog is opened") {
                val projectScope = ProjectSteps.getProjectScope(double)!!
                val theme = ThemeSteps.getCreatedThemes(double).first()
                val symbol = theme.symbols.first()
                openDialog(
                    projectScope,
                    symbol.id.uuid.toString(),
                    symbol.name
                )
            }

            Then("the Confirm Delete Symbol Dialog should be closed") {
                assertNull(getOpenDialog())
            }
            Then("the Confirm Delete Symbol Dialog should not be open") {
                assertNull(getOpenDialog())
            }
            Then("the Confirm Delete Symbol Dialog should not open the next time a symbol is deleted") {
                val projectScope = ProjectSteps.givenProjectHasBeenOpened(double)
                val theme = ThemeSteps.givenANumberOfThemesHaveBeenCreated(1, double).first()
                val symbol = ThemeSteps.givenANumberOfSymbolsHaveBeenCreated(1, theme.id.uuid.toString(), projectScope).first()
                openDialog(
                    projectScope,
                    symbol.id.uuid.toString(),
                    symbol.name
                )
                assertNull(getOpenDialog())
            }
        }
    }

}