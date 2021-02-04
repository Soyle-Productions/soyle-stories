package com.soyle.stories.theme

import com.soyle.stories.common.SoyleStoriesIntegrationDouble
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.get
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogViewListener
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialogViewListener
import com.soyle.stories.theme.deleteValueWebDialog.DeleteValueWebDialogScope
import com.soyle.stories.theme.deleteValueWebDialog.DeleteValueWebDialogViewListener
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.themeList.ThemeListViewListener
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsModel
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsScope
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsViewListener
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ValueOppositionWebToolIntTest {

    private val projectScope: ProjectScope
    private val theme: Theme

    init {
        // given a project has been opened
        val applicationScope = SoyleStoriesIntegrationDouble().scope
        val projectViewListener = applicationScope.get<ProjectListViewListener>()
        val threadTransformer = applicationScope.get<ThreadTransformer>()
        threadTransformer.async {
            projectViewListener.startApplicationWithParameters(listOf())
        }
        threadTransformer.async {
            projectViewListener.startNewProject("C:\\Users", "Untitled")
        }
        projectScope = applicationScope.projectScopes.first()
        projectScope.get<LayoutViewListener>().loadLayoutForProject(projectScope.projectId)
        // given a theme has been created
        projectScope.get<CreateThemeDialogViewListener>().createTheme("Growing Up")
        theme = runBlocking {
            projectScope.get<ThemeRepository>().listThemesInProject(Project.Id(projectScope.projectId)).first()
        }
    }

    private fun whenValueWebToolIsOpened(): Pair<ValueOppositionWebsModel, ValueOppositionWebsViewListener>
    {
        projectScope.get<ThemeListViewListener>().openValueWeb(theme.id.uuid.toString())
        val toolScope = projectScope.toolScopes.filterIsInstance<ValueOppositionWebsScope>().first()
        return toolScope.get<ValueOppositionWebsModel>() to toolScope.get<ValueOppositionWebsViewListener>().also {
            it.getValidState()
        }
    }

    @Test
    fun `Open Tool with Value Webs Created in Theme`() {
        val expectedCount = 5
        repeat(expectedCount) {
            projectScope.get<CreateValueWebDialogViewListener>()
                .createValueWeb(theme.id.uuid.toString(), "Value Web $it")
        }

        val (model, viewListener) = whenValueWebToolIsOpened()

        assertEquals(
            expectedCount,
            model.valueWebs.size
        )
    }

    @Test
    fun `Select Value Web`() {
        projectScope.get<CreateValueWebDialogViewListener>()
            .createValueWeb(theme.id.uuid.toString(), "Justice")

        val valueWeb = runBlocking {
            projectScope.get<ThemeRepository>().getThemeById(theme.id)!!.valueWebs.find { it.name == "Justice" }!!
        }

        val (model, viewListener) = whenValueWebToolIsOpened()
        viewListener.selectValueWeb(valueWeb.id.uuid.toString())

        assertTrue(
            model.oppositionValues.value.isNotEmpty()
        )
    }

    @Test
    fun `React to Value Web Deleted`() {
        projectScope.get<CreateValueWebDialogViewListener>()
            .createValueWeb(theme.id.uuid.toString(), "Justice")

        val valueWeb = runBlocking {
            projectScope.get<ThemeRepository>().getThemeById(theme.id)!!.valueWebs.find { it.name == "Justice" }!!
        }

        val (model, viewListener) = whenValueWebToolIsOpened()

        DeleteValueWebDialogScope(projectScope, valueWeb.id.uuid.toString(), "")
            .get<DeleteValueWebDialogViewListener>().deleteValueWeb(true)
        assertNull(
            model.valueWebs.find { it.valueWebName == "Justice" }
        )
    }

    @Test
    fun `React to Value Web Created`() {
        val (model, viewListener) = whenValueWebToolIsOpened()

        projectScope.get<CreateValueWebDialogViewListener>().createValueWeb(theme.id.uuid.toString(), "Justice")

        assertNotNull(
            model.valueWebs.find { it.valueWebName == "Justice" }
        )
    }
}