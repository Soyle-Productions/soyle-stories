package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.OppositionValue
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.addOppositionToValueWeb.AddOppositionToValueWebController
import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeController
import com.soyle.stories.theme.addValueWebToTheme.AddValueWebToThemeController
import com.soyle.stories.theme.createTheme.CreateThemeController
import com.soyle.stories.theme.deleteTheme.DeleteThemeController
import com.soyle.stories.theme.removeOppositionFromValueWeb.RemoveOppositionFromValueWebController
import com.soyle.stories.theme.removeSymbolFromTheme.RemoveSymbolFromThemeController
import com.soyle.stories.theme.removeValueWebFromTheme.RemoveValueWebFromThemeController
import com.soyle.stories.theme.renameTheme.RenameThemeController
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.removeOppositionFromValueWeb.RemoveOppositionFromValueWeb
import io.cucumber.java8.En
import io.cucumber.java8.HookBody
import io.cucumber.messages.internal.com.google.protobuf.Value
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

        fun createTheme(double: SoyleStoriesTestDouble, withName: String = "New Theme ${UUID.randomUUID()}") {
            val scope = ProjectSteps.getProjectScope(double)!!
            val controller = scope.get<CreateThemeController>()
            controller.createTheme(withName) { throw it }
            val repo = scope.get<ThemeRepository>()
            runBlocking {
                repo.listThemesInProject(Project.Id(scope.projectId)).find { it.name == withName }!!.let {
                    uatNameToUUID["Theme($withName)"] = it.id.uuid
                }
            }
        }

        fun getTheme(themeId: String, scope: ProjectScope): Theme?
        {
            val repo = scope.get<ThemeRepository>()
            return runBlocking {
                repo.getThemeById(Theme.Id(UUID.fromString(themeId)))
            }
        }

        fun getValueWeb(valueWebId: String, scope: ProjectScope): ValueWeb?
        {
            val repo = scope.get<ThemeRepository>()
            return runBlocking {
                repo.getThemeContainingValueWebWithId(ValueWeb.Id(UUID.fromString(valueWebId)))?.valueWebs
                    ?.find { it.id.uuid.toString() == valueWebId }
            }
        }

        private val uatNameToUUID = mutableMapOf<String, UUID>()

        fun getValueWebWithName(valueWebName: String, scope: ProjectScope): ValueWeb?
        {
            val uuid = uatNameToUUID["ValueWeb($valueWebName)"] ?: return null
            return getValueWeb(uuid.toString(), scope)
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

        fun getThemeWithName(double: SoyleStoriesTestDouble, themeName: String): Theme?
        {
            val scope = ProjectSteps.getProjectScope(double) ?: return null
            val uuid = uatNameToUUID["Theme($themeName)"] ?: return null
            return getTheme(uuid.toString(), scope)
        }

        fun givenAThemeHasBeenCreatedWithTheName(double: SoyleStoriesTestDouble, themeName: String): Theme
        {
            val scope = ProjectSteps.givenProjectHasBeenOpened(double)
            val uuid = uatNameToUUID["Theme($themeName)"] ?: run {
                createTheme(double, themeName)
                uatNameToUUID.getValue("Theme($themeName)")
            }
            return getTheme(uuid.toString(), scope)!!
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

        fun createValueWeb(scope: ProjectScope, themeId: String, withName: String = "New Value Web ${UUID.randomUUID()}")
        {
            val controller = scope.get<AddValueWebToThemeController>()
            interact {
                controller.addValueWebToTheme(themeId, withName) { throw it }
            }
            val repo = scope.get<ThemeRepository>()
            runBlocking {
                repo.getThemeById(Theme.Id(UUID.fromString(themeId)))!!.valueWebs.find { it.name == withName }!!.let {
                    uatNameToUUID["ValueWeb($withName)"] = it.id.uuid
                }
            }
        }

        fun givenANumberOfValueWebsHaveBeenCreated(count: Int, themeId: String, scope: ProjectScope): List<ValueWeb>
        {
            val currentCount = getTheme(themeId, scope)?.valueWebs?.size ?: 0
            if (currentCount < count) {
                repeat(count - currentCount) {
                    createValueWeb(scope, themeId)
                }
            }
            val valueWebs = getTheme(themeId, scope)!!.valueWebs
            assertTrue(valueWebs.size >= count)
            return valueWebs
        }

        fun givenAValueWebHasBeenCreatedWithTheName(double: SoyleStoriesTestDouble, themeId: String, name: String): ValueWeb
        {
            val scope = ProjectSteps.givenProjectHasBeenOpened(double)
            val uuid = uatNameToUUID["ValueWeb($name)"] ?: run {
                createValueWeb(scope, themeId, name)
                uatNameToUUID.getValue("ValueWeb($name)")
            }
            return getValueWeb(uuid.toString(), scope)!!
        }

        fun createOppositionValue(scope: ProjectScope, valueWebId: String)
        {
            val controller = scope.get<AddOppositionToValueWebController>()
            interact {
                controller.addOpposition(valueWebId)
            }
        }

        fun removeOppositionValue(scope: ProjectScope, valueWebId: String)
        {
            /*
            val controller = scope.get<RemoveOppositionFromValueWebController>()
            interact {
                controller.addOpposition(valueWebId)
            }*/
        }

        fun givenANumberOfOppositionsHaveBeenCreated(count: Int, valueWebId: String, scope: ProjectScope): List<OppositionValue>
        {
            val currentCount = getValueWeb(valueWebId, scope)?.oppositions?.size ?: 0
            if (currentCount < count) {
                repeat(count - currentCount) {
                    createOppositionValue(scope, valueWebId)
                }
            }
            val oppositions = getValueWeb(valueWebId, scope)!!.oppositions
            assertTrue(oppositions.size >= count)
            return oppositions
        }

        fun givenTheNumberOfOppositionsHasBeenReducedTo(count: Int, valueWebId: String, scope: ProjectScope)
        {
            val currentCount = getValueWeb(valueWebId, scope)?.oppositions?.size ?: 0
            if (currentCount > count) {
                repeat(count - currentCount) {
                    createOppositionValue(scope, valueWebId)
                }
            }
            val oppositions = getValueWeb(valueWebId, scope)!!.oppositions
            assertTrue(oppositions.size >= count)
        }
    }

    init {
        CreateThemeDialogSteps(en, double)
        DeleteThemeDialogSteps(en, double)
        CreateSymbolDialogSteps(en, double)
        DeleteSymbolDialogSteps(en, double)
        CreateValueWebDialogSteps(en, double)
        DeleteValueWebDialogSteps(en, double)
        ThemeListToolSteps(en, double)
        ValueOppositionWebSteps(en, double)

        with(en) {

            Before(HookBody {
                uatNameToUUID.clear()
            })

            Given("{int} Themes have been created") { count: Int ->
                givenANumberOfThemesHaveBeenCreated(count, double)
            }
            Given("a value web called {string} has been created for the {string} theme") {
                    valueWebName: String, themeName: String ->

                val theme = givenAThemeHasBeenCreatedWithTheName(double, themeName)
                givenAValueWebHasBeenCreatedWithTheName(double, theme.id.uuid.toString(), valueWebName)
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
            Given("all value oppositions have been removed from the {string} value web") { valueWebName: String ->
                val scope = ProjectSteps.givenProjectHasBeenOpened(double)
                val valueWeb = getValueWebWithName(valueWebName, scope)!!
                val controller = scope.get<RemoveOppositionFromValueWebController>()
                interact {
                    valueWeb.oppositions.forEach {
                        controller.removeOpposition(it.id.uuid.toString(), valueWeb.id.uuid.toString())
                    }
                }
            }
            Given("a value opposition has been created for the {string} value web") { valueWebName: String ->
                val scope = ProjectSteps.givenProjectHasBeenOpened(double)
                val valueWeb = getValueWebWithName(valueWebName, scope)!!
                if (valueWeb.oppositions.isEmpty()) {
                    createOppositionValue(scope, valueWeb.id.uuid.toString())
                }
                assertFalse(getValueWebWithName(valueWebName, scope)!!.oppositions.isEmpty())
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
            When("a symbol is deleted") {
                val projectScope = ProjectSteps.getProjectScope(double)!!
                val controller = projectScope.get<RemoveSymbolFromThemeController>()
                val theme = getCreatedThemes(double).first()
                interact {
                    controller.removeSymbolFromTheme(theme.symbols.first().id.uuid.toString())
                }
            }
            When("the {string} value web is deleted") { valueWebName: String ->
                val projectScope = ProjectSteps.getProjectScope(double)!!
                val controller = projectScope.get<RemoveValueWebFromThemeController>()
                val valueWeb = getValueWebWithName(valueWebName, projectScope)!!
                interact {
                    controller.removeValueWeb(valueWeb.id.uuid.toString())
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
            Then("the symbol should be renamed") {
                val (symbolId, name) = ThemeListToolSteps.renameRequest!!
                val symbol = getCreatedThemes(double).asSequence().flatMap { it.symbols.asSequence() }.find { it.id == symbolId }!!
                assertEquals(name, symbol.name)
            }
            Then("the symbol should not be renamed") {
                val (symbolId, name) = ThemeListToolSteps.renameRequest!!
                val symbol = getCreatedThemes(double).asSequence().flatMap { it.symbols.asSequence() }.find { it.id == symbolId }!!
                assertNotEquals(name, symbol.name)
            }
            Then("the value web should be deleted") {
                val valueWebId = DeleteValueWebDialogSteps.requestedValueWebId!!
                assertNull(getCreatedThemes(double).flatMap { it.valueWebs }.find { it.id == valueWebId })
            }
            Then("the value web should not be deleted") {
                val valueWebId = DeleteValueWebDialogSteps.requestedValueWebId!!
                assertNotNull(getCreatedThemes(double).flatMap { it.valueWebs }.find { it.id == valueWebId })
            }
        }
    }

}