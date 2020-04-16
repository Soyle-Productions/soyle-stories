package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutViewListener

class CreateLocationDialogTestView(
  private val createLocationDialogViewListener: CreateLocationDialogViewListener,
  private val layoutViewListener: LayoutViewListener
) {

	var nameText: String = ""
	var descriptionText: String = ""

	init {
		createLocationDialogViewListener.getValidState()
	}

	fun pressEnterKey() {
		createLocationDialogViewListener.createLocation(nameText, descriptionText)
	}
	fun pressEscKey() {
		layoutViewListener.closeDialog(Dialog.CreateLocation::class)
	}
	fun clickCreateButton() {
		createLocationDialogViewListener.createLocation(nameText, descriptionText)
	}
	fun clickCancelButton() {
		layoutViewListener.closeDialog(Dialog.CreateLocation::class)
	}

}