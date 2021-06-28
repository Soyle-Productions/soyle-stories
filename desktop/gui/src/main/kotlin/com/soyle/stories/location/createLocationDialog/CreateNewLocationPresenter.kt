package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation

class CreateNewLocationPresenter(
  private val view: CreateLocationDialogView
) : CreateNewLocation.OutputPort {
	override fun receiveCreateNewLocationFailure(failure: Exception) {
		view.updateOrInvalidated {
			copy(
			  errorMessage = failure.localizedMessage?.takeUnless { it.isBlank() } ?: "Failure"
			)
		}
	}

	override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
		view.updateOrInvalidated {
			copy(
			  isOpen = false
			)
		}
	}
}