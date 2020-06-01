package com.soyle.stories.layout.tools.dynamic

import com.soyle.stories.entities.Location
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.location.LocationDoesNotExist
import java.util.*

data class LocationDetails(val locationId: UUID) : DynamicTool() {

	override val isTemporary: Boolean
		get() = false

	override suspend fun validate(context: OpenToolContext) {
		context.locationRepository.getLocationById(Location.Id(locationId))
		  ?: throw LocationDoesNotExist(locationId)
	}

	override fun identifiedWithId(id: UUID): Boolean =
	  id == locationId
}