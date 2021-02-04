package com.soyle.stories.location.deleteLocation

import com.soyle.stories.entities.Location

interface DeleteLocationController {

    fun deleteLocation(locationId: Location.Id)

}