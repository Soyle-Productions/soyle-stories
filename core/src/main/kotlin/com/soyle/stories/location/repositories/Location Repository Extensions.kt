package com.soyle.stories.location.repositories

import com.soyle.stories.entities.Location
import com.soyle.stories.location.LocationDoesNotExist

suspend fun LocationRepository.getLocationOrError(locationId: Location.Id) =
    getLocationById(locationId) ?: throw LocationDoesNotExist(locationId.uuid)