package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.createTheme.CreateThemeController
import com.soyle.stories.theme.repositories.ThemeRepository
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.*

class ThemeSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object {
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
    }

    init {
        CreateThemeDialogSteps(en, double)
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

        }
    }

}