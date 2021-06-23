package com.soyle.stories.desktop.view.location.create

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.location.controllers.CreateNewLocationController
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

class CreateNewLocationControllerDouble(
    var onCreateNewLocation: (SingleNonBlankLine, String) -> Unit = { _, _ -> },
    var deferred: CompletableDeferred<CreateNewLocation.ResponseModel> = CompletableDeferred()
) : CreateNewLocationController {

    override fun createNewLocation(name: SingleNonBlankLine, description: String): Deferred<CreateNewLocation.ResponseModel> {
        onCreateNewLocation(name, description)
        return deferred
    }
}