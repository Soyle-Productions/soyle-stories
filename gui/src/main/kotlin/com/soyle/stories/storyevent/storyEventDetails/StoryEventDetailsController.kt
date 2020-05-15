package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations

class StoryEventDetailsController(
  private val threadTransformer: ThreadTransformer,
  private val listAllLocations: ListAllLocations,
  private val listAllLocationsOutputPort: ListAllLocations.OutputPort
) : StoryEventDetailsViewListener {

	override fun getValidState() {
		threadTransformer.async {
			listAllLocations.invoke(listAllLocationsOutputPort)
		}
	}

}