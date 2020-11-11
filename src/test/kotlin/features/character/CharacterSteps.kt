package com.soyle.stories.desktop.config.features.character

import com.soyle.stories.desktop.config.drivers.character.CharacterArcDriver
import com.soyle.stories.desktop.config.drivers.character.CharacterDriver
import com.soyle.stories.desktop.config.drivers.project.ProjectDriver
import com.soyle.stories.desktop.config.drivers.soylestories.ScenarioContext
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.soylestories.getWorkbenchForProjectOrError
import com.soyle.stories.desktop.config.drivers.theme.ThemeDriver
import com.soyle.stories.desktop.config.drivers.theme.givenMoralArgumentToolHasBeenOpenedForTheme
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.theme.moralArgument.MoralArgumentViewAssert
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcTemplate
import com.soyle.stories.entities.CharacterArcTemplateSection
import com.soyle.stories.entities.Theme
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.*
import kotlin.math.exp

class CharacterSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("the following characters have been created") { data: DataTable ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val characterDriver = CharacterDriver(workbench)
            data.asList().forEach { characterName ->
                characterDriver.givenCharacterNamed(characterName)
            }
        }
        Given("a character named {string} has been created") { characterName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val characterDriver = CharacterDriver(workbench)
            characterDriver.givenCharacterNamed(characterName)
        }
    }

    private fun whens() {

    }

    private fun thens() {
        Then(
            "all the character arcs in the theme originally named {string} should have been renamed to {string}"
        ) { originalThemeName: String, expectedArcName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeAtOnePointNamedOrError(originalThemeName).second!!
            val arcs = CharacterArcDriver(workbench).getCharacterArcsForTheme(theme.id)

            arcs.onEach {
                assertEquals(expectedArcName, it.name) { "Character arc ${it.id} was not renamed to $expectedArcName" }
            }
        }
        Then(
            "all the character arcs in the theme named {string} should have been deleted"
        ) { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeId = ThemeDriver(workbench).getThemeAtOnePointNamedOrError(themeName).first!!
            val arcs = CharacterArcDriver(workbench).getCharacterArcsForTheme(themeId)

            assertTrue(arcs.isEmpty()) { "Not all character arcs for $themeName theme deleted.  Still have ${arcs.size} left." }
        }
        Then(
            "all the character arcs in the theme named {string} should not have been deleted"
        ) { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeId = ThemeDriver(workbench).getThemeAtOnePointNamedOrError(themeName).first!!
            val arcs = CharacterArcDriver(workbench).getCharacterArcsForTheme(themeId)

            assertFalse(arcs.isEmpty()) { "All character arcs for $themeName theme have been deleted." }
        }
        Then(
            "a new section should have been added to {string}s character arc in the {string} theme with that type"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val character = CharacterDriver(workbench).getCharacterByNameOrError(characterName)
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)
            val templateSectionToAdd = ScenarioContext(soyleStories).templateSectionToAdd!!

            arc.arcSections.find {
                it.template.id.uuid.toString() == templateSectionToAdd
            }!!

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            MoralArgumentViewAssert.assertThat(moralArgument) {
                onlyHasArcSections(
                    arc.moralArgument().arcSections.map { it.template.name }
                )
            }

            ScenarioContext(soyleStories).updatedCharacterArc = arc
        }
    }
}