package com.soyle.stories.desktop.config.features.character

import com.soyle.stories.character.rename.RenameCharacterForm
import com.soyle.stories.desktop.config.drivers.character.*
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.ScenarioContext
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbench
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.ThemeDriver
import com.soyle.stories.desktop.config.drivers.theme.givenMoralArgumentToolHasBeenOpenedForTheme
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.character.list.CharacterListAssertions
import com.soyle.stories.desktop.view.character.profile.`Character Profile Assertions`.Companion.assertThat
import com.soyle.stories.desktop.view.character.profile.`Character Profile View Access`.Companion.access
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.desktop.view.theme.moralArgument.MoralArgumentViewAssert
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import java.util.*
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
                characterDriver.givenCharacterNamed(NonBlankString.create(characterName)!!)
            }
        }
        Given("I have created the following characters") { data: DataTable ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val characterDriver = CharacterDriver(workbench)
            data.asList().forEach { characterName ->
                characterDriver.givenCharacterNamed(NonBlankString.create(characterName)!!)
            }
        }
        Given("a character named {string} has been created") { characterName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val characterDriver = CharacterDriver(workbench)
            characterDriver.givenCharacterNamed(NonBlankString.create(characterName)!!)
        }
        Given("I have created a character named {string}")  { characterName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val characterDriver = CharacterDriver(workbench)
            characterDriver.givenCharacterNamed(NonBlankString.create(characterName)!!)
        }
        Given("I have renamed the {character} to {string}") { character: Character, newName: String ->
            if (character.name.value != newName) {
                CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError())
                    .givenCharacterRenamedTo(character.id, newName)
            }
        }
        Given("I am deleting the {character}") { character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .givenDeleteCharacterDialogHasBeenOpened(character.id)
        }
        Given("I have removed the {character} from the story") { character: Character ->
            CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenCharacterRemoved(character)
        }
        Given("I have created a character arc for the {character} in the {theme}") { character: Character, theme: Theme ->
            ThemeDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenCharacterIsMajorCharacterInTheme(character.id, theme.id)
        }
        Given(
            "I have created the following character arcs for the {character}"
        ) { character: Character, dataTable: DataTable ->
            val driver = CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError())
            dataTable.asList().forEach {
                driver.givenCharacterHasAnArcNamed(character, it)
            }
        }
        Given("I have created {int} character arcs for the {character}") { arcCount: Int, character: Character ->
            val driver = CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError())
            repeat(arcCount) {
                driver.givenCharacterHasAnArcNamed(character, UUID.randomUUID().toString())
            }
        }
        Given("I am creating a character") {
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCreateCharacterDialogHasBeenOpened()
        }
        Given("I am creating a name variant for the {character}") { character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .givenCharacterProfileOpenedFor(character)
                .givenCreatingCharacterNameVariant()
        }
        Given("I have created a name variant of {string} for the {character}") { variant: String, character: Character ->
            CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenCharacterHasANameVariant(character, variant)
        }
        Given("I am renaming the {string} name variant for the {character}") { variant: String, character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .givenCharacterProfileOpenedFor(character)
                .givenRenamingCharacterNameVariant(variant)
        }
    }

    private fun whens() {
        When("I rename the {character} to {string}") { character: Character, newName: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .givenRenameCharacterDialogHasBeenOpened(character.id)
                .renameCharacterTo(newName)
        }
        When("I want to delete the {character}") { character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .openDeleteCharacterDialog(character.id)
        }
        When("I confirm I want to delete the {character}") { character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .givenDeleteCharacterDialogHasBeenOpened(character.id)
                .confirmDelete()
        }
        When("I remove the {character} from the story") { character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .givenDeleteCharacterDialogHasBeenOpened(character.id)
                .confirmDelete()
        }
        When("I create a character named {string}") { name: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCreateCharacterDialogHasBeenOpened()
                .createCharacterWithName(name)
        }
        When("I create a character without a name") {
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCreateCharacterDialogHasBeenOpened()
                .createCharacterWithName("")
        }
        When("I create a name variant of {string} for the {character}") { variant: String, character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .givenCharacterProfileOpenedFor(character)
                .givenCreatingCharacterNameVariant()
                .createNameVariant(variant)
        }
        When(
            "I rename the name variant of {string} for the {character} to {string}"
        ) { originalVariant: String, character: Character, newVariant: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .givenCharacterProfileOpenedFor(character)
                .givenRenamingCharacterNameVariant(originalVariant)
                .renameVariantTo(originalVariant, newVariant)
        }
        When(
            "I remove the {string} name variant for the {character}"
        ) { variant: String, character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .givenCharacterProfileOpenedFor(character)
                .removeVariant(variant)
        }
    }

    private fun thens() {
        Then("I should still be creating a character") {
            getCreateCharacterDialogOrError()
        }
        Then("I should not be creating a character") {
            assertNull(getCreateCharacterDialog())
        }
        Then(
            "a character named {string} should have been created"
        ) { name: String ->
            CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .getCharacterByNameOrError(name)
        }
        Then("a new character should not have been created") {
            CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .getCharacterCountInProject()
                .let { assertEquals(0, it) }
        }
        Then("I should be prompted to confirm deleting the {character}") { character: Character ->
            getDeleteCharacterDialogOrError()
        }
        Then("the {string} character should not have been deleted") { characterName: String ->
            CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()).getCharacterByNameOrError(characterName)
        }
        Then("the {string} character should have been deleted") { characterName: String ->
            assertNull(CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()).getCharacterByName(characterName))
        }
        Then(
            "the {character}'s character arc for the {theme} should have been renamed to {string}"
        ) { character: Character, theme: Theme, expectedName: String ->
            val workBench = soyleStories.getAnyOpenWorkbenchOrError()
            val arc = CharacterDriver(workBench)
                .getCharacterArcByCharacterAndTheme(character, theme)!!
            assertEquals(expectedName, arc.name)

            CharacterListAssertions.assertThat(workBench.givenCharacterListToolHasBeenOpened()) {
                characterArcHasName(character.id, theme.id, arc.id, expectedName)
            }
        }
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
        Then("the character formerly named {string} should have the name {string}") { originalName: String, expectedName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val character = CharacterDriver(workbench)
                .getCharacterAtOnePointNamed(originalName)!!
            assertEquals(expectedName, character.name.value)

            CharacterListAssertions.assertThat(workbench.givenCharacterListToolHasBeenOpened()) {
                characterHasName(character.id, expectedName)
            }
        }
        Then("I should still be renaming the {character}") { character: Character ->
            assertNotNull(robot.getOpenDialog<RenameCharacterForm>())
        }
        Then(
            "the {character} should still have the display name of {string}"
        ) { character: Character, expectedName: String ->
            assertEquals(expectedName, character.name.value)
        }
        Then(
            "I should not be creating a name variant for the {character}"
        ) { character: Character ->
            val characterProfile = soyleStories.getAnyOpenWorkbench()
                ?.getCharacterListTool()
                ?.getCharacterProfileFor(character)

            if (characterProfile == null) return@Then

            assertThat(characterProfile) {
                isNotCreatingNameVariant()
            }
        }
        Then(
            "the {character} should have a name variant of {string}"
        ) { character: Character, expectedVariant: String ->
            assertTrue(character.otherNames.contains(NonBlankString.create(expectedVariant)!!))

            val characterProfile = soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .givenCharacterProfileOpenedFor(character)

            assertThat(characterProfile) {
                hasNameVariant(expectedVariant)
            }
        }
        Then("I should still be creating a name variant for the {character}") { character: Character ->
            val characterProfile = soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .getCharacterProfileFor(character)!!

            assertThat(characterProfile) {
                isCreatingNameVariant()
            }
        }
        Then(
            "I should still be renaming the name variant {string} for the {character}"
        ) { variant: String, character: Character ->
            val characterProfile = soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .getCharacterProfileFor(character)!!

            assertThat(characterProfile) {
                isRenamingNameVariant(variant)
            }
        }
        Then("I should not be renaming a name variant for the {character}") { character: Character ->
            val characterProfile = soyleStories.getAnyOpenWorkbenchOrError()
                .givenCharacterListToolHasBeenOpened()
                .getCharacterProfileFor(character) ?: return@Then

            assertThat(characterProfile) {
                isNotRenamingNameVariantFor(character)
            }
        }
        Then(
            "the {character} should not have a name variant of {string}"
        ) { character: Character, variant: String ->
            assertNull(character.otherNames.find { it.value == variant })
        }
        Then(
            "the {character} should have only one name variant of {string}"
        ) { character: Character, variant: String ->
            character.otherNames.filter { it.value == variant }.single()
        }
    }
}