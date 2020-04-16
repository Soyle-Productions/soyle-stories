package com.soyle.stories.location.deleteLocationDialog

import com.soyle.stories.common.ThreadTransformerImpl
import com.soyle.stories.di.location.LocationComponent
import tornadofx.Component
import tornadofx.ScopedInstance

class DeleteLocationDialogComponent : Component(), ScopedInstance {

	private val locationComponent by inject<LocationComponent>()

	val deleteLocationDialogViewListener: DeleteLocationDialogViewListener by lazy {
		DeleteLocationDialogController(
		  ThreadTransformerImpl,
		  locationComponent.deleteLocationController,
		  DeleteLocationDialogPresenter(
			find<DeleteLocationDialogModel>()
		  )
		)
	}

}