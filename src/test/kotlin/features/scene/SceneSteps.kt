package com.soyle.stories.desktop.config.features.scene

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.anyNewLineCharacter
import com.soyle.stories.desktop.config.drivers.character.CharacterDriver
import com.soyle.stories.desktop.config.drivers.project.givenSettingsDialogHasBeenOpened
import com.soyle.stories.desktop.config.drivers.project.markConfirmDeleteSceneDialogUnNecessary
import com.soyle.stories.desktop.config.drivers.prose.ProseAssertions
import com.soyle.stories.desktop.config.drivers.prose.ProseDriver
import com.soyle.stories.desktop.config.drivers.prose.getMentionByEntityIdOrError
import com.soyle.stories.desktop.config.drivers.prose.getMentionByText
import com.soyle.stories.desktop.config.drivers.scene.*
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.getParagraphs
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.project.workbench.WorkbenchAssertions.Companion.assertThat
import com.soyle.stories.desktop.view.scene.sceneDetails.SceneDetailsAssertions
import com.soyle.stories.desktop.view.scene.sceneEditor.SceneEditorAssertions
import com.soyle.stories.desktop.view.scene.sceneList.SceneListAssert
import com.soyle.stories.desktop.view.scene.sceneList.SceneListAssert.Companion.assertThat
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.mentioned
import com.soyle.stories.project.WorkBench
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import javafx.scene.input.KeyCode
import org.junit.jupiter.api.Assertions.*

class SceneSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("a scene named {string} has been created") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneCreatedWithName(sceneName)
        }
        Given("I have created a scene named {string}") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneCreatedWithName(sceneName)
        }
        Given("the user has requested that a delete scene confirmation message not be shown") {
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSettingsDialogHasBeenOpened()
                .markConfirmDeleteSceneDialogUnNecessary()
        }
        Given("the user wanted to delete {scene}") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenDeleteSceneDialogHasBeenOpened(scene)
        }
        Given("the following scenes with motivations for characters") { dataTable: DataTable ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val dataLists = dataTable.asLists()
            val scenes = dataLists[0].drop(1).map { workbench.givenSceneCreatedWithName(it) }
            dataLists.drop(1).forEach {
                val character = CharacterDriver(workbench).givenCharacterNamed(NonBlankString.create(it.first())!!)
                it.drop(1).forEachIndexed { index, s ->
                    if (s == "-") return@forEach
                    val scene = scenes[index]
                    val sceneDetailsTool = workbench.givenSceneListToolHasBeenOpened()
                        .givenSceneDetailsToolHasBeenOpened(scene)
                    sceneDetailsTool.includeCharacter(character)
                    if (s != "inherit") {
                        sceneDetailsTool.setCharacterMotivation(character, s)
                    }
                }
            }
        }
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
            SceneDriver(workbench).givenSceneHasProse(scene, listOf("Paragraph", location.name.value))
            SceneDriver(workbench).givenSceneProseMentionsEntity(scene, location.id.mentioned(), 10, location.name.length)
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
    }

    private fun whens() {
        When("a scene is created with the name {string}") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.openCreateSceneDialog()
                .createSceneWithName(sceneName)
        }
        When("{scene} is renamed with the name {string}") { scene: Scene, newName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .renameSceneTo(scene, newName)
        }
        When("the user wants to delete {scene}") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .deleteScene(scene)
        }
        When("the user confirms they want to delete {scene}") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenDeleteSceneDialogHasBeenOpened(scene)
                .confirmDelete()
        }
        When("{scene} is deleted") { scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .openDeleteSceneDialog(scene)
            getDeleteSceneDialog()
                ?.confirmDelete()
        }
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
            "the user requests the story elements matching {string} for the {scene} scene"
        ) { query: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSceneListToolHasBeenOpened()
                .givenSceneEditorToolHasBeenOpened(scene)
                .requestStoryElementsMatching(query)
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
    }

    private fun thens() {
        Then("a scene named {string} should have been created") { sceneName: String ->
            SceneAssertions.assertSceneExistsWithName(sceneName)

            val sceneList = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneListToolHasBeenOpened()
            assertThat(sceneList) {
                hasSceneNamed(sceneName)
            }
        }
        Then(
            "the scene originally named {string} should have been renamed to {string}"
        ) { originalName: String, newName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val (scene) = SceneDriver(workbench).getScenesAtOnePointNamed(originalName)

            assertEquals(newName, scene.name.value)

            val sceneList = workbench.givenSceneListToolHasBeenOpened()
            assertThat(sceneList) {
                doesNotHaveSceneNamed(originalName)
                hasSceneNamed(newName)
            }
        }
        Then("a delete scene confirmation message should be shown") {
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            assertThat(workbench) {
                hasConfirmDeleteSceneDialogOpen()
            }
        }
        Then("the {string} scene should not have been deleted") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            SceneDriver(workbench).getSceneByNameOrError(sceneName)
        }
        Then("the {string} scene should have been deleted") { sceneName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            assertNull(SceneDriver(workbench).getSceneByName(sceneName))
        }
        Then(
            "{scene} should not have a motivation for {character} anymore"
        ) { scene: Scene, character: Character ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            assertTrue(scene.getMotivationForCharacter(character.id)!!.isInherited())

            val sceneDetailsTool = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneDetailsToolHasBeenOpened(scene)
            SceneDetailsAssertions.assertThat(sceneDetailsTool) {
                andCharacter(character.id.uuid.toString()) {
                    hasMotivationValue("")
                }
            }
        }
        Then(
            "{scene} should have {string} as {character}'s inherited motivation"
        ) { scene: Scene, expectedMotivation: String, character: Character ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            assertTrue(scene.getMotivationForCharacter(character.id)!!.isInherited())

            val sceneDetailsTool = workbench.givenSceneListToolHasBeenOpened()
                .givenSceneDetailsToolHasBeenOpened(scene)

            SceneDetailsAssertions.assertThat(sceneDetailsTool) {
                andCharacter(character.id.uuid.toString()) {
                    hasInheritedMotivationValue(expectedMotivation)
                }
            }
        }
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
            assertTrue(scene.locationId == location.id)
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
            SceneListAssert.assertThat(sceneList) {
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
    }

    companion object {
        private fun WorkBench.givenSceneCreatedWithName(sceneName: String): Scene {
            val driver = SceneDriver(this)
            return driver.getSceneByName(sceneName) ?: this.openCreateSceneDialog()
                .createSceneWithName(sceneName)
                .let { driver.getSceneByNameOrError(sceneName) }
        }
    }

}