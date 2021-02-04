package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.common.SingleNonBlankLine
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.controllers.CreateNewLocationController

class CreateLocationDialogController(
  private val threadTransformer: ThreadTransformer,
  private val createLocationController: CreateNewLocationController,
  private val presenter: CreateLocationDialogPresenter
) : CreateLocationDialogViewListener {

	override fun getValidState() {
		presenter.displayCreateLocationDialog()
	}

	override fun createLocation(name: SingleNonBlankLine, description: String) {
		threadTransformer.async {
			createLocationController.createNewLocation(name, description)
		}
	}
}