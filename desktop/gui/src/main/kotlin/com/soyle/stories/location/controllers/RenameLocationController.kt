package com.soyle.stories.location.controllers

import com.soyle.stories.common.SingleNonBlankLine
import com.soyle.stories.entities.Location
import com.soyle.stories.location.usecases.renameLocation.RenameLocation

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