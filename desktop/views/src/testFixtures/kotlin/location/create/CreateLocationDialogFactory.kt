package com.soyle.stories.desktop.view.location.create

import com.soyle.stories.common.AsyncThreadTransformer
import com.soyle.stories.domain.location.Location
import com.soyle.stories.location.controllers.CreateNewLocationController
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogLocale
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation

class CreateLocationDialogFactory(
    private val onInvoke: (suspend (CreateNewLocation.ResponseModel) -> Unit) -> Unit = {},
    val threadTransformer: AsyncThreadTransformer = AsyncThreadTransformer(ApplicationScope()),
    val createNewLocation: CreateNewLocationControllerDouble = CreateNewLocationControllerDouble(),
    val locale: CreateLocationDialogLocaleMock = CreateLocationDialogLocaleMock()
) : CreateLocationDialog.Factory {

    override fun invoke(onCreateLocation: suspend (CreateNewLocation.ResponseModel) -> Unit): CreateLocationDialog
    {
        onInvoke(onCreateLocation)
        return CreateLocationDialog(
            threadTransformer,
            onCreateLocation,
            createNewLocation,
            locale
        )
    }
}