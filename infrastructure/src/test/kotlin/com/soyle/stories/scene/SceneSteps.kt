package com.soyle.stories.scene

import com.soyle.stories.UATLogger
import com.soyle.stories.character.CharacterDriver
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest

class SceneSteps(en: En, double: SoyleStoriesTestDouble) : ApplicationTest() {

	private var targetObject: Any? = null
	private var createdScene: Scene? = null

	private var sceneIdFor: Map<String, Scene.Id>? = null
	private var characterIdFor: Map<String, Character.Id>? = null
	private var sceneRamificationsSceneId: Scene.Id? = null

	init {
		with(en) {

			Given("the Create Scene Dialog has been opened") {
				CreateSceneDialogDriver.givenHasBeenOpened(double)
			}
			Given("the Create Scene Dialog Name input has an invalid Scene Name") {
				CreateSceneDialogDriver.givenNameInputHasInvalidSceneName(double)
			}
			Given("the Create Scene Dialog Name input has a valid Scene Name") {
				CreateSceneDialogDriver.givenNameInputHasValidSceneName(double)
			}
			Given("{int} Scenes have been created") { count: Int ->
				ScenesDriver.givenNumberOfCreatedScenesIsAtLeast(double, count)
				targetObject = ScenesDriver.getCreatedScenes(double).firstOrNull()
			}
			Given("The Scene List Tool has been opened") {
				SceneListDriver.givenHasBeenOpened(double)
			}
			Given("A Scene has been created") {
				ScenesDriver.givenNumberOfCreatedScenesIsAtLeast(double, 1)
			}
			Given("the Scene right-click menu has been opened") {
				SceneListDriver.givenRightClickMenuHasBeenOpened(double)
			}
			Given("a Scene has been selected") {
				SceneListDriver.givenASceneHasBeenSelected(double)
			}
			Given("the user has entered a valid Scene name") {
				SceneListDriver.givenValidSceneNameHasBeenEntered(double)
			}
			Given("the Scene rename input box is visible") {
				SceneListDriver.givenRenameInputBoxHasBeenVisible(double)
				targetObject = SceneListDriver.getSelectedItem(double)
			}
			Given("the Scene List Tool has been opened") {
				SceneListDriver.givenHasBeenOpened(double)
				SceneListDriver.givenHasBeenVisible(double)
			}
			Given("the Scene List Tool tab has been selected") {
				SceneListDriver.givenHasBeenVisible(double)
			}
			Given("a Scene has been created") {
				ScenesDriver.givenNumberOfCreatedScenesIsAtLeast(double, 1)
			}
			Given("the Scene List Tool right-click menu has been opened") {
				SceneListDriver.givenRightClickMenuHasBeenOpened(double)
			}
			Given("the Confirm Delete Scene Dialog has been opened") {
				DeleteSceneDialogDriver.openDialog.given(double)
				targetObject = DeleteSceneDialogDriver.targetScene.get(double)!!
			}
			Given("all Characters have been included in the Scene") {
				val scene = targetObject as Scene
				CharacterDriver.getCharactersCreated(double).forEach { character ->
					ScenesDriver.characterIncludedIn(character.id, scene.id).given(double)
				}
			}
			Given("the following Scenes") { table: DataTable ->
				val cells = table.asLists()
				val headers = cells.first()
				val rows = cells.drop(1)
				val sceneNames = headers.drop(1)

				ScenesDriver.givenNumberOfCreatedScenesIsAtLeast(double, sceneNames.size)
				val scenes = ScenesDriver.getCreatedScenes(double)
				sceneIdFor = scenes.mapIndexed { index, scene ->
					sceneNames[index] to scene.id
				}.toMap()

				CharacterDriver.givenANumberOfCharactersHaveBeenCreated(double, rows.size)
				val characters = CharacterDriver.getCharactersCreated(double)
				characterIdFor = rows.mapIndexed { index, list ->
					val character = characters[index]
					list.drop(1).forEachIndexed { sceneIndex, motivation ->
						when (motivation) {
							"inherit" -> ScenesDriver.characterIncludedIn(character.id, scenes[sceneIndex].id).given(double)
							"-" -> {}
							else -> {
								ScenesDriver.characterIncludedIn(character.id, scenes[sceneIndex].id).given(double)
								ScenesDriver.charactersMotivationIn(character.id, motivation, scenes[sceneIndex].id).given(double)
							}
						}
					}
					list.first() to character.id
				}.toMap()
			}
			Given("the Delete Scene Ramifications Tool has been opened for {string}") { focusScene: String ->
				val sceneId = sceneIdFor!![focusScene]!!
				sceneRamificationsSceneId = sceneId
				DeleteSceneRamificationsDriver.tool(sceneId).given(double)
			}

			When("The Scene List Tool is opened") {
				if (SceneListDriver.isOpen(double)) {
					SceneListDriver.whenClosed(double)
				}
				SceneListDriver.whenOpened(double)
				assertTrue(SceneListDriver.isOpen(double))
				SceneListDriver.givenHasBeenVisible(double)
			}
			When("A new Scene is created") {
				ScenesDriver.whenSceneIsCreated(double)
				createdScene = ScenesDriver.getCreatedScenes(double).last()
			}
			When("A Scene is deleted") {
				val existingScenes = ScenesDriver.getCreatedScenes(double)
				ScenesDriver.whenSceneIsDeleted(double)
				targetObject = (existingScenes.toSet() - ScenesDriver.getCreatedScenes(double).toSet()).single()
			}
			When("the user clicks the Scene List Tool delete button") {
				SceneListDriver.whenBottomButtonIsClicked(double, "delete")
				targetObject = SceneListDriver.getSelectedItem(double)
			}
			When("the center Create New Scene button is selected") {
				interact {
					clickOn(SceneListDriver.centerButton.get(double)!!, MouseButton.PRIMARY)
				}
			}
			When("the bottom Create New Scene button is selected") {
				SceneListDriver.whenBottomButtonIsClicked(double, "create")
			}
			When("the Scene List Tool right-click menu {string} option is selected") { option: String ->
				SceneListDriver.whenRightClickOptionIsClicked(double, option)
				targetObject = SceneListDriver.getSelectedItem(double)
			}
			When("a new Scene is created without a relative Scene") {
				ScenesDriver.whenSceneIsCreated(double)
				createdScene = ScenesDriver.getCreatedScenes(double).last()
			}
			When("a new Scene is created before a relative Scene") {
				val existing = ScenesDriver.getCreatedScenes(double).map(Scene::id).toSet()
				targetObject = existing.first()
				ScenesDriver.createdSceneBefore(existing.first()).whenSet(double)
				createdScene = ScenesDriver.getCreatedScenes(double).filterNot { it.id in existing }.firstOrNull()
			}
			When("a new Scene is created after the first Scene") {
				val existing = ScenesDriver.getCreatedScenes(double).map(Scene::id).toSet()
				targetObject = existing.first()
				ScenesDriver.createdSceneAfter(existing.first()).whenSet(double)
				createdScene = ScenesDriver.getCreatedScenes(double).filterNot { it.id in existing }.firstOrNull()
			}
			When("the Confirm Delete Scene Dialog {string} button is selected") { button: String ->
				if (button == "Show Ramifications") {
					sceneRamificationsSceneId = (targetObject as Scene).id
				}
				val button = DeleteSceneDialogDriver.button(button).get(double)!!
				interact {
					clickOn(button, MouseButton.PRIMARY)
				}
			}
			When("the Confirm Delete Scene Dialog do not show again check-box is checked") {
				val checkBox = DeleteSceneDialogDriver.doNotShowCheckbox.get(double)!!
				interact {
					clickOn(checkBox, MouseButton.PRIMARY)
				}
			}
			When("the Delete Scene Ramifications Tool is opened") {
				val sceneId = ScenesDriver.getCreatedScenes(double).first().id
				sceneRamificationsSceneId = sceneId
				DeleteSceneRamificationsDriver.openTool(sceneId).whenSet(double)
			}
			When("the Delete Scene Ramifications Tool is opened for {string}") { sceneName: String ->
				val sceneId = sceneIdFor!![sceneName]!!
				sceneRamificationsSceneId = sceneId
				DeleteSceneRamificationsDriver.tool(sceneId).whenSet(double)
			}
			When("{string} is deleted") { sceneName: String ->
				val sceneId = sceneIdFor!![sceneName]!!
				ScenesDriver.deletedScene(sceneId).whenSet(double)
			}
			When("{string} is removed from {string} in the Delete Scene Ramifications Tool for {string}") {
				characterName: String, listedSceneName: String, focusSceneName: String ->

				CharacterDriver.whenCharacterIsDeleted(double, characterIdFor!!.getValue(characterName))
			}
			When("{string} is removed from the Delete Scene Ramifications Tool for {string}") {
				listedScene: String, focusScene: String ->
				val listedSceneId = sceneIdFor!![listedScene]!!
				val focusSceneId = sceneIdFor!![focusScene]!!

				DeleteSceneRamificationsDriver.removeScene(focusSceneId, listedSceneId, double)
			}
			When("the Character Motivation for {string} is cleared in {string}") { character: String, scene: String ->
				val sceneId = sceneIdFor!!.getValue(scene)
				val characterId = characterIdFor!!.getValue(character)

				ScenesDriver.charactersMotivationIn(characterId, null, sceneId).whenSet(double)
			}


			Then("an error message should be displayed in the Create Scene Dialog") {
				assertTrue(CreateSceneDialogDriver.isErrorMessageShown(double))
			}
			Then("the Create Scene Dialog should be open") {
				assertTrue(CreateSceneDialogDriver.isOpen(double))
			}
			Then("the Create Scene Dialog should be closed") {
				assertFalse(CreateSceneDialogDriver.isOpen(double))
			}
			Then("a new Scene should be created") {
				assertTrue(ScenesDriver.getNumberOfCreatedScenes(double) >= 1)
			}
			Then("The Scene List Tool should show a special empty message") {
				assertTrue(SceneListDriver.isShowingEmptyMessage(double))
			}
			Then("The Scene List Tool should show all {int} scenes") { count: Int ->
				assertTrue(SceneListDriver.isShowingNumberOfScenes(double, count))
			}
			Then("The Scene List Tool should show the new Scene") {
				assertTrue(SceneListDriver.isShowingScene(double, createdScene as Scene))
			}
			Then("the Scene's name should be replaced by an input box") {
				assertTrue(SceneListDriver.isRenameInputBoxVisible(double))
			}
			Then("the Scene rename input box should contain the Scene's name") {
				assertTrue(SceneListDriver.isRenameInputBoxShowingNameOfSelected(double))
			}
			Then("the Scene rename input box should be replaced by the Scene name") {
				assertFalse(SceneListDriver.isRenameInputBoxVisible(double))
			}
			Then("the Scene name should be the new name") {
				val item = SceneListDriver.getItems(double).find {
					it.value!!.id == (targetObject as SceneItemViewModel).id
				}
				val matching = item!!.value!!.name == (targetObject as SceneItemViewModel).name
				assertFalse(matching)
			}
			Then("the Scene name should be the original name") {
				assertTrue(SceneListDriver.isSelectedItemNameMatching(double, (targetObject as SceneItemViewModel).name))
			}
			Then("The Scene List Tool should not show the deleted Scene") {
				assertFalse(SceneListDriver.isShowingScene(double, (targetObject as Scene)))
			}
			Then("the Confirm Delete Scene Dialog should be opened") {
				assertTrue(DeleteSceneDialogDriver.openDialog.check(double))
			}
			Then("the Confirm Delete Scene Dialog should show the Scene name") {
				assertTrue(DeleteSceneDialogDriver.isShowingNameOf((targetObject as SceneItemViewModel)).check(double))
			}
			Then("the Scene List Tool should show the new Scene") {
				assertTrue(SceneListDriver.isShowingScene(double, createdScene!!))
			}
			Then("the new Scene should be at the end of the Scene List Tool") {
				assertEquals(
				  ScenesDriver.getNumberOfCreatedScenes(double) - 1,
				  SceneListDriver.indexOfItemWithId(double, (createdScene as Scene).id)
				)
			}
			Then("the new Scene should be listed before the relative Scene in the Scene List Tool") {
				val relativeIndex = SceneListDriver.indexOfItemWithId(double, (targetObject as Scene.Id))
				val createdIndex = SceneListDriver.indexOfItemWithId(double, createdScene!!.id)
				assertEquals(relativeIndex - 1, createdIndex)
			}
			Then("the new Scene should be listed after the first Scene in the Scene List Tool") {
				val createdIndex = SceneListDriver.indexOfItemWithId(double, createdScene!!.id)
				assertEquals(1, createdIndex)
			}
			Then("the Confirm Delete Scene Dialog should be closed") {
				assertFalse(DeleteSceneDialogDriver.openDialog.check(double))
			}
			Then("the Scene should not be deleted") {
				ScenesDriver.getCreatedScenes(double).find {
					it.id == (targetObject as Scene).id
				}!!
			}
			Then("the Confirm Delete Scene Dialog should not open the next time a Scene is deleted") {
				SceneListDriver.givenASceneHasBeenSelected(double)
				SceneListDriver.givenRightClickMenuHasBeenOpened(double)
				SceneListDriver.whenRightClickOptionIsClicked(double, "Delete")
				assertFalse(DeleteSceneDialogDriver.openDialog.check(double))
			}
			Then("the Scene should be deleted") {
				assertNull(ScenesDriver.getCreatedScenes(double).find {
					it isSameEntityAs (targetObject as Scene)
				})
			}
			Then("the Delete Scene Ramifications Tool should be open") {
				assertTrue(DeleteSceneRamificationsDriver.openTool(sceneRamificationsSceneId!!).check(double))
			}
			Then("the Delete Scene Ramifications Tool should display an ok message") {
				assertTrue(DeleteSceneRamificationsDriver.okDisplay(sceneRamificationsSceneId!!).check(double))
			}
			Then("{string} should not be listed in the Delete Scene Ramifications Tool for {string}") { sceneName: String, focusScene: String ->
				val focusSceneId = sceneIdFor!!.getValue(focusScene)
				val targetSceneId = sceneIdFor!!.getValue(sceneName)
				assertFalse(DeleteSceneRamificationsDriver.listedScene(focusSceneId, targetSceneId).check(double))
			}
			Then("{string} should be listed for {string} in the Delete Scene Ramifications Tool for {string}") { characterName: String, sceneName: String, focusScene: String ->
				val focusSceneId = sceneIdFor!!.getValue(focusScene)
				val targetSceneId = sceneIdFor!!.getValue(sceneName)
				val characterId = characterIdFor!!.getValue(characterName)
				assertTrue(DeleteSceneRamificationsDriver.listedCharacter(focusSceneId, targetSceneId, characterId).check(double))
			}
			Then("{string} should not be listed for {string} in the Delete Scene Ramifications Tool for {string}") { characterName: String, sceneName: String, focusScene: String ->
				val focusSceneId = sceneIdFor!!.getValue(focusScene)
				val targetSceneId = sceneIdFor!!.getValue(sceneName)
				val characterId = characterIdFor!!.getValue(characterName)
				val characterItem = DeleteSceneRamificationsDriver.listedCharacter(focusSceneId, targetSceneId, characterId).get(double)

				assertNull(characterItem)
			}
			Then("the Current Motivation field for {string} in {string} in the Delete Scene Ramifications Tool for {string} should show {string}") {
				characterName: String, listedSceneName: String, focusSceneName: String, expectedValue: String ->

				val characterId = characterIdFor!!.getValue(characterName)
				val listedSceneId = sceneIdFor!!.getValue(listedSceneName)
				val focusSceneId = sceneIdFor!!.getValue(focusSceneName)

				UATLogger.silent = false
				val currentMotivation = DeleteSceneRamificationsDriver.currentMotivation(focusSceneId, listedSceneId, characterId).get(double)
				UATLogger.silent = true

				assertEquals(expectedValue, currentMotivation)
			}
			Then("the Current Motivation field for {string} in {string} in the Delete Scene Ramifications Tool for {string} should be empty") {
				characterName: String, listedSceneName: String, focusSceneName: String ->

				val characterId = characterIdFor!!.getValue(characterName)
				val listedSceneId = sceneIdFor!!.getValue(listedSceneName)
				val focusSceneId = sceneIdFor!!.getValue(focusSceneName)

				val currentMotivation = DeleteSceneRamificationsDriver.currentMotivation(focusSceneId, listedSceneId, characterId).get(double)

				assertTrue(currentMotivation!!.isEmpty())
			}
			Then("the Changed Motivation field for {string} in {string} in the Delete Scene Ramifications Tool for {string} should show {string}") {
				characterName: String, listedSceneName: String, focusSceneName: String, expectedValue: String ->

				val characterId = characterIdFor!!.getValue(characterName)
				val listedSceneId = sceneIdFor!!.getValue(listedSceneName)
				val focusSceneId = sceneIdFor!!.getValue(focusSceneName)

				val changedMotivation = DeleteSceneRamificationsDriver.changedMotivation(focusSceneId, listedSceneId, characterId).get(double)

				assertEquals(expectedValue, changedMotivation)
			}
			Then("the Changed Motivation field for {string} in {string} in the Delete Scene Ramifications Tool for {string} should be empty") {
				characterName: String, listedSceneName: String, focusSceneName: String ->

				val characterId = characterIdFor!!.getValue(characterName)
				val listedSceneId = sceneIdFor!!.getValue(listedSceneName)
				val focusSceneId = sceneIdFor!!.getValue(focusSceneName)

				val changedMotivation = DeleteSceneRamificationsDriver.changedMotivation(focusSceneId, listedSceneId, characterId).get(double)

				assertTrue(changedMotivation!!.isEmpty())
			}
			Then("the deleted Character should be removed from the Delete Scene Ramifications Tool") {
				val deletedCharacter = CharacterDriver.recentlyDeletedCharacter.get(double)!!
				assertFalse(
				  DeleteSceneRamificationsDriver.listedCharacter(sceneRamificationsSceneId!!, deletedCharacter.id).check(double)
				)
			}
			Then("the Delete Scene Ramifications Tool for {string} should display an ok message") { focusScene: String ->
				val focusSceneId = sceneIdFor!!.getValue(focusScene)
				assertTrue(DeleteSceneRamificationsDriver.okDisplay(focusSceneId).check(double))
			}
			Then("{string} should be listed in the Delete Scene Ramifications Tool for {string}") { listedScene: String, focusScene: String ->
				val listedSceneId = sceneIdFor!!.getValue(listedScene)
				val focusSceneId = sceneIdFor!!.getValue(focusScene)
				assertTrue(DeleteSceneRamificationsDriver.listedScene(focusSceneId, listedSceneId).check(double))
			}
		}
	}

}