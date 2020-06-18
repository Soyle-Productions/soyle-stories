package com.soyle.stories.theme

import com.soyle.stories.di.get
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.theme.repositories.ThemeRepository
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking

class ThemeSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object {
        fun getCreatedThemes(double: SoyleStoriesTestDouble): List<Theme>
        {
            val scope = ProjectSteps.getProjectScope(double) ?: return emptyList()
            return runBlocking {
                scope.get<ThemeRepository>().listThemesInProject(Project.Id(scope.projectId))
            }
        }
    }

    init {
        CreateThemeDialogSteps(en, double)

        with(en) {
        }
    }

}