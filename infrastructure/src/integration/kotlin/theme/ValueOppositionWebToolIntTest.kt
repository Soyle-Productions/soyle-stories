package com.soyle.stories.theme

import com.soyle.stories.common.SoyleStoriesIntegrationDouble
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.get
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.config.fixed.ThemeList
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogViewListener
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.themeList.ThemeListViewListener
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsModel
import com.soyle.stories.theme.themeOppositionWebs.ValueOppositionWebsScope
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ValueOppositionWebToolIntTest {

    private val projectScope: ProjectScope
    private val theme: Theme
    init {
        // given a project has been opened
        val applicationScope = SoyleStoriesIntegrationDouble().scope
        val projectViewListener = applicationScope.get<ProjectListViewListener>()
        applicationScope.get<ThreadTransformer>().async {
            projectViewListener.startApplicationWithParameters(listOf())
            projectViewListener.startNewProject("C:\\Users", "Untitled")
        }
        projectScope = applicationScope.projectScopes.first()
        // given a theme has been created
        projectScope.get<CreateThemeDialogViewListener>().createTheme("Growing Up")
        theme = runBlocking {
            projectScope.get<ThemeRepository>().listThemesInProject(Project.Id(projectScope.projectId)).first()
        }
    }

    @Test
    fun `Open Tool Before Any Value Webs Created in Theme`() {
        projectScope.applicationScope.get<ThreadTransformer>().async {
            projectScope.get<ThemeListViewListener>().openValueWeb(theme.id.uuid.toString())
        }
        assertTrue(
            projectScope.toolScopes.filterIsInstance<ValueOppositionWebsScope>().first().get<ValueOppositionWebsModel>().valueWebs.isEmpty()
        )
    }

}