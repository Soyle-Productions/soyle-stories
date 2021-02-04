package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.location.LocationException
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation

class CreateNewLocationPresenter(
  private val view: CreateLocationDialogView
) : CreateNewLocation.OutputPort {
	override fun receiveCreateNewLocationFailure(failure: LocationException) {
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