package com.soyle.stories.desktop.config.features.scene


import com.soyle.stories.desktop.config.drivers.character.CharacterDriver
import com.soyle.stories.desktop.config.drivers.character.createCharacterWithName
import com.soyle.stories.desktop.config.drivers.character.createSectionForTemplate
import com.soyle.stories.desktop.config.drivers.character.getCreateCharacterDialogOrError
import com.soyle.stories.desktop.config.drivers.ramifications.confirmation.confirm
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.scene.*
import com.soyle.stories.desktop.config.drivers.scene.character.*
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.storyevent.`Story Event Robot`
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.sceneCharacters.SceneCharactersAssertions
import com.soyle.stories.desktop.view.scene.sceneCharacters.SceneCharactersAssertions.Companion.assertThat
import com.soyle.stories.desktop.view.scene.sceneCharacters.SceneCharactersAssertions.IncludedCharacterAssertions.Companion.andCharacter
import com.soyle.stories.desktop.view.scene.sceneCharacters.`Scene Characters Access`.Companion.access
import com.soyle.stories.desktop.view.scene.sceneCharacters.inspect.assertThat
import com.soyle.stories.desktop.view.scene.sceneCharacters.selectStoryEvent.assertThat
import com.soyle.stories.desktop.view.scene.sceneCharacters.selectStoryEvent.drive
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.scene.characters.tool.SceneCharactersToolViewModel
import com.soyle.stories.scene.sceneList.SceneListView
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
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
        Given("I am tracking the {scene}'s characters") { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
        }
        Given("I am attempting to include a character in the {scene}") { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenIncludingCharacter()
        }
        Given("I have chosen to create a new character to include in the {scene}") { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenIncludingCharacter()
                .givenCreatingNewCharacter()
        }
        Given(
            "I have chosen the {character} to include in the {scene}"
        ) { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenIncludingCharacter()
                .givenCharacterChosen(character)
        }
        Given("I am removing the {character} from the {scene}") { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenRemovingCharacter(character)
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
            "I have explicitly included the {character} in the {scene}"
        ) { character: Character, scene: Scene ->
            if (scene.includesCharacter(character.id)) return@Given
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenIncludingCharacter()
                .chooseCharacter(character)
        }
        Given(
            "I am removing the {story event} from the {scene} outline"
        ) { storyEvent: StoryEvent, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneOutlineToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenConfirmRemoveStoryEventFromSceneDialogHasBeenOpened(storyEvent.id)
        }
        Given(
            "I have removed the {character} from the {scene}"
        ) { character: Character, scene: Scene ->
            sceneDriver.givenSceneDoesNotIncludeCharacter(scene, character)
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
            sceneDriver.givenCharacterHasMotivationInScene(scene, character, motivation)
        }
        Given(
            "I have assigned the {character} the {string} role in the {scene}"
        ) { character: Character, role: String, scene: Scene ->
            sceneDriver.givenCharacterHasRole(scene, character, role)
        }
        Given(
            "I have set the {character}'s desire to {string} in the {scene}"
        ) { character: Character, desire: String, scene: Scene ->
            sceneDriver.givenCharacterHasDesire(scene, character, desire)
        }
    }

    private fun whens() {
        When("I attempt to include a character in the {scene}") { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .includeCharacter()
        }
        When(
            "I choose the {character} from the list of characters in the {story event} to include in the {scene}"
        ) { character: Character, storyEvent: StoryEvent, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenIncludingCharacter()
                .givenStoryEventSelected(storyEvent)
                .chooseCharacter(character)
        }
        When(
            "I create a character named {string} to include in the {scene}"
        ) { characterName: String, scene: Scene ->
            getCreateCharacterDialogOrError()
                .createCharacterWithName(characterName)
        }
        When("I choose the {character} to include in the {scene}") { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenSceneCharactersToolOrError()
                .chooseCharacter(character)
        }
        When(
            "I choose to involve the {character} from the {scene} in the following story events"
        ) { character: Character, scene: Scene, data: DataTable ->
            val storyEventOptions = data.asLists()

            soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenSceneCharactersToolOrError(scene)
                .getOpenStoryEventPromptOrError(character)
                .drive {
                    storyEventOptions.forEach {
                        if (it.size == 1) // just selecting story events by name
                        {
                            storyEventItemNamed(it.single())!!.isSelected = true
                        } else {
                            assert(it.size == 3) { "Expecting 3 cells per line" }
                            val (isNew, name, time) = it
                            if (isNew.isNullOrBlank()) {
                                storyEventItemNamed(name)!!.isSelected = true
                            } else {
                                creatingStoryEventToggle.isSelected = true
                                newNameField.text = name
                                if (time != null && time.isNotBlank()) {
                                    val timeVal = time.toInt()
                                    newTimeField.text = timeVal.toString()
                                }
                            }
                        }
                    }
                    doneButton.fire()
                }
        }
        When("I explicitly include the {character} in the {scene}") { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenIncludingCharacter()
                .chooseCharacter(character)
        }
        When("I want to remove the {character} from the {scene}") { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .openConfirmRemoveCharacterFromScenePrompt(character.id)
        }
        When("I confirm that I want to remove the {character} from the {scene}") { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenConfirmRemoveCharacterFromScenePromptHasBeenOpened(character)
                .confirm()
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
            val charactersInSceneTool = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenInspecting(character.id)

            runBlocking {
                charactersInSceneTool.setMotivationAs(motivation)
            }
        }
        When(
            "I assign the {character} the {string} role in the {scene}"
        ) { character: Character, role: String, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenInspecting(character.id)
                .assignRole(role)
        }
        When(
            "I check the roles of the included characters in the {scene}"
        ) { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
        }
        When(
            "I check the {character}'s desire in the {scene}"
        ) { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenInspecting(character.id)
        }
        When(
            "I set the {character}'s desire to {string} in the {scene}"
        ) { character: Character, desire: String, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenInspecting(character.id)
                .setDesireAs(desire)
        }
    }

    private fun thens() {
        Then(
            "the {character} should be listed to include in the {scene}"
        ) { character: Character, scene: Scene ->

            val charactersInSceneTool = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenSceneCharactersToolOrError()
            assertThat(charactersInSceneTool) {
                hasCharacterToInclude(character)
            }
        }
        Then("no characters should be listed to include in the {scene}") { scene: Scene ->
            val charactersInSceneTool = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenSceneCharactersToolOrError()

            assertThat(charactersInSceneTool) {
                hasNoCharactersToInclude()
            }
        }
        Then("the {character} should be explicitly included in the {scene}") { character: Character, scene: Scene ->
            assertTrue(scene.includesCharacter(character.id)) { "Scene does not include character" }

            val charactersInSceneTool = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            assertThat(charactersInSceneTool) {
                hasCharacter(character)
            }
        }
        Then(
            "I should be prompted to involve the {character} in story events covered by the {scene}"
        ) { character: Character, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenSceneCharactersToolOrError()
                .getOpenStoryEventPromptOrError()
        }
        Then(
            "no story events should be listed to involve the {character} from the {scene}"
        ) { character: Character, scene: Scene ->
            val storyEventPrompt = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenSceneCharactersToolOrError()
                .getOpenStoryEventPromptOrError()

            assertThat(storyEventPrompt) {
                hasNoStoryEvents()
            }
        }
        Then(
            "the following story events should be listed to involve the {character} from the {scene}"
        ) { character: Character, scene: Scene, data: DataTable ->
            val expectedNames = data.asList()
            val storyEventPrompt = soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenSceneCharactersToolOrError()
                .getOpenStoryEventPromptOrError()

            assertThat(storyEventPrompt) {
                hasStoryEventsNamed(expectedNames)
            }
        }
        Then("the {character} should( still) be implicitly included in the {scene}") { character: Character, scene: Scene ->
            val charactersInSceneTool = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            assertThat(charactersInSceneTool) {
                hasCharacter(character)
            }
        }
        Then("the {character} should not be implicitly included in the {scene}") { character: Character, scene: Scene ->
            val charactersInSceneTool = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            assertThat(charactersInSceneTool) {
                doesNotHaveCharacter(character)
            }
        }
        Then("the {character} should not be explicitly included in the {scene}") { character: Character, scene: Scene ->
            assertFalse(scene.includesCharacter(character.id)) { "Scene includes character" }
        }
        Then(
            "the {scene} should show a warning for the {string} character"
        ) { scene: Scene, characterName: String ->
            val charactersInSceneTool = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            assertThat(charactersInSceneTool) {
                hasCharacterNamed(characterName)
                andCharacter(characterName) {
                    hasWarning()
                }
            }
        }
        Then(
            "the {scene} should not have a character named {string}"
        ) { scene: Scene, characterName: String ->
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

            assertNotEquals(true, scene.getMotivationForCharacter(character.id)?.isInherited())

            val characterInSceneInspection = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenInspecting(character.id)

            assertThat(characterInSceneInspection) {
                hasMotivationValue("")
            }
        }
        Then(
            "the {scene} should have {string} as the {character}'s inherited motivation"
        ) { scene: Scene, expectedMotivation: String, character: Character ->
            if (scene.includesCharacter(character.id)) {
                assertTrue(scene.getMotivationForCharacter(character.id)!!.isInherited())
            }

            val characterInSceneInspection = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenInspecting(character.id)

            assertThat(characterInSceneInspection) {
                hasInheritedMotivationValue(expectedMotivation)
            }
        }
        Then(
            "I should be warned that the following story events still involve the {character}"
        ) { character: Character, data: DataTable ->
            val storyEventNames = data.asList().toList()

            val sceneCharactersTool = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()

            val prompt = getOpenConfirmRemoveCharacterFromScenePromptOrError(
                (sceneCharactersTool.viewModel.sceneSelection.value as SceneCharactersToolViewModel.SceneSelection.Selected).sceneName.value,
                character.displayName.value
            )
            val storyEvents = `Story Event Robot`(soyleStories.getAnyOpenWorkbenchOrError())
            storyEventNames.map { storyEvents.getStoryEventByName(it)!! }
                .forEach { storyEvent ->
                    prompt.viewModel.items.single { it.storyEventId == storyEvent.id }
                    robot.from(prompt.content).lookup("#${storyEvent.id}").queryLabeled()
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
            val arcs =
                CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()).getCharacterArcsForCharacter(character)
            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneCharactersAssertions.assertThat(sceneCharacters) {
                andCharacter(character.id) {
                    arcs.forEach { arc ->
                        arc.arcSections.filterNot { scene.isCharacterArcSectionCovered(it.id) }.forEach {
                            isListingAvailableArcSectionToCover(arc.id, it.id, it.template.name)
                        }
                    }
                }
            }
        }
        Then(
            "all of the {character}'s arc sections that have been covered in the {scene} should be listed"
        ) { character: Character, scene: Scene ->
            val arcs =
                CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()).getCharacterArcsForCharacter(character)
            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneCharactersAssertions.assertThat(sceneCharacters) {
                andCharacter(character.id) {
                    arcs.forEach { arc ->
                        arc.arcSections.filter { scene.isCharacterArcSectionCovered(it.id) }.forEach {
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
            val arc = CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()).getCharacterArcByNameOrError(
                character,
                arcName
            )
            val section = arc.arcSections.find { it.template.name == sectionName }!!
            assertTrue(scene.isCharacterArcSectionCovered(section.id))
        }
        Then(
            "the {scene} should not cover the {string} section from the {character}'s {string} character arc"
        ) { scene: Scene, sectionName: String, character: Character, arcName: String ->
            val arc = CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()).getCharacterArcByNameOrError(
                character,
                arcName
            )
            val section = arc.arcSections.find { it.template.name == sectionName }!!
            Assertions.assertFalse(scene.isCharacterArcSectionCovered(section.id))
        }
        Then(
            "the {character}'s motivation in the {scene} should be {string}"
        ) { character: Character, scene: Scene, expectedMotivation: String ->
            assertEquals(expectedMotivation, scene.getMotivationForCharacter(character.id)!!.motivation)
            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenInspecting(character.id)
            assertThat(sceneCharacters) {
                hasMotivationValue(expectedMotivation)
            }
        }
        Then(
            "the {character}'s inherited motivation in the {scene} should be {string}"
        ) { character: Character, scene: Scene, expectedInheritedMotivation: String ->
            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenInspecting(character.id)
            assertThat(sceneCharacters) {
                hasInheritedMotivationValue(expectedInheritedMotivation)
            }
        }
        Then(
            "the {character} should not have a role in the {scene}"
        ) { character: Character, scene: Scene ->
            assertNull(scene.includedCharacters[character.id]?.roleInScene)

            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            assertThat(sceneCharacters) {
                andCharacter(character.id) {
                    doesNotHaveRole()
                }
            }
        }
        Then(
            "the {character} should have the {string} role in the {scene}"
        ) { character: Character, role: String, scene: Scene ->
            val currentRole = scene.includedCharacters.getOrError(character.id).roleInScene
            when (role) {
                "Inciting Character" -> assertEquals(RoleInScene.IncitingCharacter, currentRole)
                else -> assertEquals(RoleInScene.OpponentCharacter, currentRole)
            }

            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            assertThat(sceneCharacters) {

                andCharacter(character.id) {
                    hasRole(
                        when (role) {
                            "Inciting Character" -> "Inciting Character"
                            else -> when (val incitingCharacter =
                                scene.includedCharacters.find { it.roleInScene == RoleInScene.IncitingCharacter }) {
                                null -> "Opponent"
                                else -> "Opponent to ${
                                    sceneCharacters.access().getCharacterItem(incitingCharacter.id)!!.name.text
                                }"
                            }
                        }
                    )
                }
            }
        }
        Then(
            "the {character} should not have the {string} role in the {scene}"
        ) { character: Character, role: String, scene: Scene ->
            val currentRole = scene.includedCharacters.getOrError(character.id).roleInScene
            when (role) {
                "Inciting Character" -> assertNotEquals(RoleInScene.IncitingCharacter, currentRole)
                else -> assertNotEquals(RoleInScene.OpponentCharacter, currentRole)
            }

            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneCharactersAssertions.assertThat(sceneCharacters) {
                andCharacter(character.id) {
                    doesNotHaveRole(
                        when (role) {
                            "Inciting Character" -> "Inciting Character"
                            else -> when (val incitingCharacter =
                                scene.includedCharacters.find { it.roleInScene == RoleInScene.IncitingCharacter }) {
                                null -> "Opponent to Inciting Character"
                                else -> "Opponent to ${
                                    sceneCharacters.access().getCharacterItem(incitingCharacter.id)!!.name.text
                                }"
                            }
                        }
                    )
                }
            }
        }
        Then(
            "the {character} should not have a desire in the {scene}"
        ) { character: Character, scene: Scene ->
            assertTrue(scene.includedCharacters[character.id]?.desire.orEmpty().isEmpty())

            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenInspecting(character.id)
            assertThat(sceneCharacters) {
                doesNotHaveDesire()
            }
        }
        Then(
            "the {character}'s desire in the {scene} should be {string}"
        ) { character: Character, scene: Scene, expectedDesire: String ->
            assertEquals(expectedDesire, scene.includedCharacters.getOrError(character.id).desire)

            val sceneCharacters = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneCharactersToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenInspecting(character.id)
            assertThat(sceneCharacters) {
                hasDesireValue(expectedDesire)
            }
        }
    }

}