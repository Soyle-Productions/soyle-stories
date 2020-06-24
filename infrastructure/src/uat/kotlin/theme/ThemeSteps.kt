package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeController
import com.soyle.stories.theme.addValueWebToTheme.AddValueWebToThemeController
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

        fun getTheme(themeId: String, scope: ProjectScope): Theme?
        {
            val repo = scope.get<ThemeRepository>()
            return runBlocking {
                repo.getThemeById(Theme.Id(UUID.fromString(themeId)))
            }
        }

        fun givenANumberOfThemesHaveBeenCreated(count: Int, double: SoyleStoriesTestDouble): List<Theme>
        {
            val currentCount = getCreatedThemes(double).size
            if (currentCount < count) {
                ProjectSteps.checkProjectHasBeenOpened(double)
                repeat(count - currentCount) {
                    createTheme(double)
                }
            }
            val themes = getCreatedThemes(double)
            assertTrue(themes.size >= count)
            return themes
        }

        fun createSymbol(scope: ProjectScope, themeId: String)
        {
            val controller = scope.get<AddSymbolToThemeController>()
            interact {
                controller.addSymbolToTheme(themeId, "New Symbol ${UUID.randomUUID()}") { throw it }
            }
        }

        fun givenANumberOfSymbolsHaveBeenCreated(count: Int, themeId: String, scope: ProjectScope): List<Symbol>
        {
            val currentCount = getTheme(themeId, scope)?.symbols?.size ?: 0
            if (currentCount < count) {
                repeat(count - currentCount) {
                    createSymbol(scope, themeId)
                }
            }
            val symbols = getTheme(themeId, scope)!!.symbols
            assertTrue(symbols.size >= count)
            return symbols
        }
    }

    init {
        CreateThemeDialogSteps(en, double)
        DeleteThemeDialogSteps(en, double)
        CreateSymbolDialogSteps(en, double)
        DeleteSymbolDialogSteps(en, double)
        CreateValueWebDialogSteps(en, double)
        ThemeListToolSteps(en, double)
        ValueOppositionWebSteps(en, double)

        with(en) {

            Given("{int} Themes have been created") { count: Int ->
                givenANumberOfThemesHaveBeenCreated(count, double)
            }
            Given("a Theme has been created") {
                givenANumberOfThemesHaveBeenCreated(1, double)
            }
            Given("{int} value webs have been created in the theme open in the Value Opposition Web Tool") { count: Int ->
                val theme = givenANumberOfThemesHaveBeenCreated(1, double).first()
                val currentCount = theme.valueWebs.size
                if (currentCount < count) {
                    val updatedTheme = (currentCount until count).fold(theme) { currentTheme, it ->
                        currentTheme.withValueWeb(ValueWeb(""))
                    }
                    val scope = ProjectSteps.getProjectScope(double)!!
                    val repo = scope.get<ThemeRepository>()
                    runBlocking {
                        repo.updateTheme(updatedTheme)
                    }
                }
                assertTrue(count <= givenANumberOfThemesHaveBeenCreated(1, double).first().valueWebs.size)
            }
            Given("a symbol has been created") {
                val projectScope = ProjectSteps.givenProjectHasBeenOpened(double)
                val theme = givenANumberOfThemesHaveBeenCreated(1, double).first()
                givenANumberOfSymbolsHaveBeenCreated(1, theme.id.uuid.toString(), projectScope)
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
                val projectScope = ProjectSteps.getProjectScope(double)!!
                val theme = getCreatedThemes(double).first()
                createSymbol(projectScope, theme.id.uuid.toString())
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
            When("a value web is created for the theme open in the Value Opposition Web Tool") {
                val scope = ProjectSteps.getProjectScope(double)!!
                val controller = scope.get<AddValueWebToThemeController>()
                val theme = getCreatedThemes(double).first()
                interact {
                    controller.addValueWebToTheme(theme.id.uuid.toString(), "New Value Web") { throw it }
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
            Then("the symbol should be deleted") {
                val symbolId = DeleteSymbolDialogSteps.requestedSymbolId!!
                assertNull(getCreatedThemes(double).flatMap { it.symbols }.find { it.id == symbolId })
            }
            Then("the symbol should not be deleted") {
                val symbolId = DeleteSymbolDialogSteps.requestedSymbolId!!
                assertNotNull(getCreatedThemes(double).flatMap { it.symbols }.find { it.id == symbolId })
            }

        }
    }

}