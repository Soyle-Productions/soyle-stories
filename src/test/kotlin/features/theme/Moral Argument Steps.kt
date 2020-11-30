package com.soyle.stories.desktop.config.features.theme

import com.soyle.stories.common.PairOf
import com.soyle.stories.common.template
import com.soyle.stories.desktop.config.drivers.character.CharacterArcDriver
import com.soyle.stories.desktop.config.drivers.character.CharacterDriver
import com.soyle.stories.desktop.config.drivers.soylestories.ScenarioContext
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.theme.moralArgument.MoralArgumentViewAssert
import com.soyle.stories.desktop.view.theme.moralArgument.MoralArgumentViewAssert.Companion.assertThat
import com.soyle.stories.desktop.view.theme.moralArgument.MoralArgumentViewDriver
import com.soyle.stories.entities.*
import io.cucumber.java8.En
import io.cucumber.java8.PendingException
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import java.util.*

class `Moral Argument Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given(
            "the user has indicated they want to add a new section to {character}'s {theme} moral argument"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacterNamed(character.name.value)
            moralArgument.givenMoralArgumentHasBeenPreparedToAddNewSection()

        }
        Given(
            "the user has indicated they want to move one of {character}'s {theme} moral argument sections"
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
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacterNamed(character.name.value)
        }
    }

    private fun whens() {
        When(
            "the user wants to add a new section to {character}'s {theme} moral argument"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacterNamed(character.name.value)
            moralArgument.prepareToAddNewSection()

        }
        When(
            "the user wants to remove a section from {character}'s {theme} moral argument"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacterNamed(character.name.value)
        }
        When(
            "an unused moral argument section type is selected to be added for {string} in the {string} theme"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacterNamed(characterName)
            moralArgument.givenMoralArgumentHasBeenPreparedToAddNewSection()
            ScenarioContext(soyleStories).templateSectionToAdd = moralArgument.selectUnusedSectionType().sectionTypeId

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

            ScenarioContext(soyleStories).sectionsSurroundingTemplateSection = arc.moralArgument().arcSections.let { it[insertIndex-1] to it[insertIndex] }
            ScenarioContext(soyleStories).templateSectionToAdd = moralArgument.selectUnusedSectionType().sectionTypeId

        }
        When(
            "the user indicates they want to move one of {character}'s {theme} moral argument sections"
        ) { character: Character?, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacterNamed(character!!.name.value)
        }
        When(
            "a used moral argument section type is selected to be moved for {string} in the {string} theme"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacterNamed(characterName)
            moralArgument.givenMoralArgumentHasBeenPreparedToMoveSection()
            ScenarioContext(soyleStories).templateSectionToMove = moralArgument.selectUsedSectionType().sectionTypeId
        }
        When(
            "the {ordinal} section in {character}'s {theme} moral argument is moved above the {ordinal} section"
        ) { initialIndex: Int, character: Character, theme: Theme, moveIndex: Int ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)

            // Produces a snapshot of the character arc before the move.  Any [then]s trying to compare after the fact
            // will now have a snapshot of the arc to look back to
            CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)

            moralArgument.moveSectionToNewPosition(initialPosition = initialIndex, newPosition = moveIndex)
        }
        When(
            "the {template} section in {character}'s {theme} moral argument is removed"
        ) { template: CharacterArcTemplateSection, character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.givenMoralArgumentHasBeenLoadedForPerspectiveCharacter(character)

            moralArgument.removeFirstSectionWithName(template.name)
        }
    }

    private fun thens() {
        Then(
            "all the moral argument section types should be listed for {string}s moral argument in the {string} theme"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeNamedOrError(characterName, themeName)

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
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeNamedOrError(characterName, themeName)
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
                return "Expected to be placed between ${surroundingSections.first.id} and ${surroundingSections.second.id}.\n"+
                        "Should have had template of ${templateSectionToAdd}.\n"+
                        "\n"+
                        "Moral Argument Sections and Template ids received:\n"+
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
            "all of {character}'s {theme} moral argument sections should be listed"
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
            "the optional sections in {character}'s {theme} moral argument should be shown to be able to be removed"
        ) { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val arc = CharacterArcDriver(workbench).getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                andEachArcSection {
                    val baseSection = arc.moralArgument().arcSections[it]
                    if (! baseSection.template.isRequired) {
                        hasRemoveButton()
                    }
                }
            }
        }
        Then(
            "the required sections in {character}'s {theme} moral argument should not be shown to be able to be removed"
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
    }

}