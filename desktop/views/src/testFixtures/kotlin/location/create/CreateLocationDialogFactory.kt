package com.soyle.stories.desktop.view.location.create

import com.soyle.stories.domain.location.Location
import com.soyle.stories.location.controllers.CreateNewLocationController
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation

class CreateLocationDialogFactory(
    private val onInvoke: ((CreateNewLocation.ResponseModel) -> Unit) -> Unit = {},
) : CreateLocationDialog.Factory {

    override fun invoke(onCreateLocation: (CreateNewLocation.ResponseModel) -> Unit): CreateLocationDialog
    {
        onInvoke(onCreateLocation)
        return CreateLocationDialog(
            onCreateLocation,
            CreateNewLocationControllerDouble()
        )
    }
}