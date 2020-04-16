package com.soyle.stories.location.deleteLocationDialog

import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutViewListener

class DeleteLocationDialogTestView(
  private val deleteLocationDialogViewListener: DeleteLocationDialogViewListener,
  private val layoutViewListener: LayoutViewListener,
  private val getView: () -> DeleteLocationDialogViewModelWrapper
) {

	init {
		val dialog = getView().dialog!!
		deleteLocationDialogViewListener.getValidState(dialog.locationId, dialog.locationName)
	}

	fun clickConfirmButton() {
		val locationId = getView().locationId ?: return
		deleteLocationDialogViewListener.deleteLocation(locationId)
	}

	fun clickCancelButton() {
		layoutViewListener.closeDialog(Dialog.DeleteLocation::class)
	}

}