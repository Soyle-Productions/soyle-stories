package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.gui.SingleThreadTransformer
import com.soyle.stories.location.LocationComponent
import com.soyle.stories.location.controllers.CreateNewLocationController

class CreateNewLocationDialogComponent(
  locationComponent: LocationComponent,
  createLocationDialogView: () -> CreateLocationDialogView
) {

	val createLocationDialogPresenter by lazy {
		CreateLocationDialogPresenter(
		  createLocationDialogView(),
		  locationComponent.locationEvents
		)
	}

	val createLocationDialogViewListener: CreateLocationDialogViewListener by lazy {
		CreateLocationDialogController(
		  SingleThreadTransformer,
		  locationComponent.createNewLocationController,
		  createLocationDialogPresenter
		)
	}

}