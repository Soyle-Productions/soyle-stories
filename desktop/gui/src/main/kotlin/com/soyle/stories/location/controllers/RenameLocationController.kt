package com.soyle.stories.location.controllers

import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.domain.location.Location
import com.soyle.stories.usecase.location.renameLocation.RenameLocation

class RenameLocationController(
    private val renameLocation: RenameLocation,
    private val renameLocationOutputPort: RenameLocation.OutputPort
) {

    suspend fun renameLocation(locationId: Location.Id, newName: SingleNonBlankLine) {
        renameLocation.invoke(
            locationId,
            newName,
            renameLocationOutputPort
        )
    }

}