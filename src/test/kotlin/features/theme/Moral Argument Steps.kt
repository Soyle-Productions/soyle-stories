package com.soyle.stories.desktop.config.features.theme

import com.soyle.stories.desktop.config.drivers.character.CharacterArcDriver
import com.soyle.stories.desktop.config.drivers.character.CharacterDriver
import com.soyle.stories.desktop.config.drivers.soylestories.ScenarioContext
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.theme.moralArgument.MoralArgumentViewAssert.Companion.assertThat
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcTemplateSection
import com.soyle.stories.entities.Theme
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.*

class `Moral Argument Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given(
            "the {string} section has been added to the {character}'s {moral argument}"
        ) { sectionName: String, character: Character, theme: Theme ->
            val driver = CharacterArcDriver(soyleStories.getAnyOpenWorkbenchOrError())
            val arc = driver.getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)
            driver.givenArcHasArcSectionInMoralArgument(
                arc,
                arc.template.sections.find { it.name == sectionName }!!
            )
        }
        Given(
            "I am moving one of the {character}'s {moral argument} sections"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
        }
        Given(
            "the user has indicated they want to remove a section from {character}'s {theme} moral argument"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
        }
        Given(
            "I am removing a section from the {character}'s {moral argument}"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
        }
        Given("I am outlining the {character}'s {moral argument}") { character: Character, theme: Theme ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenMoralArgumentToolHasBeenOpenedForTheme(theme)
                .givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
        }
        Given(
            "I have requested which sections are available to add to the {character}'s {moral argument}"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
            moralArgument.givenMoralArgumentHasBeenPreparedToAddNewSection()
        }
        Given(
            "I have requested which sections are available to add to the {character}'s {moral argument} after the {string} section"
        ) { character: Character, theme: Theme, relativeSectionName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
            moralArgument.givenMoralArgumentHasBeenPreparedToAddNewSectionAfter(relativeSectionName)
        }
        Given(
            "I have chosen the {ordinal} position to move one of the {character}'s {moral argument} sections"
        ) { ordinal: Int, character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
            moralArgument.givenPreparedToMoveSectionTo(ordinal)
        }
    }

    private fun whens() {
        When(
            "I request which sections are available to add to the {character}'s {moral argument}"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
            moralArgument.prepareToAddNewSection()
        }
        When(
            "I request which sections are available to add to the {character}'s {moral argument} after the {string} section"
        ) { character: Character, theme: Theme, sectionName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
            moralArgument.prepareToAddNewSectionAfter(sectionName)
        }
        When(
            "I want to remove a section from the {character}'s {moral argument}"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.loadMoralArgumentForPerspectiveCharacter(character)
        }
        When(
            "I choose the {string} section type to add to the {character}'s {moral argument}"
        ) { sectionTypeName: String, character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
            moralArgument.selectFromAvailableSections(sectionTypeName)
        }
        When(
            "I choose the {ordinal} position to move one of the {character}'s {moral argument} sections"
        ) { ordinal: Int, character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
            moralArgument.prepareToMoveSectionTo(ordinal)
        }
        When(
            "I move the {string} section to the {ordinal} position of the {character}'s {moral argument}"
        ) { sectionName: String, ordinal: Int, character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
            moralArgument.dragSectionToPosition(sectionName, ordinal)
        }
        When(
            "I choose to move the {string} section of the {character}'s {moral argument}"
        ) { sectionName: String, character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
            moralArgument.selectFromAvailableSections(sectionName)
        }
        When(
            "I remove the {string} section from the {character}'s {moral argument}"
        ) { sectionName: String, character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val section = CharacterDriver(workbench).getCharacterArcSectionByNameOrError(character, theme, sectionName)
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
            moralArgument.removeFirstSectionWithName(section.template.name)
        }
    }

    private fun thens() {
        Then(
            "I should see the following options to add to the {character}'s {moral argument}"
        ) { character: Character, theme: Theme, dataTable: DataTable ->
            val moralArgumentView = soyleStories.getAnyOpenWorkbenchOrError()
                .givenMoralArgumentToolHasBeenOpenedForTheme(theme)
                .givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
            assertThat(moralArgumentView) {
                andSectionTypeMenu {
                    hasItemsInOrder(dataTable.asLists().drop(1)
                        .map { (sectionName, usability) -> sectionName to (usability == "unused") })
                }
            }
        }
        Then(
            "all the moral argument section types should be listed for {string}s moral argument in the {string} theme"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val arc =
                CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeNamedOrError(characterName, themeName)

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                andAtLeastOneSectionTypeMenu {
                    onlyHasItems(arc.template.sections.filter { it.isMoral }.map { it.name })
                }
            }
        }
        Then(
            "the new section should be at the end of {string}s moral argument in the {string} theme"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val arc =
                CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeNamedOrError(characterName, themeName)
            val templateSectionToAdd = ScenarioContext(soyleStories).templateSectionToAdd!!

            val newSection =
                arc.arcSections.find { it.template.id.uuid.toString() == templateSectionToAdd }!!
            assertEquals(arc.moralArgument().arcSections.last(), newSection)
        }
        Then("the new section should be between the two pre-existing moral argument sections") {
            val surroundingSections = ScenarioContext(soyleStories).sectionsSurroundingTemplateSection!!
            val arc = ScenarioContext(soyleStories).updatedCharacterArc!!
            val templateSectionToAdd = ScenarioContext(soyleStories).templateSectionToAdd!!

            val firstIndex = arc.indexInMoralArgument(surroundingSections.first.id)!!
            val secondIndex = arc.indexInMoralArgument(surroundingSections.second.id)!!

            val newSection = arc.moralArgument().arcSections[firstIndex + 1]
            val midIndex = arc.indexInMoralArgument(newSection.id)

            fun explanation(): String {
                return "Expected to be placed between ${surroundingSections.first.id} and ${surroundingSections.second.id}.\n" +
                        "Should have had template of ${templateSectionToAdd}.\n" +
                        "\n" +
                        "Moral Argument Sections and Template ids received:\n" +
                        arc.moralArgument().arcSections.joinToString("\n") {
                            val prefix = if (it.id == surroundingSections.first.id) " 1 "
                            else if (it.id == surroundingSections.second.id) " 2 "
                            else if (it.template.id.uuid.toString() == templateSectionToAdd) " + "
                            else " - "

                            prefix + "Section Id: ${it.id.uuid}, template: ${it.template.id.uuid}"
                        }

            }

            assertEquals(
                templateSectionToAdd,
                newSection.template.id.uuid.toString()
            ) { explanation() }
            assertEquals(firstIndex + 1, midIndex) { explanation() }
            assertEquals(secondIndex - 1, midIndex) { explanation() }
        }
        Then(
            "all of the {character}'s {moral argument} sections should be listed"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                onlyHasArcSections(
                    arc.moralArgument().arcSections.map { it.template.name }
                )
            }
        }
        Then(
            "all of the {character}'s {moral argument} sections should be listed to be moved"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                andSectionTypeMenu {
                    hasItems(arc.moralArgument().arcSections.map { it.template.name })
                }
            }
        }
        Then(
            "the order of the sections in the {character}'s {moral argument} should be as follows"
        ) { character: Character, theme: Theme, dataTable: DataTable ->
            val expectedNameOrder = dataTable.asList()
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)
            assertEquals(
                expectedNameOrder,
                arc.moralArgument().arcSections.map { it.template.name }
            )

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                hasArcSectionsInOrder(expectedNameOrder)
            }
        }
        Then(
            "the section originally in the {ordinal} position in {character}'s {theme} moral argument should be in the {ordinal} position"
        ) { initialIndex: Int, character: Character, theme: Theme, movedIndex: Int ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val arcDriver = CharacterArcDriver(workbench)
            val arc = arcDriver.getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)
            val lastArc = arcDriver.getPreviousVersions(arc).dropLast(1).last()

            val sectionAtFirstPosition = lastArc.moralArgument().arcSections[initialIndex]
            val sectionAtNewPosition = arc.moralArgument().arcSections[movedIndex]

            assertNotEquals(lastArc, arc)
            assertEquals(sectionAtFirstPosition, sectionAtNewPosition)

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)

            assertThat(moralArgument) {
                andEachArcSection {
                    if (it == movedIndex) {
                        hasLabel(sectionAtFirstPosition.template.name)
                        hasValue(sectionAtFirstPosition.value)
                        hasId(sectionAtFirstPosition.id.uuid.toString())
                    }
                }
            }

        }
        Then(
            "the {string} section should be removed from the {character}'s {moral argument}"
        ) { sectionName: String, character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)
            assertNull(arc.moralArgument().arcSections.find { it.template.name == sectionName })

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)
            assertThat(moralArgument) {
                andEachArcSection {
                    val baseSection = arc.moralArgument().arcSections[it]
                    if (!baseSection.template.isRequired) {
                        hasRemoveButton()
                    }
                }
            }
        }
        Then(
            "the optional sections in the {character}'s {moral argument} should indicate they can be removed"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                onlyHasArcSections(
                    arc.moralArgument().arcSections.map { it.template.name }
                )
            }
        }
        Then(
            "the required sections in the {character}'s {moral argument} should indicate they can not be removed"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                andEachArcSection {
                    val baseSection = arc.moralArgument().arcSections[it]
                    if (baseSection.template.isRequired) {
                        doesNotHaveRemoveButton()
                    }
                }
            }
        }
        Then(
            "the {template} section should be removed from {character}'s {theme} moral argument"
        ) { template: CharacterArcTemplateSection, character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)
            assertFalse(arc.arcSections.any { it.template == template }) { "${character.name}'s ${theme.name} moral argument still contains ${template.name}" }

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                onlyHasArcSections(arc.moralArgument().arcSections.map { it.template.name })
            }
        }
        Then(
            "the last section of the {character}'s {moral argument} should be the {string} section"
        ) { character: Character, theme: Theme, expectedSectionName: String ->
            CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .getCharacterArcByCharacterAndTheme(character, theme)!!
                .moralArgument()
                .arcSections
                .last()
                .template.name
                .let { assertEquals(expectedSectionName, it) }
        }
        Then(
            "the section after the {string} section in the {character}'s {moral argument} should be the {string} section"
        ) { relativeSectionName: String, character: Character, theme: Theme, expectedSectionName: String ->
            val arcSections = CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .getCharacterArcByCharacterAndTheme(character, theme)!!
                .moralArgument()
                .arcSections
            val relativeIndex = arcSections.indexOfFirst { it.template.name == relativeSectionName }
            assertEquals(expectedSectionName, arcSections[relativeIndex + 1].template.name)
        }
    }

}