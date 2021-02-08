package com.soyle.stories.location.components

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.location.listAllLocations.ListAllLocations

class LocationListController(
  threadTransformer: ThreadTransformer,
  listAllLocations: ListAllLocations,
  listAllLocationsOutputPort: ListAllLocations.OutputPort
) {

	init {
		threadTransformer.async {
			listAllLocations.invoke(listAllLocationsOutputPort)
		}
	}

}