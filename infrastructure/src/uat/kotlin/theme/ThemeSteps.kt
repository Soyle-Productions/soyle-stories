package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeController
import com.soyle.stories.theme.createTheme.CreateThemeController
import com.soyle.stories.theme.deleteTheme.DeleteThemeController
import com.soyle.stories.theme.renameTheme.RenameThemeController
import com.soyle.stories.theme.repositories.ThemeRepository
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest
import java.util.*

class ThemeSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {
        fun getCreatedThemes(double: SoyleStoriesTestDouble): List<Theme>
        {
            val scope = ProjectSteps.getProjectScope(double) ?: return emptyList()
            return runBlocking {
                scope.get<ThemeRepository>().listThemesInProject(Project.Id(scope.projectId))
            }
        }

        fun createTheme(double: SoyleStoriesTestDouble) {
            val scope = ProjectSteps.getProjectScope(double)!!
            val controller = scope.get<CreateThemeController>()
            controller.createTheme("New Theme ${UUID.randomUUID()}") { throw it }
        }

        fun givenANumberOfThemesHaveBeenCreated(count: Int, double: SoyleStoriesTestDouble): List<Theme>
        {
            val currentCount = getCreatedThemes(double).size
            if (currentCount < count) {
                ProjectSteps.givenProjectHasBeenOpened(double)
                repeat(count - currentCount) {
                    createTheme(double)
                }
            }
            val themes = getCreatedThemes(double)
            assertTrue(themes.size >= count)
            return themes
        }

        fun createSymbol(double: SoyleStoriesTestDouble)
        {
            val scope = ProjectSteps.getProjectScope(double)!!
            val controller = scope.get<AddSymbolToThemeController>()
            val theme = getCreatedThemes(double).first()
            interact {
                controller.addSymbolToTheme(theme.id.uuid.toString(), "New Theme ${UUID.randomUUID()}") { throw it }
            }
        }
    }

    init {
        CreateThemeDialogSteps(en, double)
        DeleteThemeDialogSteps(en, double)
        CreateSymbolDialogSteps(en, double)
        ThemeListToolSteps(en, double)

        with(en) {

            Given("{int} Themes have been created") { count: Int ->
                givenANumberOfThemesHaveBeenCreated(count, double)
            }
            Given("a Theme has been created") {
                givenANumberOfThemesHaveBeenCreated(1, double)
            }

            When("a theme is created") {
                createTheme(double)
            }
            When("a theme is deleted") {
                val projectScope = ProjectSteps.getProjectScope(double)!!
                val controller = projectScope.get<DeleteThemeController>()
                val themeToDelete = getCreatedThemes(double).first().id.uuid.toString()
                interact {
                    controller.deleteTheme(themeToDelete)
                }
            }
            When("a symbol is created") {
                createSymbol(double)
            }
            When("a theme is renamed") {
                val theme = getCreatedThemes(double).first()
                val request = theme.id to "New Theme Name ${UUID.randomUUID()}"
                ThemeListToolSteps.renameRequest = request
                val scope = ProjectSteps.getProjectScope(double)!!
                val controller = scope.get<RenameThemeController>()
                interact {
                    controller.renameTheme(theme.id.uuid.toString(), request.second)
                }
            }

            Then("the Theme should be deleted") {
                val themeId = DeleteThemeDialogSteps.requestedThemeId!!
                assertNull(getCreatedThemes(double).find { it.id == themeId })
            }
            Then("the Theme should not be deleted") {
                val themeId = DeleteThemeDialogSteps.requestedThemeId!!
                assertNotNull(getCreatedThemes(double).find { it.id == themeId })
            }
            Then("the Theme should be renamed") {
                val (themeId, name) = ThemeListToolSteps.renameRequest!!
                val theme = getCreatedThemes(double).find { it.id == themeId }!!
                assertEquals(name, theme.name)
            }
            Then("the Theme should not be renamed") {
                val (themeId, name) = ThemeListToolSteps.renameRequest!!
                val theme = getCreatedThemes(double).find { it.id == themeId }!!
                assertNotEquals(name, theme.name)
            }

        }
    }

}