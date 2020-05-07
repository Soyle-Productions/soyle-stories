package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.eventbus.listensTo
import com.soyle.stories.location.events.LocationEvents

class CreateLocationDialogPresenter(
  private val view: CreateLocationDialogView,
  locationEvents: LocationEvents
) {

	private val subPresenters = listOf(
	  CreateNewLocationPresenter(view) listensTo locationEvents.createNewLocation
	)

	fun displayCreateLocationDialog() {
		view.update {
			CreateLocationDialogViewModel(
			  null
			)
		}
	}
}