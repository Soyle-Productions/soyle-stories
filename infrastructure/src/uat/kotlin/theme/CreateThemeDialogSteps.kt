package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialog
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.SimpleMessageDecorator
import tornadofx.decorators
import tornadofx.uiComponent
import java.util.*

class CreateThemeDialogSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {

        val validThemeName = "Valid Theme Name ${UUID.randomUUID()}"

        fun getOpenDialog(double: SoyleStoriesTestDouble): CreateThemeDialog?
        {
            return listWindows().asSequence()
                .mapNotNull {
                    if (! it.isShowing) return@mapNotNull null
                    it.scene.root.uiComponent<CreateThemeDialog>()
                }.firstOrNull()
        }

        fun isDialogOpen(double: SoyleStoriesTestDouble): Boolean
        {
            return getOpenDialog(double) != null
        }

        fun givenDialogHasBeenOpened(double: SoyleStoriesTestDouble): CreateThemeDialog
        {
            ProjectSteps.givenProjectHasBeenOpened(double)
            val scope = ProjectSteps.getProjectScope(double)!!
            var dialog: CreateThemeDialog? = listWindows().find { it.scene.root.uiComponent<CreateThemeDialog>() != null }
                ?.scene?.root?.uiComponent()
            if (! isDialogOpen(double)) interact {
                dialog = scope.get<CreateThemeDialog>().apply { show() }
            }
            assertTrue(isDialogOpen(double))
            return dialog!!
        }
    }


    init {
        with(en) {
            Given("the Create Theme Dialog has been opened") {
                givenDialogHasBeenOpened(double)
            }

            Given("a valid Theme name has been entered in the Create Theme Dialog Name Field") {
                val dialog = givenDialogHasBeenOpened(double)
                from(dialog.root).lookup(".text-field").queryTextInputControl().text = validThemeName
            }
            Given("an invalid Theme name has been entered in the Create Theme Dialog Name Field") {
                val dialog = givenDialogHasBeenOpened(double)
                from(dialog.root).lookup(".text-field").queryTextInputControl().text = ""
            }

            Then("the Create Theme Dialog should be open") {
                assertTrue(isDialogOpen(double))
            }
            Then("the Create Theme Dialog should be closed") {
                assertFalse(isDialogOpen(double))
            }
            Then("the Create Theme Dialog should show an error message") {
                val dialog = getOpenDialog(double)!!
                assertTrue(from(dialog.root).lookup(".text-field").queryTextInputControl().decorators.isNotEmpty())
            }
            Then("a new Theme should be created with the supplied name") {
                assertNotNull(ThemeSteps.getCreatedThemes(double).find {
                    it.name == validThemeName
                })
            }
            Then("a new Theme should not be created") {
                assertNull(ThemeSteps.getCreatedThemes(double).firstOrNull())
            }
        }
    }

}