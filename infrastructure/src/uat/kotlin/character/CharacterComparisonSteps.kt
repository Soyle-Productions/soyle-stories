package com.soyle.stories.character

import com.soyle.stories.characterarc.characterComparison.CharacterComparison
import com.soyle.stories.characterarc.characterComparison.CharacterComparisonScope
import com.soyle.stories.entities.Theme
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import com.soyle.stories.theme.ThemeSteps
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest
import java.util.logging.Logger

class CharacterComparisonSteps(en: En, double: SoyleStoriesTestDouble) {

    companion object : ApplicationTest() {

        fun getScope(projectScope: ProjectScope, themeId: Theme.Id): CharacterComparisonScope?
        {
            return projectScope.toolScopes
                .asSequence()
                .filterIsInstance<CharacterComparisonScope>()
                .find { it.themeId == themeId.uuid.toString() }
        }

        fun getOpenTool(scope: CharacterComparisonScope): CharacterComparison?
        {
            return findComponentsInScope<CharacterComparison>(scope).firstOrNull()
        }

        fun isToolOpen(projectScope: ProjectScope, themeId: Theme.Id): Boolean
        {
            val scope = getScope(projectScope, themeId) ?: return false
            return getOpenTool(scope) != null
        }

    }

    init {
        with(en) {

            Then("the Character Comparison Tool should be open") {
                val scope = getScope(
                    ProjectSteps.getProjectScope(double)!!,
                    ThemeSteps.getCreatedThemes(double).first().id
                )!!
                assertNotNull(getOpenTool(scope))
            }

        }
    }

}