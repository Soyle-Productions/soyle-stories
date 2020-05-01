package com.soyle.stories.location.locationList

import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutViewListener

class LocationListTestView(
  private val locationListViewListener: LocationListViewListener,
  private val layoutViewListener: LayoutViewListener,
  private val getView: () -> LocationListViewModelWrapper
) {

	var selectedItem: LocationItemViewModel? = null
		private set

	init {
		locationListViewListener.getValidState()
	}

	fun clickCenterCreateNewLocationButton() {
		layoutViewListener.openDialog(Dialog.CreateLocation)
	}

	fun clickBottomCreateNewLocationButton() {
		layoutViewListener.openDialog(Dialog.CreateLocation)
	}

	fun clickBottomDeleteButton() {
		val selectedItem = selectedItem ?: return
		layoutViewListener.openDialog(Dialog.DeleteLocation(selectedItem.id, selectedItem.name))
	}

	fun rightClickOnLocation() {
		selectedItem = getView().locations.firstOrNull()
	}

	fun selectLocation() {
		selectedItem = getView().locations.firstOrNull()
	}

	fun clickRightClickMenuDeleteButton() {
		val selectedItem = selectedItem ?: return
		layoutViewListener.openDialog(Dialog.DeleteLocation(selectedItem.id, selectedItem.name))
	}
	fun clickRightClickMenuRenameButton() {
	}

}