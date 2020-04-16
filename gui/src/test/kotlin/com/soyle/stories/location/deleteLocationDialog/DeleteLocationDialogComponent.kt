package com.soyle.stories.location.deleteLocationDialog

import com.soyle.stories.gui.SingleThreadTransformer
import com.soyle.stories.location.LocationComponent

class DeleteLocationDialogComponent(
  locationComponent: LocationComponent,
  deleteLocationDialogView: () -> DeleteLocationDialogView
) {

	val deleteLocationDialogViewListener: DeleteLocationDialogViewListener by lazy {
		DeleteLocationDialogController(
		  SingleThreadTransformer,
		  locationComponent.deleteLocationController,
		  DeleteLocationDialogPresenter(
			deleteLocationDialogView()
		  )
		)
	}

}