package com.soyle.stories.location

import com.soyle.stories.UATLogger
import com.soyle.stories.character.CharacterDriver
import com.soyle.stories.common.async
import com.soyle.stories.common.editingCell
import com.soyle.stories.common.isEditing
import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.location.LocationSteps.interact
import com.soyle.stories.location.controllers.CreateNewLocationController
import com.soyle.stories.location.controllers.DeleteLocationController
import com.soyle.stories.location.controllers.RenameLocationController
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import com.soyle.stories.location.deleteLocationDialog.deleteLocationDialog
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.locationDetails.LocationDetails
import com.soyle.stories.location.locationDetails.LocationDetailsScope
import com.soyle.stories.location.locationList.LocationList
import com.soyle.stories.location.redescribeLocation.ReDescribeLocationController
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.event.EventType
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.text.Text
import javafx.stage.Window
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.find
import tornadofx.selectFirst
import tornadofx.uiComponent
import java.util.*

object LocationSteps : ApplicationTest() {

	// location list tool is opened

	fun setLocationListToolOpened(double: SoyleStoriesTestDouble) {
		ProjectSteps.checkProjectHasBeenOpened(double)
		whenLocationListToolIsOpened(double)
	}

	fun getOpenLocationListTool(double: SoyleStoriesTestDouble): LocationList?
	{
		val projectScope = ProjectSteps.getProjectScope(double) ?: return null
		return findComponentsInScope<LocationList>(projectScope).singleOrNull()?.takeIf { it.currentStage?.isShowing == true }
	}

	fun isLocationListToolOpen(double: SoyleStoriesTestDouble): Boolean = getOpenLocationListTool(double) != null

	fun whenLocationListToolIsOpened(double: SoyleStoriesTestDouble) {
		val scope = ProjectSteps.getProjectScope(double)!!
		CharacterDriver.interact {
			async(scope) {
				scope.get<LayoutViewListener>().toggleToolOpen(com.soyle.stories.layout.config.fixed.LocationList)
			}
		}
	}

	fun givenLocationListToolHasBeenOpened(double: SoyleStoriesTestDouble) {
		if (!isLocationListToolOpen(double)) {
			setLocationListToolOpened(double)
		}
		assertTrue(isLocationListToolOpen(double))
	}

	fun setLocationListToolClosed(double: SoyleStoriesTestDouble) {
		ProjectSteps.checkProjectHasBeenOpened(double)
		whenLocationListToolIsClosed(double)
	}

	fun whenLocationListToolIsClosed(double: SoyleStoriesTestDouble) {
		val scope = ProjectSteps.getProjectScope(double)!!
		CharacterDriver.interact {
			async(scope) {
				scope.get<LayoutViewListener>().toggleToolOpen(com.soyle.stories.layout.config.fixed.LocationList)
			}
		}
	}

	fun givenLocationListToolHasBeenClosed(double: SoyleStoriesTestDouble) {
		if (isLocationListToolOpen(double)) {
			setLocationListToolClosed(double)
		}
		assertFalse(isLocationListToolOpen(double))
	}

	// location creation

	fun setLocationCreated(double: SoyleStoriesTestDouble)
	{
		ProjectSteps.checkProjectHasBeenOpened(double)
		whenLocationIsCreated(double)
	}

	fun setNoLocationsCreated(double: SoyleStoriesTestDouble)
	{
		ProjectSteps.checkProjectHasBeenOpened(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		val locations = getLocationsCreated(double)
		interact {
			async(scope.applicationScope) {
				locations.forEach {
					DI.resolve<DeleteLocationController>(scope).deleteLocation(it.id.uuid.toString())
				}
			}
		}
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

	fun givenNoLocationsHaveBeenCreated(double: SoyleStoriesTestDouble) {
		val numberOfLocationsCreated = getNumberOfLocationsCreated(double)
		if (numberOfLocationsCreated > 0) {
			setNoLocationsCreated(double)
		}
		assertThat(getNumberOfLocationsCreated(double)).isEqualTo(0)
	}

	// create new location dialog open

	fun setCreateNewLocationDialogOpen(double: SoyleStoriesTestDouble) {
		val menuItem = ProjectSteps.getMenuItem(double, "File", "New", "Location")!!
		interact {
			menuItem.fire()
		}
	}

	fun isCreateNewLocationDialogOpen(double: SoyleStoriesTestDouble): Boolean {
		return getOpenCreateNewLocationDialog(double) != null
	}

	fun getOpenCreateNewLocationDialog(double: SoyleStoriesTestDouble): CreateLocationDialog?
	{
		val dialog = listWindows().find {
			it.scene.root.uiComponent<CreateLocationDialog>() != null
		}?.scene?.root?.uiComponent<CreateLocationDialog>()
		  ?: return (null).also { UATLogger.log("no project started") }
		return dialog.takeIf { it.currentStage?.isShowing == true }
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
		whenLocationDetailsToolIsOpened(double, locationId)
	}

	fun getOpenedLocationDetailsTool(double: SoyleStoriesTestDouble, locationId: UUID): LocationDetails?
	{
		val projectScope = ProjectSteps.getProjectScope(double) ?: return (null).also { UATLogger.log("Project not yet opened") }
		val scope = projectScope.toolScopes.find { it is LocationDetailsScope && it.locationId == locationId.toString() }
		  ?: return (null).also { UATLogger.log("No LocationDetailsScope with id $locationId") }
		val component = findComponentsInScope<LocationDetails>(scope).singleOrNull() ?: return (null).also { UATLogger.log("Location Details component not found in scope") }
		return component.takeIf { it.currentStage?.isShowing == true } ?: (null).also { UATLogger.log("Location Details component not visible") }
	}

	fun whenLocationDetailsToolIsOpened(double: SoyleStoriesTestDouble, locationId: UUID)
	{
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			scope.get<OpenToolController>().openLocationDetailsTool(locationId.toString())
		}
	}

	fun isLocationDetailsToolOpen(double: SoyleStoriesTestDouble, locationId: UUID) = getOpenedLocationDetailsTool(double, locationId) != null

	fun givenLocationDetailsToolHasBeenOpened(double: SoyleStoriesTestDouble, locationId: UUID) {
		if (!isLocationDetailsToolOpen(double, locationId)) {
			setLocationDetailsToolOpened(double, locationId)
		}
		assertTrue(isLocationDetailsToolOpen(double, locationId))
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

	fun getLocationSelectedInLocationListTool(double: SoyleStoriesTestDouble): LocationItemViewModel? {
		val projectScope = ProjectSteps.getProjectScope(double) ?: return null
		val locationList = findComponentsInScope<LocationList>(projectScope).singleOrNull() ?: return null
		var selected: LocationItemViewModel? = null
		interact {
			selected = (locationList.root.lookup(".tree-view") as TreeView<*>)
			  .selectionModel.selectedItem?.value as? LocationItemViewModel
		}
		return selected
	}

	fun isLocationSelectedInLocationListTool(double: SoyleStoriesTestDouble): Boolean {
		return getLocationSelectedInLocationListTool(double) != null
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
		val populatedDisplay = findComponentsInScope<LocationList>(projectScope).singleOrNull() ?: return false
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
		val dialog = getOpenCreateNewLocationDialog(double)
		var isInvalid = false
		interact {
			val name = dialog?.name?.get()
			isInvalid = name != null && name.isBlank()
		}
		return isInvalid
	}

	fun whenUserEntersInvalidLocationNameInCreatedLocationDialog(double: SoyleStoriesTestDouble) {
		val dialog = getOpenCreateNewLocationDialog(double)!!
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			val textInput = from(dialog.root).lookup("#name").queryTextInputControl()
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
		val dialog = getOpenCreateNewLocationDialog(double)
		var isInvalid = false
		interact {
			val name = dialog?.name?.get()
			isInvalid = name != null && name.isNotBlank()
		}
		return isInvalid
	}

	fun whenUserEntersValidLocationNameInCreatedLocationDialog(double: SoyleStoriesTestDouble) {
		givenCreateNewLocationDialogHasBeenOpened(double)
		val dialog = getOpenCreateNewLocationDialog(double)!!
		interact {
			from(dialog.root).lookup("#name").queryTextInputControl().textProperty().set("Valid Location Name")
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
			from(projectScope.get<LocationList>().root).lookup("#emptyDisplay_createLocation").queryButton().onAction.handle(ActionEvent())
		}
	}

	fun whenLocationListToolActionBarCreateButtonIsClicked(double: SoyleStoriesTestDouble) {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		interact {
			from(projectScope.get<LocationList>().root).lookup("#actionBar_createLocation").queryButton().onAction.handle(ActionEvent())
		}
	}

	fun whenLocationListToolActionBarDeleteButtonIsClicked(double: SoyleStoriesTestDouble) {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		interact {
			from(projectScope.get<LocationList>().root).lookup("#actionBar_deleteLocation").queryButton().onAction.handle(ActionEvent())
		}
	}

	fun whenCreateLocationDialogCreateButtonIsClicked(double: SoyleStoriesTestDouble) {
		val dialog = getOpenCreateNewLocationDialog(double)!!
		interact {
			from(dialog.root).lookup("#createLocation").queryButton().onAction.handle(ActionEvent())
		}
	}

	fun whenCreateLocationDialogCancelButtonIsClicked(double: SoyleStoriesTestDouble) {
		val dialog = getOpenCreateNewLocationDialog(double)!!
		interact {
			from(dialog.root).lookup("#cancel").queryButton().onAction.handle(ActionEvent())
		}
	}

	fun locationListToolShowsEmptyMessage(double: SoyleStoriesTestDouble): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		var emptyDisplayIsVisible = false
		interact {
			emptyDisplayIsVisible = projectScope.get<LocationList>().let {
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
			populatedDisplayIsVisible = projectScope.get<LocationList>().let {
				it.root.isVisible && it.currentStage != null
			}
			locationListSize = (projectScope.get<LocationList>().root.lookup(".tree-view") as TreeView<*>).root.children.size
		}
		return populatedDisplayIsVisible && locationListSize == number
	}

	fun locationListToolShowsLocationWithName(double: SoyleStoriesTestDouble, name: String): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		var locations: List<TreeItem<*>> = emptyList()
		interact {
			locations = (projectScope.get<LocationList>().root.lookup(".tree-view") as TreeView<*>).root.children.toList()
		}
		return locations.isNotEmpty() && locations.find { (it.value as? LocationItemViewModel)?.name == name } != null
	}

	fun createNewLocationDialogShowsErrorMessage(double: SoyleStoriesTestDouble): Boolean {
		val dialog = getOpenCreateNewLocationDialog(double)
		var text: Text? = null
		interact {
			text = from(dialog?.root).lookup("#errorMessage").queryText()
		}
		return text!!.isVisible && text!!.text.isNotBlank()
	}

	fun whenLocationIsDeleted(double: SoyleStoriesTestDouble): Location {
		ProjectSteps.checkProjectHasBeenOpened(double)
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

	fun whenLocationIsRenamed(double: SoyleStoriesTestDouble) {
		ProjectSteps.checkProjectHasBeenOpened(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		val repo = scope.get<LocationRepository>()
		val controller = scope.get<RenameLocationController>()
		runBlocking {
			val firstLocation = repo.getAllLocationsInProject(Project.Id(scope.projectId)).first()
			controller.renameLocation(firstLocation.id.uuid.toString(), "Renamed Location")
		}
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

	fun isConfirmDeleteLocationDialogOpen(double: SoyleStoriesTestDouble): Boolean {
		return getOpenConfirmDeleteLocationDialog(double) != null
	}

	fun getOpenConfirmDeleteLocationDialog(double: SoyleStoriesTestDouble): Window?
	{
		ProjectSteps.getProjectScope(double) ?: return null
		var window: Window? = null
		interact {
			val windows = robotContext().windowFinder.listTargetWindows()
			window = windows.find {
				val styleClass = it.scene?.root?.styleClass ?: return@find false
				styleClass.contains("alert") && styleClass.contains("confirmation")
			}
		}
		return window
	}

	fun openConfirmDeleteLocationDialog(double: SoyleStoriesTestDouble) {
		givenNumberOfLocationsHaveBeenCreated(double, 1)
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			val repo = scope.get<LocationRepository>()
			val location = runBlocking {
				repo.getAllLocationsInProject(Project.Id(scope.projectId)).first()
			}
			deleteLocationDialog(scope, LocationItemViewModel(location.id.uuid.toString(), location.name))
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
		if (! isConfirmDeleteLocationDialogOpen(double)) {
			openConfirmDeleteLocationDialog(double)
		}
		assertTrue(isConfirmDeleteLocationDialogOpen(double))
	}

	fun whenCreateLocationDialogIsOpened(double: SoyleStoriesTestDouble) {
		ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		setCreateNewLocationDialogOpen(double)
	}

	fun locationNameIsBlankInCreateLocationDialog(double: SoyleStoriesTestDouble): Boolean {
		val dialog = getOpenCreateNewLocationDialog(double)
		var name: String? = null
		interact {
			name = dialog?.name?.value
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

	fun setLocationDescriptionTo(double: SoyleStoriesTestDouble, locationId: UUID, description: String)
	{
		givenNumberOfLocationsHaveBeenCreated(double, 1)
		val scope = ProjectSteps.getProjectScope(double) ?: error("project not yet opened")
		val controller = scope.get<ReDescribeLocationController>()
		controller.reDescribeLocation(locationId.toString(), description)
	}

	fun isLocationDescriptionSameAs(double: SoyleStoriesTestDouble, locationId: UUID, description: String): Boolean
	{
		val scope = ProjectSteps.getProjectScope(double) ?: return false
		val locationRepo = scope.get<LocationRepository>()
		val storedLocation = runBlocking {
			locationRepo.getLocationById(Location.Id(locationId))
		} ?: return false
		return storedLocation.description == description
	}

	fun givenLocationHasDescription(double: SoyleStoriesTestDouble, locationId: UUID, description: String)
	{
		if (! isLocationDescriptionSameAs(double, locationId, description)) {
			setLocationDescriptionTo(double, locationId, description)
		}
		assertTrue(isLocationDescriptionSameAs(double, locationId, description))
	}

	fun setDescriptionInLocationDetailsToolEqualTo(double: SoyleStoriesTestDouble, locationId: UUID, description: String)
	{
		givenLocationDetailsToolHasBeenOpened(double, locationId)
		val tool = getOpenedLocationDetailsTool(double, locationId)!!
		interact {
			from(tool.root).lookup("#description").queryTextInputControl()?.let {
				it.requestFocus()
				it.text = description
			}
		}
	}

	fun getDescriptionInLocationDetailsTool(double: SoyleStoriesTestDouble, locationId: UUID): String?
	{
		val tool = getOpenedLocationDetailsTool(double, locationId) ?: return (null).also { UATLogger.log("no open location details tool for $locationId") }
		var textArea: TextInputControl? = null
		interact {
			textArea = from(tool.root).lookup("#description").queryTextInputControl()
		}
		return textArea?.text ?: (null).also { UATLogger.log("no text input control found in $tool") }
	}

	fun isDescriptionInLocationDetailsToolEqualTo(double: SoyleStoriesTestDouble, locationId: UUID, description: String): Boolean
	{
		return getDescriptionInLocationDetailsTool(double, locationId) == description
	}

	fun givenLocationDetailsToolHasDescriptionOf(double: SoyleStoriesTestDouble, locationId: UUID, description: String)
	{
		if (! isDescriptionInLocationDetailsToolEqualTo(double, locationId, description)) {
			setDescriptionInLocationDetailsToolEqualTo(double, locationId, description)
		}
		assertTrue(isDescriptionInLocationDetailsToolEqualTo(double, locationId, description))
	}

	fun whenLocationDetailsToolIsClosed(double: SoyleStoriesTestDouble, locationId: UUID)
	{
		val tool = getOpenedLocationDetailsTool(double, locationId)!!
		interact {
			tool.owningTab?.tabPane?.requestFocus()
			tool.owningTab?.onCloseRequest?.handle(Event(EventType.ROOT))
		}
	}

	fun isConfirmDeleteLocationDialogLocationDisplayingNameOf(double: SoyleStoriesTestDouble, location: Location): Boolean
	{
		val window = getOpenConfirmDeleteLocationDialog(double) ?: return false
		val dialog = window.scene.root as? DialogPane ?: return false
		return dialog.headerText.contains(location.name)
	}
}