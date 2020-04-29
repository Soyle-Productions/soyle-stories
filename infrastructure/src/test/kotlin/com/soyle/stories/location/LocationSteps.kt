package com.soyle.stories.location

import com.soyle.stories.common.async
import com.soyle.stories.common.editingCell
import com.soyle.stories.common.isEditing
import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.location.LocationSteps.interact
import com.soyle.stories.location.controllers.CreateNewLocationController
import com.soyle.stories.location.controllers.DeleteLocationController
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import com.soyle.stories.location.locationDetails.LocationDetails
import com.soyle.stories.location.locationDetails.LocationDetailsScope
import com.soyle.stories.location.locationList.*
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.project.WorkBenchModel
import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import javafx.event.ActionEvent
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.text.Text
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.find
import tornadofx.selectFirst
import tornadofx.toObservable
import java.util.*

object LocationSteps : ApplicationTest() {

	// location list tool is opened

	fun setLocationListToolOpened(double: SoyleStoriesTestDouble) {
		ProjectSteps.givenProjectHasBeenOpened(double)
		whenLocationListToolIsOpened(double)
	}

	fun getOpenLocationListTool(double: SoyleStoriesTestDouble): LocationList?
	{
		val projectScope = ProjectSteps.getProjectScope(double) ?: return null
		return findComponentsInScope<LocationList>(projectScope).singleOrNull()?.takeIf { it.currentStage?.isShowing == true }
	}

	fun isLocationListToolOpen(double: SoyleStoriesTestDouble): Boolean = getOpenLocationListTool(double) != null

	fun whenLocationListToolIsOpened(double: SoyleStoriesTestDouble) {
		val menuItem: MenuItem = ProjectSteps.getMenuItem(double, "tools", "tools_Locations")!!
		interact {
			menuItem.fire()
		}
	}

	fun givenLocationListToolHasBeenOpened(double: SoyleStoriesTestDouble) {
		if (!isLocationListToolOpen(double)) {
			setLocationListToolOpened(double)
		}
		assertTrue(isLocationListToolOpen(double))
	}

	// location creation

	fun setLocationCreated(double: SoyleStoriesTestDouble)
	{
		ProjectSteps.givenProjectHasBeenOpened(double)
		whenLocationIsCreated(double)
	}

	fun getLocationsCreated(double: SoyleStoriesTestDouble): List<Location>
	{
		val projectScope = ProjectSteps.getProjectScope(double) ?: return emptyList()
		var locations: List<Location> = emptyList()
		interact {
			val locationRepo: LocationRepository = projectScope.get()
			async(double.application.scope as ApplicationScope) {
				locations = locationRepo.getAllLocationsInProject(Project.Id(projectScope.projectId))
			}
		}
		return locations
	}

	fun getNumberOfLocationsCreated(double: SoyleStoriesTestDouble): Int = getLocationsCreated(double).size

	fun whenLocationIsCreated(double: SoyleStoriesTestDouble): String {
		val scope = ProjectSteps.getProjectScope(double)!!
		val name = "New Location ${UUID.randomUUID()}"
		interact {
			async(scope.applicationScope) {
				DI.resolve<CreateNewLocationController>(scope).createNewLocation(name, "")
			}
		}
		return name
	}

	fun givenNumberOfLocationsHaveBeenCreated(double: SoyleStoriesTestDouble, number: Int) {
		val numberOfLocationsCreated = getNumberOfLocationsCreated(double)
		if (numberOfLocationsCreated < number) {
			repeat(number - numberOfLocationsCreated) {
				setLocationCreated(double)
			}
		}
		assertThat(getNumberOfLocationsCreated(double)).isGreaterThanOrEqualTo(number)
	}

	// create new location dialog open

	fun setCreateNewLocationDialogOpen(double: SoyleStoriesTestDouble) {
		ProjectSteps.givenProjectHasBeenOpened(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			find<WorkBenchModel>(scope).openDialogs.let {
				it.set((it.get() + (Dialog.CreateLocation::class to Dialog.CreateLocation)).toObservable())
			}
		}
	}

	fun isCreateNewLocationDialogOpen(double: SoyleStoriesTestDouble): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		val isOpen: Boolean
		val dialog = findComponentsInScope<CreateLocationDialog>(projectScope).singleOrNull()
		isOpen = dialog != null && (dialog.currentStage?.isShowing == true)
		return isOpen
	}

	fun givenCreateNewLocationDialogHasBeenOpened(double: SoyleStoriesTestDouble) {
		if (!isCreateNewLocationDialogOpen(double)) {
			setCreateNewLocationDialogOpen(double)
		}
		assertTrue(isCreateNewLocationDialogOpen(double))
	}

	// location details tool
	fun setLocationDetailsToolOpened(double: SoyleStoriesTestDouble, locationId: UUID)
	{
		givenNumberOfLocationsHaveBeenCreated(double, 1)
		val scope = ProjectSteps.getProjectScope(double)!!
		scope.get<LocationListViewListener>().openLocationDetails(locationId.toString())
	}

	fun getOpenedLocationDetailsTool(double: SoyleStoriesTestDouble, locationId: UUID): LocationDetails?
	{
		val projectScope = ProjectSteps.getProjectScope(double) ?: return null
		val scope = projectScope.toolScopes.find { it is LocationDetailsScope && it.locationId == locationId.toString() }
		  ?: return null
		return findComponentsInScope<LocationDetails>(scope).singleOrNull()?.takeIf { it.currentStage?.isShowing == true }
	}

	fun isLocationDetailsToolOpen(double: SoyleStoriesTestDouble, locationId: UUID) = getOpenedLocationDetailsTool(double, locationId) != null

	fun givenLocationDetailsToolHasBeenOpened(double: SoyleStoriesTestDouble, locationId: UUID) {
		if (!isLocationDetailsToolOpen(double, locationId)) {
			setLocationDetailsToolOpened(double, locationId)
		}
		assertTrue(isLocationDetailsToolOpen(double, locationId))
	}


	fun isLocationSelectedInLocationListTool(double: SoyleStoriesTestDouble): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		val locationList = findComponentsInScope<LocationList>(projectScope).singleOrNull() ?: return false
		var selected = false
		interact {
			selected = (locationList.root.lookup(".tree-view") as TreeView<*>)
			  .selectionModel.selectedItem != null
		}
		return selected
	}

	fun setLocationSelectedInLocaitonListTool(double: SoyleStoriesTestDouble) {
		givenNumberOfLocationsHaveBeenCreated(double, 1)
		givenLocationListToolHasBeenOpened(double)
		//givenLocationListToolHasBeenVisible(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			(find<LocationList>(scope).root.lookup(".tree-view") as TreeView<*>)
			  .selectFirst()
		}
	}

	fun givenLocationIsSelectedInLocationListTool(double: SoyleStoriesTestDouble) {
		if (!isLocationSelectedInLocationListTool(double)) {
			setLocationSelectedInLocaitonListTool(double)
		}
		assertTrue(isLocationSelectedInLocationListTool(double))
	}

	fun locationRightClickMenuIsOpenInLocationListTool(double: SoyleStoriesTestDouble): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		val locationList = findComponentsInScope<LocationList>(projectScope).singleOrNull() ?: return false
		var isOpen = false
		interact {
			val treeView = (locationList.root.lookup(".tree-view") as TreeView<*>)
			isOpen = treeView.contextMenu?.isShowing ?: false
		}
		return isOpen
	}

	fun openLocationRightClickMenuInLocationListTool(double: SoyleStoriesTestDouble) {
		givenLocationIsSelectedInLocationListTool(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			val treeView = (find<LocationList>(scope).root.lookup(".tree-view") as TreeView<*>)
			treeView.contextMenu!!.show(treeView, Side.TOP, 0.0, 0.0)
		}
	}

	fun locationRenameInputBoxIsVisible(double: SoyleStoriesTestDouble): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		val populatedDisplay = findComponentsInScope<PopulatedDisplay>(projectScope).singleOrNull() ?: return false
		val isEditing = from(populatedDisplay.root).lookup(".tree-view").query<TreeView<*>>().isEditing
		val isVisible = from(populatedDisplay.root).lookup(".tree-view").query<TreeView<*>>().editingCell?.graphic?.isVisible ?: false
		return isEditing && isVisible
	}

	fun showLocationRenameInputBox(double: SoyleStoriesTestDouble) {
		givenLocationIsSelectedInLocationListTool(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			val locationList = scope.get<LocationList>()
			locationList.owningTab?.let {
				it.tabPane.selectionModel.select(it)
			}
			val treeView = (locationList.root.lookup(".tree-view") as TreeView<LocationItemViewModel?>)
			treeView.edit(treeView.selectionModel.selectedItem)
		}
	}

	fun givenLocationRenameInputBoxIsVisible(double: SoyleStoriesTestDouble) {
		if (!locationRenameInputBoxIsVisible(double)) {
			showLocationRenameInputBox(double)
		}
		assertTrue(locationRenameInputBoxIsVisible(double))
	}

	fun createLocationDialogLocationNameIsInvalid(double: SoyleStoriesTestDouble): Boolean {
		if (!isCreateNewLocationDialogOpen(double)) return false
		val scope = ProjectSteps.getProjectScope(double) ?: return false
		var isInvalid = false
		interact {
			val name = findComponentsInScope<CreateLocationDialog>(scope).singleOrNull()?.name?.get()
			isInvalid = name != null && name.isBlank()
		}
		return isInvalid
	}

	fun whenUserEntersInvalidLocationNameInCreatedLocationDialog(double: SoyleStoriesTestDouble) {
		givenCreateNewLocationDialogHasBeenOpened(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			val textInput = from(find<CreateLocationDialog>(scope).root).lookup("#name").queryTextInputControl()
			textInput.requestFocus()
			textInput.textProperty().set("  ")
		}
	}

	fun givenUserHasEnteredInvalidLocationNameInCreateLocationDialog(double: SoyleStoriesTestDouble) {
		if (!createLocationDialogLocationNameIsInvalid(double)) {
			whenUserEntersInvalidLocationNameInCreatedLocationDialog(double)
		}
		assertTrue(createLocationDialogLocationNameIsInvalid(double))
	}

	fun createLocationDialogLocationNameIsValid(double: SoyleStoriesTestDouble): Boolean {
		if (!isCreateNewLocationDialogOpen(double)) return false
		val scope = ProjectSteps.getProjectScope(double) ?: return false
		var isInvalid = false
		interact {
			val name = findComponentsInScope<CreateLocationDialog>(scope).singleOrNull()?.name?.get()
			isInvalid = name != null && name.isNotBlank()
		}
		return isInvalid
	}

	fun whenUserEntersValidLocationNameInCreatedLocationDialog(double: SoyleStoriesTestDouble) {
		givenCreateNewLocationDialogHasBeenOpened(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			from(find<CreateLocationDialog>(scope).root).lookup("#name").queryTextInputControl().textProperty().set("Valid Location Name")
		}
	}

	fun givenUserHasEnteredValidLocationNameInCreateLocationDialog(double: SoyleStoriesTestDouble) {
		if (!createLocationDialogLocationNameIsValid(double)) {
			whenUserEntersValidLocationNameInCreatedLocationDialog(double)
		}
		assertTrue(createLocationDialogLocationNameIsValid(double))
	}

	fun givenLocationRightClickMenuIsOpenInLocationListTool(double: SoyleStoriesTestDouble) {
		if (!locationRightClickMenuIsOpenInLocationListTool(double)) {
			openLocationRightClickMenuInLocationListTool(double)
		}
		assertTrue(locationRightClickMenuIsOpenInLocationListTool(double))
	}

	fun givenUserHasEnteredValidLocationNameInRenameInputBox(double: SoyleStoriesTestDouble) {
		givenLocationRenameInputBoxIsVisible(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			val treeView = (find<LocationList>(scope).root.lookup(".tree-view") as TreeView<LocationItemViewModel?>)
			(treeView.editingCell?.graphic as TextField).text = "New Valid Location Name"
		}
	}

	fun whenLocationListToolCenterButtonIsClicked(double: SoyleStoriesTestDouble) {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		interact {
			from(projectScope.get<EmptyDisplay>().root).lookup("#emptyDisplay_createLocation").queryButton().onAction.handle(ActionEvent())
		}
	}

	fun whenLocationListToolActionBarCreateButtonIsClicked(double: SoyleStoriesTestDouble) {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		interact {
			from(projectScope.get<ActionBar>().root).lookup("#actionBar_createLocation").queryButton().onAction.handle(ActionEvent())
		}
	}

	fun whenLocationListToolActionBarDeleteButtonIsClicked(double: SoyleStoriesTestDouble) {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		interact {
			from(projectScope.get<ActionBar>().root).lookup("#actionBar_deleteLocation").queryButton().onAction.handle(ActionEvent())
		}
	}

	fun whenCreateLocationDialogCreateButtonIsClicked(double: SoyleStoriesTestDouble) {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		interact {
			from(projectScope.get<CreateLocationDialog>().root).lookup("#createLocation").queryButton().onAction.handle(ActionEvent())
		}
	}

	fun whenCreateLocationDialogCancelButtonIsClicked(double: SoyleStoriesTestDouble) {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		interact {
			from(projectScope.get<CreateLocationDialog>().root).lookup("#cancel").queryButton().onAction.handle(ActionEvent())
		}
	}

	fun locationListToolShowsEmptyMessage(double: SoyleStoriesTestDouble): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		var emptyDisplayIsVisible = false
		interact {
			emptyDisplayIsVisible = projectScope.get<EmptyDisplay>().let {
				it.root.isVisible && it.currentStage != null
			}
		}
		return emptyDisplayIsVisible
	}

	fun locationListToolShowsNumberOfLocations(double: SoyleStoriesTestDouble, number: Int): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		var populatedDisplayIsVisible = false
		var locationListSize = 0
		interact {
			populatedDisplayIsVisible = projectScope.get<PopulatedDisplay>().let {
				it.root.isVisible && it.currentStage != null
			}
			locationListSize = (projectScope.get<PopulatedDisplay>().root.lookup(".tree-view") as TreeView<*>).root.children.size
		}
		return populatedDisplayIsVisible && locationListSize == number
	}

	fun locationListToolShowsLocationWithName(double: SoyleStoriesTestDouble, name: String): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		var locations: List<TreeItem<*>> = emptyList()
		interact {
			locations = (projectScope.get<PopulatedDisplay>().root.lookup(".tree-view") as TreeView<*>).root.children.toList()
		}
		return locations.isNotEmpty() && locations.find { (it.value as? LocationItemViewModel)?.name == name } != null
	}

	fun createNewLocationDialogShowsErrorMessage(double: SoyleStoriesTestDouble): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		var text: Text? = null
		interact {
			text = from(findComponentsInScope<CreateLocationDialog>(projectScope).single().root).lookup("#errorMessage").queryText()
		}
		return text!!.isVisible && text!!.text.isNotBlank()
	}

	fun whenLocationIsDeleted(double: SoyleStoriesTestDouble): Location {
		ProjectSteps.givenProjectHasBeenOpened(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		var firstLocation: Location? = null
		interact {
			async(scope.applicationScope) {
				firstLocation = DI.resolve<LocationRepository>(scope).getAllLocationsInProject(Project.Id(scope.projectId)).first()
				DI.resolve<DeleteLocationController>(scope).deleteLocation(firstLocation!!.id.uuid.toString())
			}
		}
		return firstLocation!!
	}

	fun whenLocationListToolRightClickMenuButtonIsClicked(double: SoyleStoriesTestDouble, menuItemId: String) {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		val locationList = findComponentsInScope<LocationList>(projectScope).single()
		interact {
			val treeView = (locationList.root.lookup(".tree-view") as TreeView<*>)
			val menuItem = treeView.contextMenu!!.items.find { it.id == menuItemId }
			  ?: error("No menu item with id $menuItemId")
			menuItem.onAction.handle(ActionEvent())
		}
	}

	fun confirmDeleteLocationDialogIsOpen(double: SoyleStoriesTestDouble): Boolean {
		ProjectSteps.getProjectScope(double) ?: return false
		var isOpen = false
		interact {
			val windows = robotContext().windowFinder.listTargetWindows()
			isOpen = windows.find {
				val styleClass = it.scene?.root?.styleClass ?: return@find false
				styleClass.contains("alert") && styleClass.contains("confirmation")
			} != null
		}
		return isOpen
	}

	fun openConfirmDeleteLocationDialog(double: SoyleStoriesTestDouble) {
		givenNumberOfLocationsHaveBeenCreated(double, 1)
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			val repo = scope.get<LocationRepository>()
			val location = runBlocking {
				repo.getAllLocationsInProject(Project.Id(scope.projectId)).first()
			}
			scope.get<LayoutViewListener>().openDialog(Dialog.DeleteLocation(location.id.uuid.toString(), location.name))
		}
	}

	fun whenConfirmDeleteDialogButtonIsClicked(double: SoyleStoriesTestDouble, default: Boolean) {
		ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		interact {
			val confirmDeleteDialog = robotContext().windowFinder.listWindows().find {
				val styleClass = it.scene?.root?.styleClass ?: return@find false
				styleClass.contains("alert") && styleClass.contains("confirmation")
			} ?: error("Confirm dialog is not open")

			val buttons = from(confirmDeleteDialog.scene.root).lookup(".button").queryAllAs(Button::class.java)
			val button = when (default) {
				true -> buttons.find { it.isDefaultButton } ?: error("no default button")
				false -> buttons.find { it.isCancelButton } ?: error("no cancel button")
			}
			clickOn(button, MouseButton.PRIMARY)
		}
	}

	fun givenConfirmDeleteDialogHasBeenOpened(double: SoyleStoriesTestDouble) {
		if (! confirmDeleteLocationDialogIsOpen(double)) {
			openConfirmDeleteLocationDialog(double)
		}
		assertTrue(confirmDeleteLocationDialogIsOpen(double))
	}

	fun whenCreateLocationDialogIsOpened(double: SoyleStoriesTestDouble) {
		ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		setCreateNewLocationDialogOpen(double)
	}

	fun locationNameIsBlankInCreateLocationDialog(double: SoyleStoriesTestDouble): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		var name: String? = null
		interact {
			name = findComponentsInScope<CreateLocationDialog>(projectScope).singleOrNull()?.name?.value
		}
		return name?.isBlank() ?: false
	}

	fun locationListToolShowsInputBoxForSelectedItem(double: SoyleStoriesTestDouble): Boolean {
		if (! isLocationSelectedInLocationListTool(double)) return false
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		val locationList = findComponentsInScope<LocationList>(projectScope).singleOrNull() ?: return false
		var graphic: Node? = null
		interact {
			graphic = (locationList.root.lookup(".tree-view") as TreeView<*>).editingCell?.graphic
		}
		return graphic is TextField
	}

	fun locationListToolRenameInputBoxContainsSelectedItemName(double: SoyleStoriesTestDouble): Boolean {
		if (! locationListToolShowsInputBoxForSelectedItem(double)) return false
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		val locationList = findComponentsInScope<LocationList>(projectScope).singleOrNull() ?: return false
		var selectedItem: TreeItem<LocationItemViewModel?>? = null
		var itemGraphic: TextField? = null
		interact {
			itemGraphic = (locationList.root.lookup(".tree-view") as TreeView<*>).editingCell?.graphic as? TextField
			selectedItem = (locationList.root.lookup(".tree-view") as TreeView<*>)
			  .selectionModel.selectedItem as? TreeItem<LocationItemViewModel?>
		}
		return itemGraphic?.text?.equals(selectedItem?.value?.name) ?: false
	}

	fun locationListToolShowsLocationNameForSelectedItem(double: SoyleStoriesTestDouble): Boolean {
		if (! isLocationSelectedInLocationListTool(double)) return false.also { println("not selected") }
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false.also { println("no scope") }
		val locationList = findComponentsInScope<LocationList>(projectScope).singleOrNull() ?: return false.also { println("no list") }
		var graphic: Node? = null
		interact {
			graphic = (locationList.root.lookup(".tree-view") as TreeView<*>).editingCell?.graphic
		}
		return graphic !is TextField
	}

	fun locationListToolShowsOriginalLocationNameForSelectedItem(double: SoyleStoriesTestDouble): Boolean {
		if (! locationListToolShowsLocationNameForSelectedItem(double)) return false
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		val locationList = findComponentsInScope<LocationList>(projectScope).singleOrNull() ?: return false
		var selectedItem: TreeItem<LocationItemViewModel?>? = null
		var storedLocation: Location? = null
		interact {
			selectedItem = (locationList.root.lookup(".tree-view") as TreeView<*>)
			  .selectionModel.selectedItem as? TreeItem<LocationItemViewModel?>
			val id = selectedItem?.value?.id
			if (id != null) {
				async(projectScope.applicationScope) {
					storedLocation = projectScope.get<LocationRepository>().getLocationById(Location.Id(UUID.fromString(id)))
				}
			}
		}
		return selectedItem?.value?.name?.equals(storedLocation?.name) ?: false
	}

	fun locationListToolShowsChangedLocationNameForSelectedItem(double: SoyleStoriesTestDouble): Boolean {
		if (! locationListToolShowsLocationNameForSelectedItem(double)) return false
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		val locationList = findComponentsInScope<LocationList>(projectScope).singleOrNull() ?: return false
		var selectedItem: TreeItem<LocationItemViewModel?>? = null
		var storedLocation: Location? = null
		interact {
			selectedItem = (locationList.root.lookup(".tree-view") as TreeView<*>)
			  .selectionModel.selectedItem as? TreeItem<LocationItemViewModel?>
			val id = selectedItem?.value?.id
			if (id != null) {
				async(projectScope.applicationScope) {
					storedLocation = projectScope.get<LocationRepository>().getLocationById(Location.Id(UUID.fromString(id)))
				}
			}
		}
		return selectedItem?.value?.name?.equals(storedLocation?.name) ?: false
	}
}