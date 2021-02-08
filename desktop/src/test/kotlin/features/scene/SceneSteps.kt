package com.soyle.stories.desktop.config.features.scene

import com.soyle.stories.desktop.config.drivers.character.CharacterDriver
import com.soyle.stories.desktop.config.drivers.character.createCharacterWithName
import com.soyle.stories.desktop.config.drivers.character.createSectionForTemplate
import com.soyle.stories.desktop.config.drivers.location.LocationDriver
import com.soyle.stories.desktop.config.drivers.location.createLocationWithName
import com.soyle.stories.desktop.config.drivers.prose.*
import com.soyle.stories.desktop.config.drivers.scene.*
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.getParagraphs
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.sceneDetails.SceneDetailsAssertions
import com.soyle.stories.desktop.view.scene.sceneEditor.SceneEditorAssertions
import com.soyle.stories.desktop.view.scene.sceneList.SceneListAssert
import com.soyle.stories.desktop.view.scene.sceneList.SceneListAssert.Companion.assertThat
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.validation.anyNewLineCharacter
import com.soyle.stories.scene.sceneList.SceneListView
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import javafx.scene.input.KeyCode
import org.junit.jupiter.api.Assertions.*

class SceneSteps : En {

    init {
        givens()
        whens()
        thens()
        createSceneSteps()
        renameSceneSteps()
        deleteSceneSteps()

        charactersInSceneSteps()
        locationsUsedInSceneSteps()
    }

    private val sceneDriver: SceneDriver
        get() = SceneDriver.invoke(soyleStories.getAnyOpenWorkbenchOrError())

    private val sceneListView: SceneListView
        get() = soyleStories.getAnyOpenWorkbenchOrError()
            .givenSceneListToolHasBeenOpened()

    private fun createSceneSteps() {
        Given("I have created a scene named {string}") { sceneName: String ->
            sceneDriver.givenScene(sceneName)
        }
        Given("I have created the following scenes") { dataTable: DataTable ->
            val driver = sceneDriver
            dataTable.asList().forEach(driver::givenScene)
        }
        // whens
        When("I create a scene named {string}") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.openCreateSceneDialog()
                .createSceneWithName(sceneName)
        }
        // thens
        Then("a scene named {string} should have been created") { sceneName: String ->
            SceneAssertions.assertSceneExistsWithName(sceneName)

            assertThat(sceneListView) {
                hasSceneNamed(sceneName)
            }
        }
    }

    private fun renameSceneSteps() {
        When("I rename the {scene} to {string}") { scene: Scene, newName: String ->
            sceneListView.renameSceneTo(scene, newName)
        }
        // thens
        Then(
            "the scene originally named {string} should have been renamed to {string}"
        ) { originalName: String, newName: String ->
            val (scene) = sceneDriver.getScenesAtOnePointNamed(originalName)
            assertEquals(newName, scene.name.value)

            assertThat(sceneListView) {
                doesNotHaveSceneNamed(originalName)
                hasSceneNamed(newName)
            }
        }
    }

    private fun deleteSceneSteps() {
        Given("I am deleting the {scene}") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenDeleteSceneDialogHasBeenOpened(scene)
        }
        // whens
        When("I want to delete the {scene}") { scene: Scene ->
            sceneListView.deleteScene(scene)
        }
        When("I confirm I want to delete the {scene}") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenDeleteSceneDialogHasBeenOpened(scene)
                .confirmDelete()
        }
        When("I delete the {scene}") { scene: Scene ->
            sceneListView.openDeleteSceneDialog(scene)
            soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenDeleteSceneDialog(scene)
                ?.confirmDelete()
        }
        // thens
        Then("I should be prompted to confirm deleting the {scene}") { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .getOpenDeleteSceneDialogOrError(scene)
        }
        Then("the {string} scene should not have been deleted") { sceneName: String ->
            sceneDriver.getSceneByNameOrError(sceneName)
        }
        Then("the {string} scene should have been deleted") { sceneName: String ->
            assertNull(sceneDriver.getSceneByName(sceneName))
        }
    }

    private fun charactersInSceneSteps() {
        val characterDriver by lazy { CharacterDriver(soyleStories.getAnyOpenWorkbenchOrError()) }
        Given("I have included the {character} in the {scene}") { character: Character, scene: Scene ->
            sceneDriver.givenCharacterIncludedInScene(scene, character)
        }
        Given("I have included the following characters in the {scene}") { scene: Scene, dataTable: DataTable ->
            val sceneDriver = sceneDriver
            dataTable.asList().forEach {
                val character = characterDriver.getCharacterByNameOrError(it)
                sceneDriver.givenCharacterIncludedInScene(scene, character)
            }
        }
        Given("I have set the following motivations in the following scenes for the following characters") { dataTable: DataTable ->
            val sceneDriver = sceneDriver
            val dataLists = dataTable.asLists()
            val scenes = dataLists[0].drop(1).map(sceneDriver::givenScene)
            dataLists.drop(1).forEach {
                val character = characterDriver.givenCharacterNamed(NonBlankString.create(it.first())!!)
                it.drop(1).forEachIndexed { index, s ->
                    if (s == "-") return@forEach
                    val scene = scenes[index]
                    if (s != "inherit") {
                        sceneDriver.givenCharacterIncludedInScene(scene, character, s)
                    } else {
                        sceneDriver.givenCharacterIncludedInScene(scene, character)
                    }
                }
            }
        }
        Given(
            "I have covered the following character arc sections in the {scene} for the {character}"
        ) { scene: Scene, character: Character, dataTable: DataTable ->
        }
        Given(
            "I have covered some character arc sections for the {character} in the {scene}"
        ) { character: Character, scene: Scene ->
            val sceneDriver = sceneDriver
            val sections = characterDriver
                .getCharacterArcsForCharacter(character)
                .flatMap { it.arcSections }
                .shuffled()
            sections.take(sections.size / 2).forEach {
                sceneDriver.givenSceneCoversArcSection(scene, it)
            }
        }
        Given(
            "I have requested which arc sections for the {character} can be covered in the {scene}"
        ) { character: Character, scene: Scene ->
            sceneListView
                .givenSceneDetailsToolHasBeenOpened(scene)
                .givenPositionOnArcInputForCharacterHasBeenSelected(character)
        }
        Given(
            "I have requested which arc sections for the {character} can be uncovered in the {scene}"
        ) { character: Character, scene: Scene ->
            sceneListView
                .givenSceneDetailsToolHasBeenOpened(scene)
                .givenPositionOnArcInputForCharacterHasBeenSelected(character)
        }


        When(
            "I request which arc sections for the {character} can be covered in the {scene}"
        ) { character: Character, scene: Scene ->
            sceneListView
                .givenSceneDetailsToolHasBeenOpened(scene)
                .selectPositionOnArcInputForCharacter(character)
        }
        When(
            "I request which arc sections for the {character} can be uncovered in the {scene}"
        ) { character: Character, scene: Scene ->
            sceneListView
                .givenSceneDetailsToolHasBeenOpened(scene)
                .selectPositionOnArcInputForCharacter(character)
        }
        When(
            "I cover the {string} section from the {character}'s {string} character arc in the {scene}"
        ) { sectionName: String, character: Character, arcName: String, scene: Scene ->
            sceneListView
                .givenSceneDetailsToolHasBeenOpened(scene)
                .givenPositionOnArcInputForCharacterHasBeenSelected(character)
                .coverSectionInArc(arcName, sectionName)
        }
        When(
            "I uncover the {string} section from the {character}'s {string} character arc in the {scene}"
        ) { sectionName: String, character: Character, arcName: String, scene: Scene ->
            sceneListView
                .givenSceneDetailsToolHasBeenOpened(scene)
                .givenPositionOnArcInputForCharacterHasBeenSelected(character)
                .uncoverSectionInArc(arcName, sectionName)
        }
        When(
            "I create a new {string} arc section in the {character}'s {string} arc to cover in the {scene}"
        ) { sectionName: String, character: Character, arcName: String, scene: Scene ->
            sceneListView
                .givenSceneDetailsToolHasBeenOpened(scene)
                .givenPositionOnArcInputForCharacterHasBeenSelected(character)
                .givenCreateNewSectionInArcSelected(arcName)
                .createSectionForTemplate(sectionName)
        }


        Then(
            "the {scene} should not have a motivation for the {character} anymore"
        ) { scene: Scene, character: Character ->
            assertTrue(scene.getMotivationForCharacter(character.id)!!.isInherited())

            val sceneDetailsTool = sceneListView
                .givenSceneDetailsToolHasBeenOpened(scene)
            SceneDetailsAssertions.assertThat(sceneDetailsTool) {
                andCharacter(character.id.uuid.toString()) {
                    hasMotivationValue("")
                }
            }
        }
        Then(
            "the {scene} should have {string} as the {character}'s inherited motivation"
        ) { scene: Scene, expectedMotivation: String, character: Character ->
            assertTrue(scene.getMotivationForCharacter(character.id)!!.isInherited())

            val sceneDetailsTool = sceneListView
                .givenSceneDetailsToolHasBeenOpened(scene)

            SceneDetailsAssertions.assertThat(sceneDetailsTool) {
                andCharacter(character.id.uuid.toString()) {
                    hasInheritedMotivationValue(expectedMotivation)
                }
            }
        }
        Then(
            "all of the {character}'s arc sections that are covered in the {scene} should indicate they have been covered"
        ) { character: Character, scene: Scene ->
            val sceneDetails = sceneListView
                .givenSceneDetailsToolHasBeenOpened(scene)
            SceneDetailsAssertions.assertThat(sceneDetails) {
                andCharacter(character.id.uuid.toString()) {
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
            val sceneDetails = sceneListView
                .givenSceneDetailsToolHasBeenOpened(scene)
            SceneDetailsAssertions.assertThat(sceneDetails) {
                andCharacter(character.id.uuid.toString()) {
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
            val sceneDetails = sceneListView
                .givenSceneDetailsToolHasBeenOpened(scene)
            SceneDetailsAssertions.assertThat(sceneDetails) {
                andCharacter(character.id.uuid.toString()) {
                    arcs.forEach { arc ->
                        arc.arcSections.filter { scene.coveredArcSectionIds.contains(it.id) }.forEach {
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
            assertFalse(scene.coveredArcSectionIds.contains(section.id))
        }
    }

    private fun locationsUsedInSceneSteps() {
        val locationDriver by lazy { LocationDriver(soyleStories.getAnyOpenWorkbenchOrError()) }
        Given("I have used the following locations in the {scene}") { scene: Scene, dataTable: DataTable ->
            val sceneDriver = sceneDriver
            dataTable.asList().forEach {
                val location = locationDriver.getLocationByNameOrError(it)
                sceneDriver.givenLocationUsedInScene(scene, location)
            }
        }
    }

    private fun givens() {
        //region scene prose
        Given("my {scene} has {int} paragraphs of text in its prose") { scene: Scene, paragraphCount: Int ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            SceneDriver(workbench).givenSceneHasProse(scene, getParagraphs(paragraphCount))
        }
        Given("I have mentioned the {character} in the {scene}'s prose") { character: Character, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            SceneDriver(workbench).givenSceneHasProse(scene, listOf(character.name.value))
            SceneDriver(workbench).givenSceneProseMentionsEntity(
                scene,
                character.id.mentioned(),
                0,
                character.name.length
            )
        }
        Given("I have mentioned the {location} in the {scene}'s prose") { location: Location, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            SceneDriver(workbench).givenSceneHasProse(scene, listOf(location.name.value))
            SceneDriver(workbench).givenSceneProseMentionsEntity(scene, location.id.mentioned(), 0, location.name.length)
        }
        Given("the user has wanted to edit the {scene}") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
        }
        Given("I am editing the {scene}'s prose") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
        }
        Given(
            "I have requested story elements that match {string} for the {scene}"
        ) { query: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .givenStoryElementsQueried(query)
        }
        //endregion

        Given(
            "I am covering character arc sections for the {character} in the {scene}"
        ) { character: Character, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneDetailsToolHasBeenOpened(scene)
                .givenPositionOnArcInputForCharacterHasBeenSelected(character)
        }
        Given(
            "I am investigating the {string} mention in the {scene}'s prose"
        ) { mentionText: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .givenMentionIsBeingInvestigated(mentionText)
        }
        Given(
            "I have mentioned the {string} symbol from the {theme} in the {scene}'s prose"
        ) { symbolName: String, theme: Theme, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val symbol = theme.symbols.find { it.name == symbolName }!!
            SceneDriver(workbench).givenSceneHasProse(scene, listOf(symbolName))
            SceneDriver(workbench).givenSceneProseMentionsEntity(
                scene,
                symbol.id.mentioned(theme.id),
                0,
                symbolName.length
            )

        }
    }

    private fun whens() {
        When("I read the {scene}'s prose") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .openSceneEditorTool(scene)
        }
        When("I edit the {scene}'s prose") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .openSceneEditorTool(scene)
        }
        When(
            "I enter the following text into the {scene}'s prose"
        ) { scene: Scene, data: DataTable ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .enterText(data.asList().single())
        }
        When(
            "I press the backspace key on the right of the {string} mention in the {scene}'s prose"
        ) { mentionName: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .atRightOfMention(mentionName)
                .typeKey(KeyCode.BACK_SPACE)
        }
        When(
            "I press the delete key on the left of the {string} mention in the {scene}'s prose"
        ) { mentionName: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .atLeftOfMention(mentionName)
                .typeKey(KeyCode.DELETE)
        }
        When(
            "I request story elements that match {string} for the {scene}"
        ) { query: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .query(query)
        }
        When(
            "I select {string} from the list of matching story elements for the {scene}"
        ) { elementName: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .selectMentionSuggestion(elementName)
        }
        When(
            "I select {string} from the list of matching story elements to include in the {scene}"
        ) { elementName: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .selectMentionSuggestionAndUse(elementName)
        }
        When(
            "I select {string} from the list of matching story elements to use in the {scene}"
        ) { elementName: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .selectMentionSuggestionAndUse(elementName)
        }
        When(
            "I investigate the {string} mention in the {scene}'s prose"
        ) { mentionText: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .investigateMention(mentionText)
        }
        When(
            "I clear the {string} mention from the {scene}'s prose"
        ) { mentionText: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .givenMentionIsBeingInvestigated(mentionText)
                .clearAllMentionsOfEntity()
        }
        When(
            "I remove the {string} mention from the {scene}'s prose"
        ) { mentionText: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .run {
                    investigateMention(mentionText)?.removeMention()
                        ?: atRightOfMention(mentionText).typeKey(KeyCode.BACK_SPACE)
                }
        }
        When(
            "I select {string} to replace the {string} mention in the {scene}'s prose"
        ) { replacementText: String, mentionText: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .givenMentionIsBeingInvestigated(mentionText)
                .selectReplacementSuggestion(replacementText)
        }
        When(
            "I create a character named {string} to replace the {string} mention in the {scene}'s prose"
        ) { newCharacterName: String, mentionText: String, scene: Scene ->
            sceneListView
                .givenSceneEditorToolHasBeenOpened(scene)
                .givenMentionIsBeingInvestigated(mentionText)
                .givenReplacingInvestigatedMentionWithNewCharacter()
                .createCharacterWithName(newCharacterName)
        }
        When(
            "I create a location named {string} to replace the {string} mention in the {scene}'s prose"
        ) { newLocationName: String, mentionText: String, scene: Scene ->
            sceneListView
                .givenSceneEditorToolHasBeenOpened(scene)
                .givenMentionIsBeingInvestigated(mentionText)
                .givenReplacingInvestigatedMentionWithNewLocation()
                .createLocationWithName(newLocationName)
        }
    }

    private fun thens() {
        Then(
            "I should see all {int} paragraphs of the {scene}'s prose"
        ) { paragraphCount: Int, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)

            val proseContent = ProseDriver(workbench).getProseByIdOrError(scene.proseId).content
            assertEquals(paragraphCount, proseContent.split(anyNewLineCharacter).size)

            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    hasContent(proseContent)
                }
            }
        }
        Then(
            "I should see the {character} mentioned in the {scene}'s prose"
        ) { character: Character, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)

            val mention = ProseDriver(workbench)
                .getProseByIdOrError(scene.proseId)
                .getMentionByEntityIdOrError(character.id)

            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    hasMention(mention.entityId, mention.position)
                }
            }
        }
        Then(
            "I should see the {location} mentioned in the {scene}'s prose"
        ) { location: Location, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)

            val mention = ProseDriver(workbench)
                .getProseByIdOrError(scene.proseId)
                .getMentionByEntityIdOrError(location.id)

            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    hasMention(mention.entityId, mention.position)
                }
            }
        }
        Then(
            "the {scene} scene's prose should show the location {location}'s name as a reference"
        ) { scene: Scene, location: Location ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)

            val locationMention = ProseDriver(workbench).getProseByIdOrError(scene.proseId).mentions
                .find { it.entityId.id == location.id }
                ?: throw AssertionError("No mention in prose for ${location.name}")

            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    hasMention(locationMention.entityId, locationMention.position)
                }
            }
        }
        Then(
            "the following story elements should be listed for the {scene} scene and marked as their element type"
        ) { scene: Scene, dataTable: DataTable ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)

            dataTable.asLists().drop(1)
                .forEachIndexed { index, (expectedName, expectedTypeLabel) ->
                    SceneEditorAssertions.assertThat(sceneEditor) {
                        andProseEditor {
                            isListingStoryElement(index, expectedName, expectedTypeLabel)
                        }
                    }
                }
        }
        Then(
            "no story elements should be listed for the {scene} scene"
        ) { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)

            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    suggestedMentionListIsNotVisible()
                }
            }
        }
        Then(
            "the {scene}'s prose should have the following text"
        ) { scene: Scene, dataTable: DataTable ->
            val expectedText = dataTable.asList().single()
            ProseAssertions.proseTextIs(scene.proseId, expectedText)

            val sceneEditor = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)

            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    hasContent(expectedText)
                }
            }
        }
        Then(
            "I should see {string} mentioned in the {scene}'s prose"
        ) { mentionName: String, scene: Scene ->
            ProseAssertions.proseDoesContainMention(scene.proseId, mentionName)
        }
        Then(
            "the {string} mention should not be in the {scene}'s prose"
        ) { mentionName: String, scene: Scene ->
            ProseAssertions.proseDoesNotContainMention(scene.proseId, mentionName)
        }
        Then(
            "the text previously covered by the {string} mention in the {scene}'s prose should be removed"
        ) { mentionText: String, scene: Scene ->
            ProseAssertions.proseDoesNotContainText(scene.proseId, mentionText)
        }
        Then("I should not see any matching story elements for the {scene}") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)

            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    suggestedMentionListIsNotVisible()
                }
            }
        }
        Then(
            "I should see the following matching story elements for the {scene} in this order"
        ) { scene: Scene, dataTable: DataTable ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)

            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    suggestedMentionListIsVisible()
                    isListingAllStoryElementsInOrder(dataTable.asLists().drop(1).map { it.component1() to it.component2() })
                }
            }
        }
        Then(
            "the {character} should be included in the {scene}"
        ) { character: Character, scene: Scene ->
            assertTrue(scene.includesCharacter(character.id))
        }
        Then(
            "the {location} should be used in the {scene}"
        ) { location: Location, scene: Scene ->
            assertTrue(scene.settings.contains(location.id))
        }
        Then(
            "the {string} mention in the {scene}'s prose should read {string}"
        ) { previousMentionText: String, scene: Scene, newText: String ->
            ProseAssertions.proseDoesNotContainMention(scene.proseId, previousMentionText)
            ProseAssertions.proseDoesContainMention(scene.proseId, newText)
            val workBench = soyleStories.getAnyOpenWorkbenchOrError()
            val prose = ProseDriver(workBench).getProseByIdOrError(scene.proseId)

            val sceneEditor = workBench
                .givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    hasContent(prose.content)
                    prose.mentions.forEach {
                        hasMention(it.entityId, it.position)
                    }
                }
            }
        }
        Then("the {scene} should not indicate that it has an issue") { scene: Scene ->
            val workBench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneList = workBench
                .givenSceneListToolHasBeenOpened()
            SceneListAssert.assertThat(sceneList) {
                doesNotIndicateSceneHasAnIssue(scene.name.value)
            }
        }
        Then("the {scene} should indicate that it has an issue") { scene: Scene ->
            val workBench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneList = workBench
                .givenSceneListToolHasBeenOpened()
            assertThat(sceneList) {
                indicatesSceneHasAnIssue(scene.name.value)
            }
        }
        Then(
            "the {string} mention in the {scene}'s prose should indicate that it was removed"
        ) { mentionText: String, scene: Scene ->
            val workBench = soyleStories.getAnyOpenWorkbenchOrError()
            val mention = ProseDriver(workBench).getProseByIdOrError(scene.proseId)
                .getMentionByText(mentionText)!!
            val sceneEditor = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    hasIssueWithMention(mention.entityId, mention.position)
                }
            }
        }
        Then(
            "all of the {character}'s arcs and all their sections should be listed to cover in the {scene}"
        ) { character: Character, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val allArcs = CharacterDriver(workbench)
                .getCharacterArcsForCharacter(character)

            val sceneDetails = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneDetailsToolHasBeenOpened(scene)
            SceneDetailsAssertions.assertThat(sceneDetails) {
                andCharacter(character.id.uuid.toString()) {
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
            "I should be able to clear the {string} mention in the {scene}'s prose"
        ) { mentionText: String, scene: Scene ->

            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    isShowingMentionIssueMenuForMention(mentionText)
                    mentionIssueMenuHasOption("Clear all Mentions of $mentionText and Use Normal Text")
                }
            }
        }
        Then(
            "I should be able to remove the {string} mention from the {scene}'s prose"
        ) { mentionText: String, scene: Scene ->

            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    isShowingMentionIssueMenuForMention(mentionText)
                    mentionIssueMenuHasOption("Remove this Mention of $mentionText and Remove the Text")
                }
            }
        }
        Then(
            "the {scene}'s prose should still contain text for {string}"
        ) { scene: Scene, expectedText: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            assertTrue(ProseDriver(workbench).getProseByIdOrError(scene.proseId).content.contains(expectedText))
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    containsContent(expectedText)
                }
            }
        }
        Then(
            "the {scene}'s prose should not contain text for {string}"
        ) { scene: Scene, expectedText: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            assertFalse(ProseDriver(workbench).getProseByIdOrError(scene.proseId).content.contains(expectedText))
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    doesNotContainContent(expectedText)
                }
            }
        }
        Then(
            "I should not see any listed elements with which to replace {string} in the {scene}'s prose"
        ) { mentionText: String, scene: Scene ->
            val sceneEditor = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    doesNotHaveAnyReplacementMentionElementsListed()
                }
            }
        }
        Then(
            "the {string} mention in the {scene}'s prose should have been replaced with {string}"
        ) { previousMentionText: String, scene: Scene, expectedMentionText: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val proseContent = ProseDriver(workbench).getProseByIdOrError(scene.proseId).content
            assertFalse(proseContent.contains(previousMentionText)) { "Prose content contains $previousMentionText" }
            assertTrue(proseContent.contains(expectedMentionText)) { "Prose content does not contain $expectedMentionText" }
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    doesNotContainContent(previousMentionText)
                    containsContent(expectedMentionText)
                }
            }
        }
        Then(
            "I should be able to create a new character to replace the {string} mention in the {scene}'s prose"
        ) { mentionText: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    isShowingMentionIssueMenuForMention(mentionText)
                    mentionIssueReplacementMenuHasOption("Create New Character")
                }
            }
        }
        Then(
            "I should be able to create a new location to replace the {string} mention in the {scene}'s prose"
        ) { mentionText: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val sceneEditor = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    isShowingMentionIssueMenuForMention(mentionText)
                    mentionIssueReplacementMenuHasOption("Create New Location")
                }
            }
        }
        Then(
            "the suggested elements with which to replace {string} in the {scene}'s prose should be as follows"
        ) { mentionText: String, scene: Scene, dataTable: DataTable ->
            val sceneEditor = sceneListView
                .givenSceneEditorToolHasBeenOpened(scene)
                .givenMentionIsBeingInvestigated(mentionText)
            SceneEditorAssertions.assertThat(sceneEditor) {
                andProseEditor {
                    isShowingMentionIssueMenuForMention(mentionText)
                    isListingAllReplacementOptionsInOrder(dataTable.asList())
                }
            }
        }
    }

}