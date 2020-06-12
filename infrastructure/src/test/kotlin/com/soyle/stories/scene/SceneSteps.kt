package com.soyle.stories.scene

import com.soyle.stories.UATLogger
import com.soyle.stories.character.CharacterDriver
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Scene
import com.soyle.stories.location.LocationSteps
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import javafx.event.ActionEvent
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.testfx.framework.junit5.ApplicationTest

class SceneSteps(en: En, double: SoyleStoriesTestDouble) : ApplicationTest() {

	private var targetObject: Any? = null
	private var createdScene: Scene? = null

	private var sceneRamificationsSceneId: Scene.Id? = null

	init {

		ReorderSceneDialogSteps(en, double)

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
				val scene = targetObject as? Scene ?: ScenesDriver.getCreatedScenes(double).first()
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
				ScenesDriver.registerIdentifiers(double, scenes.mapIndexed { index, scene ->
					sceneNames[index] to scene.id
				})

				CharacterDriver.givenANumberOfCharactersHaveBeenCreated(double, rows.size)
				val characters = CharacterDriver.getCharactersCreated(double)
				CharacterDriver.registerIdentifiers(double, rows.mapIndexed { index, list ->
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
				})
			}
			Given("the Delete Scene Ramifications Tool has been opened for {string}") { focusScene: String ->
				val sceneId = ScenesDriver.getSceneIdByIdentifier(double, focusScene)!!
				sceneRamificationsSceneId = sceneId
				DeleteSceneRamificationsDriver.tool(sceneId).given(double)
			}
			Given("a Location has been linked to the Scene") {
				ScenesDriver
				  .locationLinkedToScene(
					ScenesDriver.getCreatedScenes(double).first(),
					LocationSteps.getLocationsCreated(double).first()
				  )
				  .given(double)
			}
			Given("the Scene Details Tool has been opened") {
				SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .openTool
				  .given(double)
			}
			Given("{int} Characters have been included in the Scene") { characterCount: Int ->
				val scene = targetObject as? Scene ?: ScenesDriver.getCreatedScenes(double).first()
				CharacterDriver.getCharactersCreated(double).take(characterCount).forEach { character ->
					ScenesDriver.characterIncludedIn(character.id, scene.id).given(double)
				}
			}
			Given("the {string} Scene Details Tool has been opened") { scene: String ->
				val sceneId = ScenesDriver.getSceneIdByIdentifier(double, scene)!!
				SceneDetailsDriver.toolFor(ScenesDriver.getCreatedScenes(double).find { it.id == sceneId }!!)
				  .openTool.given(double)
			}
			Given("the {string} Scene Details Character Motivation Previously Set tooltip has been opened for {string}") {
				sceneId: String, characterId: String ->
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				SceneDetailsDriver.toolFor(scene)
				  .previouslySetToolTipFor(CharacterDriver.getCharacterByIdentifier(double, characterId)!!)
				  .openTooltip
				  .given(double)
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
				val btn = DeleteSceneDialogDriver.button(button).get(double)!!
				interact {
					btn.fireEvent(ActionEvent())
				}
			}
			When("the Confirm Delete Scene Dialog do not show again check-box is checked") {
				val checkBox = DeleteSceneDialogDriver.doNotShowCheckbox.get(double)!!
				interact {
					checkBox.fire()
				}
			}
			When("the Delete Scene Ramifications Tool is opened") {
				val sceneId = ScenesDriver.getCreatedScenes(double).first().id
				sceneRamificationsSceneId = sceneId
				DeleteSceneRamificationsDriver.openTool(sceneId).whenSet(double)
			}
			When("the Delete Scene Ramifications Tool is opened for {string}") { sceneName: String ->
				val sceneId = ScenesDriver.getSceneIdByIdentifier(double, sceneName)!!
				sceneRamificationsSceneId = sceneId
				DeleteSceneRamificationsDriver.tool(sceneId).whenSet(double)
			}
			When("{string} is deleted") { sceneName: String ->
				val sceneId = ScenesDriver.getSceneIdByIdentifier(double, sceneName)!!
				ScenesDriver.deletedScene(sceneId).whenSet(double)
			}
			When("{string} is removed from {string} in the Delete Scene Ramifications Tool for {string}") {
				characterIdentifier: String, listedSceneName: String, focusSceneName: String ->

				CharacterDriver.whenCharacterIsDeleted(double, CharacterDriver.getCharacterIdByIdentifier(double, characterIdentifier)!!)
			}
			When("{string} is removed from the Delete Scene Ramifications Tool for {string}") {
				listedScene: String, focusScene: String ->
				val listedSceneId = ScenesDriver.getSceneIdByIdentifier(double, listedScene)!!
				val focusSceneId = ScenesDriver.getSceneIdByIdentifier(double, focusScene)!!

				DeleteSceneRamificationsDriver.removeScene(focusSceneId, listedSceneId, double)
			}
			When("the Character Motivation for {string} is cleared in {string}") { characterIdentifier: String, scene: String ->
				val sceneId = ScenesDriver.getSceneIdByIdentifier(double, scene)!!
				val characterId = CharacterDriver.getCharacterIdByIdentifier(double, characterIdentifier)!!

				ScenesDriver.charactersMotivationIn(characterId, null, sceneId).whenSet(double)
			}
			When("the Character Motivation for {string} is set in {string}") { characterIdentifier: String, scene: String ->
				val sceneId = ScenesDriver.getSceneIdByIdentifier(double, scene)!!
				val characterId = CharacterDriver.getCharacterIdByIdentifier(double, characterIdentifier)!!

				ScenesDriver.charactersMotivationIn(characterId, "new value", sceneId).whenSet(double)
			}
			When("the Character Motivation for {string} is set in {string} as {string}") { characterIdentifier: String, scene: String, motive: String ->
				val sceneId = ScenesDriver.getSceneIdByIdentifier(double, scene)!!
				val characterId = CharacterDriver.getCharacterIdByIdentifier(double, characterIdentifier)!!

				ScenesDriver.charactersMotivationIn(characterId, motive, sceneId).whenSet(double)
			}
			When("the Scene Details Tool is opened") {
				SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .openTool
				  .whenSet(double)
			}
			When("a Location is selected from the Scene Details Location drop-down") {
				val item = SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .locationDropDownItems(double)!!
				  .first()
				targetObject = item.text
				interact { item.fire() }
			}
			When("the Character is included in the Scene") {
				ScenesDriver.characterIncludedIn(
				  CharacterDriver.getCharactersCreated(double).first().id,
				  ScenesDriver.getCreatedScenes(double).first().id
				).whenSet(double)
			}
			When("a Character is removed from the Scene") {
				val scene = targetObject as? Scene ?: ScenesDriver.getCreatedScenes(double).first()
				targetObject = scene.characterMotivations.first().characterId
				ScenesDriver.characterRemovedFrom(scene, scene.characterMotivations.first().characterId).whenSet(double)
			}
			When("the {string} Scene Details Tool is opened") { scene: String ->
				val sceneId = ScenesDriver.getSceneIdByIdentifier(double, scene)!!
				SceneDetailsDriver.toolFor(ScenesDriver.getCreatedScenes(double).find { it.id == sceneId }!!)
				  .openTool.whenSet(double)
			}
			When("the {string} Scene Details Character Motivation Previously Set tooltip is opened for {string}") {
				sceneId: String, characterId: String ->
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				val character = CharacterDriver.getCharacterByIdentifier(double, characterId)!!
				SceneDetailsDriver
				  .toolFor(scene)
				  .previouslySetToolTipFor(character)
				  .openTooltip
				  .whenSet(double)
			}
			When("the {string} Scene Details Character Motivation Previously Set tooltip scene name is selected") {
				sceneId: String ->
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				SceneDetailsDriver
				  .toolFor(scene)
				  .previouslySetToolTip
				  .whenSceneNameSelected(double)
			}
			When("the {string} Scene Details {string} Character Motivation Reset button is selected") {
				sceneId: String, characterId: String ->
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				val character = CharacterDriver.getCharacterByIdentifier(double, characterId)!!
				SceneDetailsDriver.toolFor(scene)
				  .listedCharacter(character)
				  .whenResetButtonSelected(double)
			}
			When("a Scene is dragged to a new position in the Scene List Tool") {
				SceneListDriver.whenASceneIsDragged(double)
			}
			When("a Scene is reordered") {
				val scenes = ScenesDriver.getCreatedScenes(double)
				targetObject = scenes.first()
				ScenesDriver
				  .reorderScene(
					double,
					scenes.first(),
					scenes.size
				  )
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
				assertFalse(DeleteSceneDialogDriver.openWindow.check(double))
			}
			Then("the Scene should not be deleted") {
				ScenesDriver.getCreatedScenes(double).find {
					it.id == (targetObject as Scene).id
				}!!
			}
			Then("the Confirm Delete Scene Dialog should not open the next time a Scene is deleted") {
				SceneListDriver.givenASceneHasBeenSelected(double)
				SceneListDriver.whenBottomButtonIsClicked(double, "delete")
				assertFalse(DeleteSceneDialogDriver.openWindow.check(double))
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
				val focusSceneId = ScenesDriver.getSceneIdByIdentifier(double, focusScene)!!
				val targetSceneId = ScenesDriver.getSceneIdByIdentifier(double, sceneName)!!
				assertFalse(DeleteSceneRamificationsDriver.listedScene(focusSceneId, targetSceneId).check(double))
			}
			Then("{string} should be listed for {string} in the Delete Scene Ramifications Tool for {string}") {
				characterIdentifier: String, sceneName: String, focusScene: String ->
				val focusSceneId = ScenesDriver.getSceneIdByIdentifier(double, focusScene)!!
				val targetSceneId = ScenesDriver.getSceneIdByIdentifier(double, sceneName)!!
				val characterId = CharacterDriver.getCharacterIdByIdentifier(double, characterIdentifier)!!
				assertTrue(DeleteSceneRamificationsDriver.listedCharacter(focusSceneId, targetSceneId, characterId).check(double))
			}
			Then("{string} should not be listed for {string} in the Delete Scene Ramifications Tool for {string}") {
				characterIdentifier: String, sceneName: String, focusScene: String ->
				val focusSceneId = ScenesDriver.getSceneIdByIdentifier(double, focusScene)!!
				val targetSceneId = ScenesDriver.getSceneIdByIdentifier(double, sceneName)!!
				val characterId = CharacterDriver.getCharacterIdByIdentifier(double, characterIdentifier)!!
				val characterItem = DeleteSceneRamificationsDriver.listedCharacter(focusSceneId, targetSceneId, characterId).get(double)

				assertNull(characterItem)
			}
			Then("the Current Motivation field for {string} in {string} in the Delete Scene Ramifications Tool for {string} should show {string}") {
				characterIdentifier: String, listedSceneName: String, focusSceneName: String, expectedValue: String ->

				val characterId = CharacterDriver.getCharacterIdByIdentifier(double, characterIdentifier)!!
				val listedSceneId = ScenesDriver.getSceneIdByIdentifier(double, listedSceneName)!!
				val focusSceneId = ScenesDriver.getSceneIdByIdentifier(double, focusSceneName)!!

				UATLogger.silent = false
				val currentMotivation = DeleteSceneRamificationsDriver.currentMotivation(focusSceneId, listedSceneId, characterId).get(double)
				UATLogger.silent = true

				assertEquals(expectedValue, currentMotivation)
			}
			Then("the Current Motivation field for {string} in {string} in the Delete Scene Ramifications Tool for {string} should be empty") {
				characterIdentifier: String, listedSceneName: String, focusSceneName: String ->

				val characterId = CharacterDriver.getCharacterIdByIdentifier(double, characterIdentifier)!!
				val listedSceneId = ScenesDriver.getSceneIdByIdentifier(double, listedSceneName)!!
				val focusSceneId = ScenesDriver.getSceneIdByIdentifier(double, focusSceneName)!!

				val currentMotivation = DeleteSceneRamificationsDriver.currentMotivation(focusSceneId, listedSceneId, characterId).get(double)

				assertTrue(currentMotivation!!.isEmpty())
			}
			Then("the Changed Motivation field for {string} in {string} in the Delete Scene Ramifications Tool for {string} should show {string}") {
				characterIdentifier: String, listedSceneName: String, focusSceneName: String, expectedValue: String ->

				val characterId = CharacterDriver.getCharacterIdByIdentifier(double, characterIdentifier)!!
				val listedSceneId = ScenesDriver.getSceneIdByIdentifier(double, listedSceneName)!!
				val focusSceneId = ScenesDriver.getSceneIdByIdentifier(double, focusSceneName)!!

				val changedMotivation = DeleteSceneRamificationsDriver.changedMotivation(focusSceneId, listedSceneId, characterId).get(double)

				assertEquals(expectedValue, changedMotivation)
			}
			Then("the Changed Motivation field for {string} in {string} in the Delete Scene Ramifications Tool for {string} should be empty") {
				characterIdentifier: String, listedSceneName: String, focusSceneName: String ->

				val characterId = CharacterDriver.getCharacterIdByIdentifier(double, characterIdentifier)!!
				val listedSceneId = ScenesDriver.getSceneIdByIdentifier(double, listedSceneName)!!
				val focusSceneId = ScenesDriver.getSceneIdByIdentifier(double, focusSceneName)!!

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
				val focusSceneId = ScenesDriver.getSceneIdByIdentifier(double, focusScene)!!
				assertTrue(DeleteSceneRamificationsDriver.okDisplay(focusSceneId).check(double))
			}
			Then("{string} should be listed in the Delete Scene Ramifications Tool for {string}") { listedScene: String, focusScene: String ->
				val listedSceneId = ScenesDriver.getSceneIdByIdentifier(double, listedScene)!!
				val focusSceneId = ScenesDriver.getSceneIdByIdentifier(double, focusScene)!!
				assertTrue(DeleteSceneRamificationsDriver.listedScene(focusSceneId, listedSceneId).check(double))
			}
			Then("the Scene Details Location dropdown should be disabled") {
				SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .isLocationDropDownDisabled
				  .check(double)
				  .let(::assertTrue)
			}
			Then("the Scene Details Location dropdown should not be disabled") {
				SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .isLocationDropDownDisabled
				  .check(double)
				  .let(::assertFalse)
			}
			Then("the Scene Details Location dropdown should show {string}") { location: String ->
				SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .locationDropDownText
				  .get(double)
				  .let { assertEquals(location, it) }
			}
			Then("the Scene Details Location dropdown should show the linked Location name") {
				val scene = ScenesDriver.getCreatedScenes(double).first()
				val location = LocationSteps.getLocationsCreated(double).find {
					it.id == scene.locationId
				}!!.name
				SceneDetailsDriver
				  .toolFor(scene)
				  .locationDropDownText
				  .get(double)
				  .let { assertEquals(location, it) }
			}
			Then("the Location should be linked to the Scene") {
				val scene = ScenesDriver.getCreatedScenes(double).first()
				val location = LocationSteps.getLocationsCreated(double).find { it.id == scene.locationId }!!
				assertEquals(targetObject as String, location.name)
			}
			Then("the Scene Details Location drop-down should show the selected Location name") {
				SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .locationDropDownText
				  .get(double)
				  .let { assertEquals(targetObject as String, it) }
			}
			Then("the Scene Details Location drop-down should show the new Location name") {
				val locationName = LocationSteps.getLocationsCreated(double).first().name
				val text = SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .locationDropDownText
				  .get(double)
				assertEquals(locationName, text)
			}
			Then("the Scene Details Add Character button should be disabled") {
				SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .isAddCharacterButtonDisabled
				  .check(double)
				  .let(::assertTrue)
			}
			Then("the Scene Details Add Character button should not be disabled") {
				SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .isAddCharacterButtonDisabled
				  .check(double)
				  .let(::assertFalse)
			}
			Then("no Characters should be listed in the Scene Details Tool") {
				SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .isIncludedCharacterListEmpty
				  .check(double)
				  .let(::assertTrue)
			}
			Then("all Characters should be listed in the Scene Details Tool") {
				val characters = CharacterDriver.getCharactersCreated(double)
				SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .includedCharacterListHasAll(characters)
				  .check(double)
				  .let(Assertions::assertTrue)
			}
			Then("the Character should be listed in the Scene Details Tool") {
				SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .includedCharacterListHas(CharacterDriver.getCharactersCreated(double).first())
				  .check(double)
				  .let(Assertions::assertTrue)
			}
			Then("the Character should not be listed in the Scene Details Tool") {
				SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .includedCharacterListHas(CharacterDriver.getCharactersCreated(double).find { it.id == targetObject as Character.Id }!!)
				  .check(double)
				  .let(Assertions::assertFalse)
			}
			Then("the Scene Details Character Motivation field should be blank") {
				val motivation = SceneDetailsDriver
				  .toolFor(ScenesDriver.getCreatedScenes(double).first())
				  .motivationTextFor(CharacterDriver.getCharactersCreated(double).first())
				  .get(double)
				assertEquals("", motivation)
			}
			Then("the {string} Scene Details {string} Character Motivation field should show {string}") {
				sceneId: String, characterId: String, expectedMotivation: String ->
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				val character = CharacterDriver.getCharacterByIdentifier(double, characterId)!!
				val motivation = SceneDetailsDriver.toolFor(scene)
				  .listedCharacter(character)
				  .motivationText
				  .get(double)
				assertEquals(expectedMotivation, motivation)
			}
			Then("the {string} Scene Details {string} Character Motivation Previously Set tip should be visible") {
				sceneId: String, characterId: String ->
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				val character = CharacterDriver.getCharacterByIdentifier(double, characterId)!!
				SceneDetailsDriver.toolFor(scene)
				  .listedCharacter(character)
				  .isPreviouslySetTipVisible
				  .check(double)
				  .let(::assertTrue)
			}
			Then("the {string} Scene Details {string} Character Motivation Reset button should be visible") {
				sceneId: String, characterId: String ->
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				val character = CharacterDriver.getCharacterByIdentifier(double, characterId)!!
				SceneDetailsDriver.toolFor(scene)
				  .listedCharacter(character)
				  .isResetButtonVisible
				  .check(double)
				  .let(::assertTrue)
			}
			Then("the {string} Scene Details Character Motivation Previously Set tooltip scene name should show {string}") {
				sceneId: String, expectedSceneId: String ->
				UATLogger.silent = false
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				val expectedSceneName = ScenesDriver.getSceneByIdentifier(double, expectedSceneId)!!.name
				val sceneName = SceneDetailsDriver.toolFor(scene)
				  .previouslySetToolTip
				  .sceneName
				  .get(double)
				assertEquals(expectedSceneName, sceneName)
			}
			Then("the {string} Scene Details Character Motivation Previously Set tooltip motivation should show {string}") {
				sceneId: String, expectedMotivation: String ->
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				val motivation = SceneDetailsDriver.toolFor(scene)
				  .previouslySetToolTip
				  .motivation
				  .get(double)
				assertEquals(expectedMotivation, motivation)
			}
			Then("the {string} Scene Details Tool should be open") { sceneId: String ->
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				SceneDetailsDriver.toolFor(scene)
				  .openTool.check(double)
				  .let(::assertTrue)
			}
			Then("the {string} Scene Details Tool should be in focus") { sceneId: String ->
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				SceneDetailsDriver.toolFor(scene)
				  .isToolFocused
				  .check(double)
				  .let(::assertTrue)
			}
			Then("the {string} Scene Details {string} Character Motivation Reset button should not be visible")
			{ sceneId: String, characterId: String ->
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				val character = CharacterDriver.getCharacterByIdentifier(double, characterId)!!
				SceneDetailsDriver.toolFor(scene)
				  .listedCharacter(character)
				  .isResetButtonVisible
				  .check(double)
				  .let(::assertFalse)
			}
			Then("the {string} Scene Details {string} name should show {string}")
			{ sceneId: String, characterId: String, expectedName: String ->
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				val character = CharacterDriver.getCharacterByIdentifier(double, characterId)!!
				val characterName = SceneDetailsDriver.toolFor(scene)
				  .listedCharacter(character)
				  .characterName
				  .get(double)
				assertEquals(expectedName, characterName)
			}
			Then("the {string} Scene Details should not list {string} as an included character")
			{ sceneId: String, characterId: String ->
				val scene = ScenesDriver.getSceneByIdentifier(double, sceneId)!!
				val character = CharacterDriver.getCharacterIdByIdentifier(double, characterId)!!
				SceneDetailsDriver.toolFor(scene)
				  .hasListedCharacter(character)
				  .check(double)
				  .let(::assertFalse)
			}
			Then("the Scene should be in its new position") {
				val firstScene = targetObject as Scene
				val listedIndex = SceneListDriver.indexOfItemWithId(double, firstScene.id)
				assertEquals(2, listedIndex)
			}
			Then("all Scenes in the Scene List Tool should be numbered to match the list order") {
				val items = SceneListDriver.getItems(double)
				items.withIndex().forEach {
					assertEquals(it.index, it.value.value!!.index)
				}
			}
			Then("the Scene should not be reordered") {
				val request = ReorderSceneDialogSteps.reorderRequest(double)!!
				val index = ScenesDriver.getCreatedScenes(double).indexOfFirst { it.id == request.first.id }
				assertNotEquals(request.second, index)
			}
			Then("the Scene should be reordered") {
				val request = ReorderSceneDialogSteps.reorderRequest(double)!!
				val index = ScenesDriver.getCreatedScenes(double).indexOfFirst { it.id == request.first.id }
				assertTrue(
				  index == request.second || index == request.second -1
				)
			}
		}
	}

}