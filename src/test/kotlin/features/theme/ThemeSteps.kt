package com.soyle.stories.desktop.config.features.theme

import com.soyle.stories.common.PairOf
import com.soyle.stories.desktop.config.drivers.character.CharacterArcDriver
import com.soyle.stories.desktop.config.drivers.character.CharacterDriver
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.theme.themeList.ThemeListAssert.Companion.assertThat
import com.soyle.stories.desktop.view.theme.moralArgument.MoralArgumentViewAssert.Companion.assertThat
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.theme.moralArgument.MoralArgumentSectionTypeViewModel
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class ThemeSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("a theme named {string} has been created") { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            ThemeDriver(workbench).givenThemeNamed(themeName)
        }
        Given("the following characters have been included as major characters in the {string} theme") { themeName: String, data: DataTable ->

            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val characterDriver = CharacterDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            data.asList().forEach { characterName ->
                val character = characterDriver.getCharacterByNameOrError(characterName)
                themeDriver.givenCharacterIsMajorCharacterInTheme(character.id, theme.id)
            }
        }
        Given(
            "the character {string} has been included in the {string} theme as a major character"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val characterDriver = CharacterDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            val character = characterDriver.getCharacterByNameOrError(characterName)
            themeDriver.givenCharacterIsMajorCharacterInTheme(character.id, theme.id)
        }
        Given(
            "the user has indicated they want to add a new section to the moral argument for {string} in the {string} theme"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacterNamed(characterName)
            moralArgument.givenMoralArgumentHasBeenPreparedToAddNewSection()

        }
    }

    private fun whens() {
        When("a theme is created with the name {string}") { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.openCreateThemeDialog()
                .createThemeWithName(themeName)
        }
        When("the {string} theme is renamed with the name {string}") { originalThemeName: String, newName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenThemeListToolHasBeenOpened()
                .renameThemeTo(originalThemeName, newName)
        }
        When("the {string} theme is deleted") { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeList = workbench.givenThemeListToolHasBeenOpened()
            themeList.openDeleteThemeDialogForThemeNamed(themeName)
                ?.confirmDeleteTheme()
        }
        When(
            "the user indicates that they want to add a new section to the moral argument for {string} in the {string} theme"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacterNamed(characterName)
            moralArgument.prepareToAddNewSection()

        }
        When(
            "the moral problem for the {string} theme is changed to {string}"
        ) { themeName: String, moralProblem: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.changeMoralProblemTo(moralProblem)
        }
        When(
            "the theme line for the {string} theme is changed to {string}"
        ) { themeName: String, themeLine: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.changeThemeLineTo(themeLine)
        }
        When(
            "an unused moral argument section type is selected to be added for {string} in the {string} theme"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacterNamed(characterName)
            moralArgument.givenMoralArgumentHasBeenPreparedToAddNewSection()
            templateSectionToAdd = moralArgument.selectUnusedSectionType()

        }
        When(
            "the thematic revelation for the {string} theme is changed to {string}"
        ) { themeName: String, thematicRevelation: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.changeThematicRevelationTo(thematicRevelation)

        }
        When(
            "a moral argument section type is selected for {string} in the {string} theme between two existing sections"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val character = CharacterDriver(workbench).getCharacterByNameOrError(characterName)
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacterNamed(characterName)

            val insertIndex = arc.moralArgument().arcSections.size / 2
            moralArgument.givenMoralArgumentHasBeenPreparedToAddNewSection(arc.moralArgument().arcSections.size / 2)

            sectionsSurroundingTemplateSection = arc.moralArgument().arcSections.let { it[insertIndex-1] to it[insertIndex] }
            templateSectionToAdd = moralArgument.selectUnusedSectionType()

        }
    }

    private fun thens() {
        Then("a theme named {string} should have been created") { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            ThemeDriver(workbench).getThemeByNameOrError(themeName)

            val themeList = workbench.givenThemeListToolHasBeenOpened()
            assertThat(themeList) {
                hasThemeNamed(themeName)
            }
        }
        Then("the theme originally named {string} should have been renamed to {string}") { originalThemeName: String, newName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeAtOnePointNamedOrError(originalThemeName).second!!

            assertEquals(newName, theme.name) { "Theme $originalThemeName was not renamed to $newName" }

            val themeList = workbench.givenThemeListToolHasBeenOpened()
            assertThat(themeList) {
                doesNotHaveThemeNamed(originalThemeName)
                hasThemeNamed(newName)
            }

        }
        Then("the {string} theme should have been deleted") { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            assertNull(ThemeDriver(workbench).getThemeByName(themeName)) { "Theme $themeName not deleted" }

            val themeList = workbench.givenThemeListToolHasBeenOpened()
            assertThat(themeList) {
                doesNotHaveThemeNamed(themeName)
            }

        }
        Then(
            "all the moral argument section types should be listed for {string}s moral argument in the {string} theme"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val character = CharacterDriver(workbench).getCharacterByNameOrError(characterName)
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                andAtLeastOneSectionTypeMenu {
                    onlyHasItems(arc.template.sections.filter { it.isMoral }.map { it.name })
                }
            }
        }
        Then(
            "the moral problem for the {string} theme should be {string}"
        ) { themeName: String, expectedMoralProblem: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            assertEquals(
                expectedMoralProblem,
                theme.centralMoralProblem
            ) { "$themeName's moral problem was not updated." }

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                andMoralProblemField {
                    hasValue(expectedMoralProblem)
                }
            }
        }
        Then(
            "the theme line for the {string} theme should be {string}"
        ) { themeName: String, expectedThemeLine: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            assertEquals(expectedThemeLine, theme.themeLine) { "$themeName's theme line was not updated." }

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                andThemeLineField {
                    hasValue(expectedThemeLine)
                }
            }
        }
        Then(
            "the thematic revelation for the {string} theme should be {string}"
        ) { themeName: String, expectedRevelation: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            assertEquals(expectedRevelation, theme.thematicRevelation) { "$themeName's thematic revelation was not updated." }

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                andThematicRevelationField {
                    hasValue(expectedRevelation)
                }
            }
        }
        Then(
            "a new section should have been added to {string}s character arc in the {string} theme with that type"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val character = CharacterDriver(workbench).getCharacterByNameOrError(characterName)
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)

            arc.arcSections.find {
                it.template.id.uuid.toString() == templateSectionToAdd!!.sectionTypeId
            }!!

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                onlyHasArcSections(
                    arc.moralArgument().arcSections.map { it.template.name }
                )
            }

            updatedCharacterArc = arc
        }
        Then(
            "the new section should be at the end of {string}s moral argument in the {string} theme"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val character = CharacterDriver(workbench).getCharacterByNameOrError(characterName)
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)

            val newSection =
                arc.arcSections.find { it.template.id.uuid.toString() == templateSectionToAdd!!.sectionTypeId }!!
            assertEquals(arc.moralArgument().arcSections.last(), newSection)
        }
        Then("the new section should be between the two pre-existing moral argument sections") {
            val surroundingSections = sectionsSurroundingTemplateSection!!
            val arc = updatedCharacterArc!!

            val firstIndex = arc.indexInMoralArgument(surroundingSections.first.id)!!
            val secondIndex = arc.indexInMoralArgument(surroundingSections.second.id)!!

            val newSection = arc.moralArgument().arcSections[firstIndex + 1]
            val midIndex = arc.indexInMoralArgument(newSection.id)

            fun explanation(): String {
                return "Expected to be placed between ${surroundingSections.first.id} and ${surroundingSections.second.id}.\n"+
                    "Should have had template of ${templateSectionToAdd!!.sectionTypeId}.\n"+
                    "\n"+
                    "Moral Argument Sections and Template ids received:\n"+
                    arc.moralArgument().arcSections.joinToString("\n") {
                        val prefix = if (it.id == surroundingSections.first.id) " 1 "
                        else if (it.id == surroundingSections.second.id) " 2 "
                        else if (it.template.id.uuid.toString() == templateSectionToAdd!!.sectionTypeId) " + "
                        else " - "

                        prefix + "Section Id: ${it.id.uuid}, template: ${it.template.id.uuid}"
                    }

            }

            assertEquals(templateSectionToAdd!!.sectionTypeId, newSection.template.id.uuid.toString()) { explanation() }
            assertEquals(firstIndex + 1, midIndex) { explanation() }
            assertEquals(secondIndex - 1, midIndex) { explanation() }
        }
    }

    private var sectionsSurroundingTemplateSection: PairOf<CharacterArcSection>? = null
    private var templateSectionToAdd: MoralArgumentSectionTypeViewModel? = null
    private var updatedCharacterArc: CharacterArc? = null

}