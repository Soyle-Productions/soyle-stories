package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.common.ThreadTransformerImpl
import com.soyle.stories.di.location.LocationComponent
import tornadofx.Component
import tornadofx.ScopedInstance

class CreateLocationDialogComponent : Component(), ScopedInstance {

	private val locationComponent: LocationComponent by inject()

	val createLocationDialogPresenter by lazy {
		CreateLocationDialogPresenter(
		  find<CreateLocationDialogModel>(),
		  locationComponent.locationEvents
		)
	}

	val createLocationDialogViewListener: CreateLocationDialogViewListener by lazy {
		CreateLocationDialogController(
		  ThreadTransformerImpl,
		  locationComponent.createNewLocationController,
		  createLocationDialogPresenter
		)
	}

}