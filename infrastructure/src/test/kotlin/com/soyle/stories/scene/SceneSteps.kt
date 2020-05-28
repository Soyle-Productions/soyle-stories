package com.soyle.stories.scene

import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import io.cucumber.java8.En
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.testfx.framework.junit5.ApplicationTest

class SceneSteps(en: En, double: SoyleStoriesTestDouble) : ApplicationTest() {

	private var targetObject: Any? = null
	private var createdScene: Scene? = null

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


			When("The Scene List Tool is opened") {
				if (SceneListDriver.isOpen(double)) {
					SceneListDriver.whenClosed(double)
				}
				SceneListDriver.whenOpened(double)
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
				val button = DeleteSceneDialogDriver.button(button).get(double)!!
				interact {
					clickOn(button, MouseButton.PRIMARY)
				}
			}


			Then("an error message should be displayed in the Create Scene Dialog") {
				Assertions.assertTrue(CreateSceneDialogDriver.isErrorMessageShown(double))
			}
			Then("the Create Scene Dialog should be open") {
				Assertions.assertTrue(CreateSceneDialogDriver.isOpen(double))
			}
			Then("the Create Scene Dialog should be closed") {
				Assertions.assertFalse(CreateSceneDialogDriver.isOpen(double))
			}
			Then("a new Scene should be created") {
				Assertions.assertTrue(ScenesDriver.getNumberOfCreatedScenes(double) >= 1)
			}
			Then("The Scene List Tool should show a special empty message") {
				Assertions.assertTrue(SceneListDriver.isShowingEmptyMessage(double))
			}
			Then("The Scene List Tool should show all {int} scenes") { count: Int ->
				Assertions.assertTrue(SceneListDriver.isShowingNumberOfScenes(double, count))
			}
			Then("The Scene List Tool should show the new Scene") {
				Assertions.assertTrue(SceneListDriver.isShowingScene(double, createdScene as Scene))
			}
			Then("the Scene's name should be replaced by an input box") {
				Assertions.assertTrue(SceneListDriver.isRenameInputBoxVisible(double))
			}
			Then("the Scene rename input box should contain the Scene's name") {
				Assertions.assertTrue(SceneListDriver.isRenameInputBoxShowingNameOfSelected(double))
			}
			Then("the Scene rename input box should be replaced by the Scene name") {
				Assertions.assertFalse(SceneListDriver.isRenameInputBoxVisible(double))
			}
			Then("the Scene name should be the new name") {
				val item = SceneListDriver.getItems(double).find {
					it.value!!.id == (targetObject as SceneItemViewModel).id
				}
				val matching = item!!.value!!.name == (targetObject as SceneItemViewModel).name
				Assertions.assertFalse(matching)
			}
			Then("the Scene name should be the original name") {
				Assertions.assertTrue(SceneListDriver.isSelectedItemNameMatching(double, (targetObject as SceneItemViewModel).name))
			}
			Then("The Scene List Tool should not show the deleted Scene") {
				Assertions.assertFalse(SceneListDriver.isShowingScene(double, (targetObject as Scene)))
			}
			Then("the Confirm Delete Scene Dialog should be opened") {
				Assertions.assertTrue(DeleteSceneDialogDriver.openDialog.check(double))
			}
			Then("the Confirm Delete Scene Dialog should show the Scene name") {
				Assertions.assertTrue(DeleteSceneDialogDriver.isShowingNameOf((targetObject as SceneItemViewModel)).check(double))
			}
			Then("the Scene List Tool should show the new Scene") {
				Assertions.assertTrue(SceneListDriver.isShowingScene(double, createdScene!!))
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
		}
	}

}