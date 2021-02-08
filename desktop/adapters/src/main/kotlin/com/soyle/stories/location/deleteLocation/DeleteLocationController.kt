package com.soyle.stories.location.deleteLocation

import com.soyle.stories.domain.location.Location

interface DeleteLocationController {

    fun deleteLocation(locationId: Location.Id)

}