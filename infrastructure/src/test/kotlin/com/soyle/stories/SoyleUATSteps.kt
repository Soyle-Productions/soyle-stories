package com.soyle.stories

import com.soyle.stories.character.CharacterArcSteps
import com.soyle.stories.character.CharacterSteps
import com.soyle.stories.character.CreateCharacterDialogDriver
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.location.LocationListDriver
import com.soyle.stories.location.LocationSteps
import com.soyle.stories.project.CreateProjectDialogDriver
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.project.WorkBenchDriver
import com.soyle.stories.scene.CreateSceneDialogDriver
import com.soyle.stories.scene.DeleteSceneDialogDriver
import com.soyle.stories.scene.SceneListDriver
import com.soyle.stories.scene.SceneSteps
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import io.cucumber.java8.En
import io.cucumber.java8.Scenario
import javafx.scene.input.KeyCode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import java.util.*

class SoyleUATSteps : En, ApplicationTest() {

	private val double = SoyleStoriesTestDouble()

	private var targetObject: Any? = null

	private var targetLocation: Location? = null
	private var recentlyCreatedLocationName: String = ""
	private var deletedLocation: Location? = null

	private var recentlyCreatedCharacter: Character? = null
	private var deletedCharacter: Character? = null

	init {
		Given("A project has been opened") {
			// ProjectDriver.givenHasBeenOpened(double)
			ProjectSteps.givenProjectHasBeenOpened(double)
		}
		Given("The Location List Tool has been opened") {
			// LocationListDriver.givenHasBeenOpened(double)
			LocationSteps.givenLocationListToolHasBeenOpened(double)
		}
		Given("A location has been created") {
			// LocationRepoDriver.givenNumberHaveBeenCreated(double, 1)
			LocationSteps.givenNumberOfLocationsHaveBeenCreated(double, 1)
		}
		Given("The create new location dialog has been opened") {
			// CreateNewLocationDialogDriver.givenHasBeenOpened(double)
			LocationSteps.givenCreateNewLocationDialogHasBeenOpened(double)
		}
		Given("The user has entered an invalid location name") {
			// CreateNewLocationDialogDriver.givenInvalidNameHasBeenEntered(double)
			LocationSteps.givenUserHasEnteredInvalidLocationNameInCreateLocationDialog(double)
		}
		Given("The user has entered a valid location name") {
			// CreateNewLocationDialogDriver.givenValidNameHasBeenEntered(double)
			LocationSteps.givenUserHasEnteredValidLocationNameInCreateLocationDialog(double)
		}
		Given("{int} Locations have been created") { int1: Int ->
			// LocationRepoDriver.givenNumberHaveBeenCreated(double, count)
			LocationSteps.givenNumberOfLocationsHaveBeenCreated(double, int1)
		}
		Given("a location has been selected") {
			// LocationListDriver.givenLocationHasBeenSelected(double)
			LocationSteps.givenLocationIsSelectedInLocationListTool(double)
		}
		Given("the location right-click menu is open") {
			// LocationListDriver.givenRightClickMenuHasBeenOpened(double)
			LocationSteps.givenLocationRightClickMenuIsOpenInLocationListTool(double)
			targetLocation = LocationSteps.getLocationsCreated(double).first()
		}
		Given("the delete location dialog has been opened") {
			// ConfirmDeleteLocationDialogDriver.givenHasBeenOpened(double)
			LocationSteps.givenConfirmDeleteDialogHasBeenOpened(double)
		}
		Given("the location rename input box is visible") {
			// LocationListDriver.givenRenameInputBoxIsVisible(double)
			LocationSteps.givenLocationRenameInputBoxIsVisible(double)
		}
		Given("the user has entered a valid Location name") {
			// LocationListDriver.givenValidNameHasBeenEnteredInRenameInputBox(double)
			LocationSteps.givenUserHasEnteredValidLocationNameInRenameInputBox(double)
		}
		Given("the Location Details Tool has been opened") {
			LocationSteps.givenNumberOfLocationsHaveBeenCreated(double, 1)
			targetLocation = LocationSteps.getLocationsCreated(double).first()
			LocationSteps.givenLocationDetailsToolHasBeenOpened(double, targetLocation!!.id.uuid)
		}
		Given("the target Location has a description of {string}") { string: String ->
			LocationSteps.givenLocationHasDescription(double, targetLocation!!.id.uuid, string)
			targetLocation = LocationSteps.getLocationsCreated(double).first()
		}
		Given("the user has entered {string} into the description field") { string: String ->
			LocationSteps.givenLocationDetailsToolHasDescriptionOf(double, targetLocation!!.id.uuid, string)
		}
		Given("a Character has been created") {
			CharacterSteps.givenANumberOfCharactersHaveBeenCreated(double, 1)
		}
		Given("a Character Arc has been created") {
			CharacterArcSteps.givenANumberOfCharacterArcsHaveBeenCreated(double, 1)
		}
		Given("no Locations have been created") {
			LocationSteps.givenNoLocationsHaveBeenCreated(double)
		}
		Given("at least one Location has been created") {
			LocationSteps.givenNumberOfLocationsHaveBeenCreated(double, 1)
		}
		Given("the Base Story Structure Tool has been opened") {
			CharacterArcSteps.givenANumberOfCharacterArcsHaveBeenCreated(double, 1)
			val arc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			CharacterArcSteps.givenBaseStoryStructureToolHasBeenOpened(double, arc.themeId, arc.characterId)
		}
		Given("the Character Arc Section Location dropdown menu has been opened") {
			CharacterArcSteps.givenANumberOfCharacterArcsHaveBeenCreated(double, 1)
			val arc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			CharacterArcSteps.givenCharacterArcSectionLocationDropDownMenuHasBeenOpened(double, arc.themeId, arc.characterId)
		}
		Given("a Character Arc Section has a linked Location") {
			CharacterArcSteps.givenANumberOfCharacterArcsHaveBeenCreated(double, 1)
			val arc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			val section = CharacterArcSteps.getCharacterArcSectionsForCharacter(double, arc.characterId).find { it.template.isRequired }!!
			LocationSteps.givenNumberOfLocationsHaveBeenCreated(double, 1)
			val location = LocationSteps.getLocationsCreated(double).first()
			CharacterArcSteps.givenCharacterArcSectionHasALinkedLocation(double, section.id, location.id)
		}
		Given("{int} Characters have been created") { int1: Int ->
			CharacterSteps.givenANumberOfCharactersHaveBeenCreated(double, int1)
		}
		Given("The Character List Tool has been opened") {
			// LocationListDriver.givenHasBeenOpened(double)
			CharacterSteps.givenCharacterListToolHasBeenOpened(double)
		}
		Given("A Character has been created") {
			CharacterSteps.givenANumberOfCharactersHaveBeenCreated(double, 1)
		}
		Given("the Character right-click menu has been opened") {
			CharacterSteps.givenCharacterListToolCharacterContextMenuHasBeenOpened(double)
			//targetLocation = LocationSteps.getLocationsCreated(double).first()
		}
		Given("a Character has been selected") {
			CharacterSteps.givenCharacterIsSelectedInCharacterListTool(double)
		}
		Given("the Character rename input box is visible") {
			CharacterSteps.givenCharacterListToolShowingInputBoxForSelectedItem(double)
		}
		Given("the user has entered a valid Character name") {
			CharacterSteps.givenValidCharacterNameHasBeenEnteredInCharacterListToolCharacterRenameInputBox(double)
		}
		Given("the user has entered an invalid Character name") {
			CharacterSteps.givenInvalidCharacterNameHasBeenEnteredInCharacterListToolCharacterRenameInputBox(double)
		}
		Given("the Create Character Dialog has been opened") {
			CreateCharacterDialogDriver.givenHasBeenOpened(double)
		}
		Given("the Create Character Dialog Name input has an invalid Character Name") {
			CreateCharacterDialogDriver.givenNameInputHasInvalidCharacterName(double)
		}
		Given("the Create Character Dialog Name input has a valid Character Name") {
			CreateCharacterDialogDriver.givenNameInputHasValidCharacterName(double)
		}
		Given("the Location rename input box is visible") {
			LocationListDriver.givenRenameInputBoxIsVisible(double)
		}
		Given("the File Menu has been opened") {
			WorkBenchDriver.givenMenuHasBeenOpened(double, "file")
		}
		Given("the File New Menu has been opened") {
			WorkBenchDriver.givenMenuHasBeenOpened(double, "file", "file_new")
		}
		Given("the {string} tool has been opened") { toolName: String ->
			when (toolName) {
				"Characters" -> CharacterSteps.givenCharacterListToolHasBeenOpened(double)
				"Locations" -> LocationSteps.givenLocationListToolHasBeenOpened(double)
				"Scenes" -> SceneListDriver.givenHasBeenOpened(double)
				else -> error("no tool of type $toolName")
			}
		}
		Given("the {string} tool has been closed") { toolName: String ->
			when (toolName) {
				"Characters" -> CharacterSteps.givenCharacterListToolHasBeenClosed(double)
				"Locations" -> LocationSteps.givenLocationListToolHasBeenClosed(double)
				"Scenes" -> SceneListDriver.givenHasBeenClosed(double)
				else -> error("no tool of type $toolName")
			}
		}
		Given("the Tools Menu has been opened") {
			WorkBenchDriver.givenMenuHasBeenOpened(double, "tools")
		}
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
			SceneSteps.givenNumberOfCreatedScenesIsAtLeast(double, count)
		}
		Given("The Scene List Tool has been opened") {
			SceneListDriver.givenHasBeenOpened(double)
		}
		Given("A Scene has been created") {
			SceneSteps.givenNumberOfCreatedScenesIsAtLeast(double, 1)
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
		Given("The Scene List Tool tab has been selected") {
			SceneListDriver.givenHasBeenVisible(double)
		}


		When("User selects the file->new->location menu option") {
			ProjectSteps.whenMenuItemIsSelected(double, "file", "file_new", "file_new_location")
		}
		When("User clicks the center create new location button") {
			LocationSteps.whenLocationListToolCenterButtonIsClicked(double)
		}
		When("User clicks the bottom create new location button") {
			LocationSteps.whenLocationListToolActionBarCreateButtonIsClicked(double)
		}
		When("The user presses the Enter key") {
			interact {
				press(KeyCode.ENTER).release(KeyCode.ENTER)
			}
		}
		When("The user clicks the Create button") {
			LocationSteps.whenCreateLocationDialogCreateButtonIsClicked(double)
		}
		When("The user presses the Esc key") {
			interact {
				press(KeyCode.ESCAPE).release(KeyCode.ESCAPE)
			}
		}
		When("The user clicks the Cancel button") {
			LocationSteps.whenCreateLocationDialogCancelButtonIsClicked(double)
		}
		When("The Location List Tool is opened") {
			LocationSteps.whenLocationListToolIsOpened(double)
		}
		When("A new Location is created") {
			recentlyCreatedLocationName = LocationSteps.whenLocationIsCreated(double)
		}
		When("A Location is deleted") {
			deletedLocation = LocationSteps.whenLocationIsDeleted(double)
		}
		When("the user clicks the location list tool right-click menu delete button") {
			LocationSteps.whenLocationListToolRightClickMenuButtonIsClicked(double, "delete")
		}
		When("the user clicks the location list tool right-click menu Rename button") {
			LocationSteps.whenLocationListToolRightClickMenuButtonIsClicked(double, "rename")
		}
		When("the user clicks the location list tool delete button") {
			LocationSteps.whenLocationListToolActionBarDeleteButtonIsClicked(double)
		}
		When("the user clicks the confirm delete location dialog delete button") {
			LocationSteps.whenConfirmDeleteDialogButtonIsClicked(double, true)
		}
		When("the user clicks the confirm delete location dialog cancel button") {
			LocationSteps.whenConfirmDeleteDialogButtonIsClicked(double, false)
		}
		When("The create new location dialog is reopened") {
			LocationSteps.whenCreateLocationDialogIsOpened(double)
		}
		When("the user clicks the location list tool right-click menu open button") {
			LocationSteps.whenLocationListToolRightClickMenuButtonIsClicked(double, "open")
		}
		When("the user closes the Location Details Tool") {
			LocationSteps.whenLocationDetailsToolIsClosed(double, targetLocation!!.id.uuid)
		}
		When("the Location Details Tool is reopened with the same Location") {
			LocationSteps.whenLocationDetailsToolIsOpened(double, targetLocation!!.id.uuid)
		}
		When("the Base Story Structure Tool is opened") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			CharacterArcSteps.whenBaseStoryStructureToolIsOpened(double, characterArc.themeId, characterArc.characterId)
		}
		When("the Character Arc Section Location dropdown is clicked") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			CharacterArcSteps.whenCharacterArcSectionLocationDropDownIsClicked(double, characterArc.themeId, characterArc.characterId)
		}
		When("a Location in the Character Arc Section Location dropdown menu is selected") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			val location = LocationSteps.getLocationsCreated(double).first()
			CharacterArcSteps.whenLocationInCharacterArcSectionLocationDropdownIsSelected(double, characterArc.themeId, characterArc.characterId, location)
		}
		When("the user clicks outside the Character Arc Section Location dropdown menu") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			CharacterArcSteps.whenCharacterArcSectionLocationDropDownLosesFocus(double, characterArc.themeId, characterArc.characterId)
		}
		When("the selected Location in in Character Arc Section Location Dropdown is deselected") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			CharacterArcSteps.whenSelectedLocationInCharacterArcSectionLocationDropdownIsDeselected(double, characterArc.themeId, characterArc.characterId)
		}
		When("The Character List Tool is opened") {
			CharacterSteps.whenCharacterListToolIsOpened(double)
		}
		When("A new Character is created") {
			recentlyCreatedCharacter = CharacterSteps.whenCharacterIsCreated(double)
		}
		When("A Character is deleted") {
			deletedCharacter = CharacterSteps.whenCharacterIsDeleted(double)
		}
		When("the user clicks the Character List Tool right-click menu delete button") {
			CharacterSteps.whenCharacterListToolCharacterContextMenuButtonIsClicked(double, "delete")
		}
		When("the user clicks the Character List Tool delete button") {
			CharacterSteps.whenCharacterListToolActionBarDeleteButtonIsClicked(double)
		}
		When("the user clicks the Character List Tool right-click menu Rename button") {
			CharacterSteps.whenCharacterListToolCharacterContextMenuButtonIsClicked(double, "rename")
		}
		When("The user clicks away from the input box") {
			ProjectSteps.whenUserClicksAway(double)
		}
		When("the user commits the rename") {
			interact {
				press(KeyCode.ENTER).release(KeyCode.ENTER)
			}
		}
		When("the File New Menu is opened") {
			WorkBenchDriver.whenMenuIsOpened(double, "file", "file_new")
		}
		When("the File New {string} option is selected") { menuItemText: String ->
			WorkBenchDriver.whenMenuItemIsSelected(double, "file", "file_new", menuItemText = menuItemText)
		}
		When("the Tools Menu is opened") {
			WorkBenchDriver.whenMenuIsOpened(double, "tools")
		}
		When("the {string} tool item is selected") { menuItemText: String ->
			WorkBenchDriver.whenMenuItemIsSelected(double, "tools", menuItemText = menuItemText)
		}
		When("The Scene List Tool is opened") {
			if (SceneListDriver.isOpen(double)) {
				SceneListDriver.whenClosed(double)
			}
			SceneListDriver.whenOpened(double)
		}
		When("A new Scene is created") {
			SceneSteps.whenSceneIsCreated(double)
			targetObject = SceneSteps.getCreatedScenes(double).last()
		}
		When("the user clicks the Scene List Tool right-click menu Rename button") {
			SceneListDriver.whenRightClickOptionIsClicked(double, "rename")
		}
		When("A Scene is deleted") {
			val existingScenes = SceneSteps.getCreatedScenes(double)
			SceneSteps.whenSceneIsDeleted(double)
			targetObject = (existingScenes.toSet() - SceneSteps.getCreatedScenes(double).toSet()).single()
		}
		When("the user clicks the Scene List Tool right-click menu delete button") {
			SceneListDriver.whenRightClickOptionIsClicked(double, "delete")
			targetObject = SceneListDriver.getSelectedItem(double)
		}
		When("the user clicks the Scene List Tool delete button") {
			SceneListDriver.whenBottomButtonIsClicked(double, "delete")
			targetObject = SceneListDriver.getSelectedItem(double)
		}


		Then("The Location List Tool should show a special empty message") {
			assertTrue(LocationSteps.locationListToolShowsEmptyMessage(double))
		}
		Then("The Location List Tool should show all {int} locations") { locationCount: Int ->
			assertTrue(LocationSteps.locationListToolShowsNumberOfLocations(double, locationCount))
		}
		Then("The Location List Tool should show the new Location") {
			assertTrue(LocationSteps.locationListToolShowsLocationWithName(double, recentlyCreatedLocationName))
		}
		Then("The Location List Tool should not show the deleted Location") {
			assertFalse(LocationSteps.locationListToolShowsLocationWithName(double, deletedLocation!!.name))
		}
		Then("The create new location dialog should be open") {
			assertTrue(LocationSteps.isCreateNewLocationDialogOpen(double))
		}
		Then("An error message should be visible in the create new location dialog") {
			assertTrue(LocationSteps.createNewLocationDialogShowsErrorMessage(double))
		}
		Then("The create new location dialog should be closed") {
			assertFalse(LocationSteps.isCreateNewLocationDialogOpen(double))
		}
		Then("the confirm delete location dialog should be opened") {
			assertTrue(LocationSteps.isConfirmDeleteLocationDialogOpen(double))
			targetLocation = LocationSteps.getLocationSelectedInLocationListTool(double)!!.let {
				Location(Location.Id(UUID.fromString(it.id)), Project.Id(UUID.randomUUID()), it.name, "")
			}
		}
		Then("the delete location dialog should be closed") {
			assertFalse(LocationSteps.isConfirmDeleteLocationDialogOpen(double))
		}
		Then("the location's name should be replaced by an input box") {
			assertTrue(LocationSteps.locationListToolShowsInputBoxForSelectedItem(double))
		}
		Then("the location rename input box should contain the location's name") {
			assertTrue(LocationSteps.locationListToolRenameInputBoxContainsSelectedItemName(double))
		}
		Then("the location rename input box should be replaced by the location name") {
			assertTrue(LocationSteps.locationListToolShowsLocationNameForSelectedItem(double))
		}
		Then("the location name should be the original name") {
			assertTrue(LocationSteps.locationListToolShowsOriginalLocationNameForSelectedItem(double))
		}
		Then("the location name should be the new name") {
			assertTrue(LocationSteps.locationListToolShowsChangedLocationNameForSelectedItem(double))
		}
		Then("the location name field should be blank") {
			assertTrue(LocationSteps.locationNameIsBlankInCreateLocationDialog(double))
		}
		Then("the Location Details Tool should be open") {
			assertTrue(LocationSteps.isLocationDetailsToolOpen(double, targetLocation!!.id.uuid))
		}
		Then("the description field text should be {string}") { string: String ->
			assertEquals(string, LocationSteps.getDescriptionInLocationDetailsTool(double, targetLocation!!.id.uuid))
		}
		Then("the confirm delete location dialog should show the location name") {
			assertTrue(LocationSteps.isConfirmDeleteLocationDialogLocationDisplayingNameOf(double, targetLocation!!))
		}
		Then("the Character Arc Section Location dropdown in the Base Story Structure Tool should be disabled") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			assertTrue(CharacterArcSteps.isLocationDropdownDisabledInBaseStoryStructureTool(double, characterArc.themeId, characterArc.characterId))
		}
		Then("the Character Arc Section Location dropdown in the Base Story Structure Tool should not be disabled") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			assertFalse(CharacterArcSteps.isLocationDropdownDisabledInBaseStoryStructureTool(double, characterArc.themeId, characterArc.characterId))
		}
		Then("all Locations should be listed in the Character Arc Section Location dropdown menu") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			val locations = LocationSteps.getLocationsCreated(double)
			CharacterArcSteps.isCharacterArcSectionLocationOpenWithAllLocations(double, characterArc.themeId, characterArc.characterId, locations)
			  .let(::assertTrue)
		}
		Then("the Character Arc Section Location dropdown should show the selected Location name") {
			val arc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			val location = LocationSteps.getLocationsCreated(double).first()
			CharacterArcSteps.isCharacterArcSectionLocationDropdownDisplayingLocation(double, arc.themeId, arc.characterId, location)
			  .let(::assertTrue)
		}
		Then("the Character Arc Section Location dropdown menu should be closed") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			CharacterArcSteps.isCharacterArcSectionLocationOpen(double, characterArc.themeId, characterArc.characterId)
			  .let(::assertFalse)
		}
		Then("the Character Arc Section Location dropdown should show an empty state") {
			val characterArc = CharacterArcSteps.getCharacterArcsCreated(double).first()
			CharacterArcSteps.isCharacterArcSectionLocationDropdownDisplayingEmptyState(double, characterArc.themeId, characterArc.characterId)
			  .let(Assertions::assertTrue)
		}
		Then("The Character List Tool should show a special empty message") {
			assertTrue(CharacterSteps.isCharacterListToolShowingEmptyMessage(double))
		}
		Then("The Character List Tool should show all {int} Characters") { characterCount: Int ->
			assertTrue(CharacterSteps.isCharacterListToolShowingNumberOfCharacters(double, characterCount))
		}
		Then("The Character List Tool should show the new Character") {
			assertTrue(CharacterSteps.isCharacterListToolShowingCharacter(double, recentlyCreatedCharacter!!))
		}
		Then("The Character List Tool should not show the deleted Character") {
			assertFalse(CharacterSteps.isCharacterListToolShowingCharacter(double, deletedCharacter!!))
		}
		Then("the Confirm Delete Character Dialog should be opened") {
			assertTrue(CharacterSteps.isConfirmDeleteCharacterDialogOpen(double))
			targetObject = CharacterSteps.getCharacterSelectedInCharacterListTool(double)!!.let {
				Character(Character.Id(UUID.fromString(it.id)), UUID.randomUUID(), it.name)
			}
		}
		Then("the Confirm Delete Character Dialog should show the Character name") {
			assertTrue(CharacterSteps.isConfirmDeleteCharacterDialogDisplayingNameOf(double, targetObject as Character))
		}
		Then("the Character's name should be replaced by an input box") {
			assertTrue(CharacterSteps.isCharacterListToolShowingInputBoxForSelectedItem(double))
		}
		Then("the Character rename input box should contain the Character's name") {
			assertTrue(CharacterSteps.isCharacterListToolRenameInputBoxContainingSelectedItemName(double))
		}
		Then("the Character rename input box should be replaced by the Character name") {
			assertFalse(CharacterSteps.isCharacterListToolShowingInputBoxForSelectedItem(double))
		}
		Then("the Character name should be the new name") {
			assertTrue(CharacterSteps.isCharacterListToolShowingNameStoredForSelectedItem(double))
		}
		Then("the Character name should be the original name") {
			assertTrue(CharacterSteps.isCharacterListToolShowingNameStoredForSelectedItem(double))
		}
		Then("an error message should be displayed in the Create Character Dialog") {
			assertTrue(CreateCharacterDialogDriver.isErrorMessageShown(double))
		}
		Then("the Create Character Dialog should be closed") {
			assertFalse(CreateCharacterDialogDriver.isOpen(double))
		}
		Then("a new Character should be created") {}
		Then("the Location rename input box should be replaced by the Location name") {
			assertFalse(LocationListDriver.isRenameInputBoxVisible(double))
		}
		Then("the Location name should be the new name") {
			LocationListDriver.isLocationShowingStoredName(double)
		}
		Then("the Location name should be the original name") {
			LocationListDriver.isLocationShowingStoredName(double)
		}
		Then("the Character rename input box should be visible") {
			assertTrue(CharacterSteps.isCharacterListToolShowingInputBoxForSelectedItem(double))
		}
		Then("the Character rename input box should show an error message") {
			assertTrue(CharacterSteps.isCharacterListToolShowingErrorOnInputBoxForSelectedItem(double))
		}
		Then("the File New Menu should display {string}") { menuText: String ->
			assertTrue(WorkBenchDriver.isMenuItemVisible(double, "file", "file_new", menuItemText = menuText))
		}
		Then("the Create New {string} Dialog should be open") { domainObject: String ->
			val isOpen = when (domainObject) {
				"Project" -> CreateProjectDialogDriver.isOpen(double)
				"Character" -> CreateCharacterDialogDriver.isOpen(double)
				"Location" -> LocationSteps.isCreateNewLocationDialogOpen(double)
				"Scene" -> CreateSceneDialogDriver.isOpen(double)
				else -> false
			}
			assertTrue(isOpen)
		}
		Then("the Tools Menu should display {string}") { menuText: String ->
			assertTrue(WorkBenchDriver.isMenuItemVisible(double, "tools", menuItemText = menuText))
		}
		Then("the Tools Menu {string} option should be checked") { menuText: String ->
			assertTrue(WorkBenchDriver.isMenuItemChecked(double, "tools", menuItemText = menuText))
		}
		Then("the Tools Menu {string} option should be unchecked") { menuText: String ->
			assertFalse(WorkBenchDriver.isMenuItemChecked(double, "tools", menuItemText = menuText))
		}
		Then("the {string} tool should be open") { toolName: String ->
			val isOpen = when (toolName) {
				"Characters" -> CharacterSteps.isCharacterListToolOpen(double)
				"Locations" -> LocationSteps.isLocationListToolOpen(double)
				"Scenes" -> SceneListDriver.isOpen(double)
				else -> false
			}
			assertTrue(isOpen)
		}
		Then("the {string} tool should be closed") { toolName: String ->
			val isOpen = when (toolName) {
				"Characters" -> CharacterSteps.isCharacterListToolOpen(double)
				"Locations" -> LocationSteps.isLocationListToolOpen(double)
				"Scenes" -> SceneListDriver.isOpen(double)
				else -> error("no registered tool with name $toolName")
			}
			assertFalse(isOpen)
		}
		Then("an error message should be displayed in the Create Scene Dialog") {
			assertTrue(CreateSceneDialogDriver.isErrorMessageShown(double))
		}
		Then("the Create Scene Dialog should be closed") {
			assertFalse(CreateSceneDialogDriver.isOpen(double))
		}
		Then("a new Scene should be created") {
			assertTrue(SceneSteps.getNumberOfCreatedScenes(double) >= 1)
		}
		Then("The Scene List Tool should show a special empty message") {
			assertTrue(SceneListDriver.isShowingEmptyMessage(double))
		}
		Then("The Scene List Tool should show all {int} scenes") { count: Int ->
			assertTrue(SceneListDriver.isShowingNumberOfScenes(double, count))
		}
		Then("The Scene List Tool should show the new Scene") {
			assertTrue(SceneListDriver.isShowingScene(double, targetObject as Scene))
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
			assertTrue(DeleteSceneDialogDriver.isOpen(double))
		}
		Then("the Confirm Delete Scene Dialog should show the Scene name") {
			assertTrue(DeleteSceneDialogDriver.isShowingNameOf(double, (targetObject as SceneItemViewModel)))
		}

		After { _: Scenario ->
			if (double.isStarted()) {
				FxToolkit.cleanupStages()
				FxToolkit.cleanupApplication(double.application)
			}
		}
	}

}