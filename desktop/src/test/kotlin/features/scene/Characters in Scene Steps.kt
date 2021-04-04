package com.soyle.stories.desktop.config.features.scene


import com.soyle.stories.desktop.config.drivers.character.CharacterDriver
import com.soyle.stories.desktop.config.drivers.character.createSectionForTemplate
import com.soyle.stories.desktop.config.drivers.scene.*
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.sceneCharacters.SceneCharactersAssertions
import com.soyle.stories.desktop.view.scene.sceneCharacters.SceneCharactersAssertions.IncludedCharacterAssertions.Companion.andCharacter
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.sceneList.SceneListView
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*

class `Characters in Scene Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private val sceneDriver: SceneDriver
        get() = SceneDriver.invoke(soyleStories.getAnyOpenWorkbenchOrError())

    private val characterDriver by lazy { CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()) }


    private val sceneListView: SceneListView
        get() = soyleStories.getAnyOpenWorkbenchOrError()
            .givenSceneListToolHasBeenOpened()

    private fun givens() {
        Given("I have included the {character} in the {scene}") { character: Character, scene: Scene ->
            sceneDriver.givenCharacterIncludedInScene(scene, character)
        }
        Given("I am tracking the {scene}'s characters") { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
        }
        Given(
            "I have requested which arc sections for the {character} can be covered in the {scene}"
        ) { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenAvailableArcsToCoverHaveBeenRequestedFor(character)
        }
        Given(
            "I have requested which arc sections for the {character} can be uncovered in the {scene}"
        ) { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenAvailableArcsToCoverHaveBeenRequestedFor(character)
        }
        Given(
            "I am covering character arc sections for the {character} in the {scene}"
        ) { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenAvailableArcsToCoverHaveBeenRequestedFor(character)
        }
        Given(
            "I am including characters in the {scene}"
        ) { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
        }
        Given(
            "I have covered the following character arc sections in the {scene} for the {character}"
        ) { scene: Scene, character: Character, dataTable: DataTable ->
            val rows = dataTable.asLists().drop(1)
            val arcs = characterDriver
                .getCharacterArcsForCharacter(character)
            rows.forEach { (arcName, sectionName) ->
                val arc = arcs.find { it.name == arcName }!!
                val section = arc.arcSections.find { it.template.name == sectionName }!!
                sceneDriver.givenSceneCoversArcSection(scene, section)
            }
        }
        Given(
            "I have set the {character}'s motivation to {string} in the {scene}"
        ) { character: Character, motivation: String, scene: Scene ->
            sceneDriver.givenCharacterIncludedInScene(scene, character, motivation)
        }
    }

    private fun whens() {
        When("I include the {character} in the {scene}") { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .includeCharacter(character)
        }
        When(
            "I request which arc sections for the {character} can be covered in the {scene}"
        ) { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .requestAvailableArcsToCoverFor(character)
        }
        When(
            "I request which arc sections for the {character} can be uncovered in the {scene}"
        ) { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .requestAvailableArcsToCoverFor(character)
        }
        When(
            "I cover the {string} section from the {character}'s {string} character arc in the {scene}"
        ) { sectionName: String, character: Character, arcName: String, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenAvailableArcsToCoverHaveBeenRequestedFor(character)
                .coverSectionInArc(arcName, sectionName)
        }
        When(
            "I uncover the {string} section from the {character}'s {string} character arc in the {scene}"
        ) { sectionName: String, character: Character, arcName: String, scene: Scene ->

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenAvailableArcsToCoverHaveBeenRequestedFor(character)
                .uncoverSectionInArc(arcName, sectionName)
        }
        When(
            "I create a new {string} arc section in the {character}'s {string} arc to cover in the {scene}"
        ) { sectionName: String, character: Character, arcName: String, scene: Scene ->

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenAvailableArcsToCoverHaveBeenRequestedFor(character)
                .givenCreateNewSectionInArcSelected(arcName)
                .createSectionForTemplate(sectionName)
        }
        When("I remove the {character} from the {scene}") { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .removeCharacter(character)
        }
        When(
            "I set the {character}'s motivation to {string} in the {scene}"
        ) { character: Character, motivation: String, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenEditing(character)
                .setMotivationAs(motivation)
        }
    }

    private fun thens() {
        Then(
            "the {character} should be in the {scene}"
        ) { character: Character, scene: Scene ->
            assertTrue(scene.includesCharacter(character.id))

            val sceneCharactersTool = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneCharactersAssertions.assertThat(sceneCharactersTool) {
                hasCharacter(character)
            }
        }
        Then(
            "the {character} should not be in the {scene}"
        ) { character: Character, scene: Scene ->
            assertFalse(scene.includesCharacter(character.id))

            val sceneCharactersTool = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneCharactersAssertions.assertThat(sceneCharactersTool) {
                doesNotHaveCharacter(character)
            }
        }
        Then(
            "the {scene} should not have a character named {string}"
        ) { scene: Scene, characterName: String ->
            assertNull(scene.includedCharacters.find { it.characterName == characterName })

            val sceneCharactersTool = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneCharactersAssertions.assertThat(sceneCharactersTool) {
                doesNotHaveCharacterNamed(characterName)
            }
        }
        Then(
            "the {scene} should not have a motivation for the {character} anymore"
        ) { scene: Scene, character: Character ->

            Assertions.assertTrue(scene.getMotivationForCharacter(character.id)!!.isInherited())

            val sceneCharactersTool = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenEditing(character)

            SceneCharactersAssertions.assertThat(sceneCharactersTool) {
                andCharacter(character.id) {
                    hasMotivationValue("")
                }
            }
        }
        Then(
            "the {scene} should have {string} as the {character}'s inherited motivation"
        ) { scene: Scene, expectedMotivation: String, character: Character ->
            Assertions.assertTrue(scene.getMotivationForCharacter(character.id)!!.isInherited())

            val sceneCharactersTool = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenEditing(character)

            SceneCharactersAssertions.assertThat(sceneCharactersTool) {
                andCharacter(character.id) {
                    hasInheritedMotivationValue(expectedMotivation)
                }
            }
        }
        Then(
            "all of the {character}'s arc sections that are covered in the {scene} should indicate they have been covered"
        ) { character: Character, scene: Scene ->
            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneCharactersAssertions.assertThat(sceneCharacters) {
                andCharacter(character.id) {
                    scene.getCoveredCharacterArcSectionsForCharacter(character.id)!!.forEach {
                        isListingAvailableArcSectionToCover(sectionId = it)
                    }
                }
            }
        }
        Then(
            "all of the {character}'s arc sections that have not yet been covered in the {scene} should be listed"
        ) { character: Character, scene: Scene ->
            val arcs = CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()).getCharacterArcsForCharacter(character)
            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneCharactersAssertions.assertThat(sceneCharacters) {
                andCharacter(character.id) {
                    arcs.forEach { arc ->
                        arc.arcSections.filterNot { scene.coveredArcSectionIds.contains(it.id) }.forEach {
                            isListingAvailableArcSectionToCover(arc.id, it.id, it.template.name)
                        }
                    }
                }
            }
        }
        Then(
            "all of the {character}'s arc sections that have been covered in the {scene} should be listed"
        ) { character: Character, scene: Scene ->
            val arcs = CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()).getCharacterArcsForCharacter(character)
            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneCharactersAssertions.assertThat(sceneCharacters) {
                andCharacter(character.id) {
                    arcs.forEach { arc ->
                        arc.arcSections.filter { scene.coveredArcSectionIds.contains(it.id) }.forEach {
                            isListingAvailableArcSectionToCover(arc.id, it.id, it.template.name)
                        }
                    }
                }
            }
        }
        Then(
            "all of the {character}'s arcs and all their sections should be listed to cover in the {scene}"
        ) { character: Character, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val allArcs = CharacterDriver(workbench)
                .getCharacterArcsForCharacter(character)

            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneCharactersAssertions.assertThat(sceneCharacters) {
                andCharacter(character.id) {
                    allArcs.forEach { arc ->
                        isListingAvailableArcToCover(arc.id, arc.name)
                        arc.arcSections.forEach {
                            isListingAvailableArcSectionToCover(arc.id, it.id, it.template.name)
                        }
                    }
                }
            }
        }
        Then(
            "the {scene} should cover the {string} section from the {character}'s {string} character arc"
        ) { scene: Scene, sectionName: String, character: Character, arcName: String ->
            val arc = CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()).getCharacterArcByNameOrError(character, arcName)
            val section = arc.arcSections.find { it.template.name == sectionName }!!
            assertTrue(scene.coveredArcSectionIds.contains(section.id))
        }
        Then(
            "the {scene} should not cover the {string} section from the {character}'s {string} character arc"
        ) { scene: Scene, sectionName: String, character: Character, arcName: String ->
            val arc = CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()).getCharacterArcByNameOrError(character, arcName)
            val section = arc.arcSections.find { it.template.name == sectionName }!!
            Assertions.assertFalse(scene.coveredArcSectionIds.contains(section.id))
        }
        Then(
            "the {character}'s motivation in the {scene} should be {string}"
        ) { character: Character, scene: Scene, expectedMotivation: String ->
            assertEquals(expectedMotivation, scene.getMotivationForCharacter(character.id)!!.motivation)
            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneCharactersAssertions.assertThat(sceneCharacters) {
                andCharacter(character.id) {
                    hasMotivationValue(expectedMotivation)
                }
            }
        }
        Then(
            "the {character}'s inherited motivation in the {scene} should be {string}"
        ) { character: Character, scene: Scene, expectedInheritedMotivation: String ->
            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneCharactersAssertions.assertThat(sceneCharacters) {
                andCharacter(character.id) {
                    hasInheritedMotivationValue(expectedInheritedMotivation)
                }
            }
        }
    }

}