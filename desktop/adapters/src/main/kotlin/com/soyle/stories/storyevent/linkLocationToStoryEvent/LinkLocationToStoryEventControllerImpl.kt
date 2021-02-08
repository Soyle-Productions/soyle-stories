package com.soyle.stories.storyevent.linkLocationToStoryEvent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEvent
import java.util.*

class LinkLocationToStoryEventControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val linkLocationToStoryEvent: LinkLocationToStoryEvent,
  private val linkLocationToStoryEventOutputPort: LinkLocationToStoryEvent.OutputPort
) : LinkLocationToStoryEventController {

	override fun linkLocationToStoryEvent(storyEventId: String, locationId: String)
	{
		val formattedStoryEventId = formatStoryEventId(storyEventId)
		val formattedLocationId = formatLocationId(locationId)
		threadTransformer.async {
			linkLocationToStoryEvent.invoke(
			  formattedStoryEventId,
			  formattedLocationId,
			  linkLocationToStoryEventOutputPort
			)
		}
	}

	override fun unlinkLocationToStoryEvent(storyEventId: String)
	{
		val formattedStoryEventId = formatStoryEventId(storyEventId)
		threadTransformer.async {
			linkLocationToStoryEvent.invoke(
			  formattedStoryEventId,
			  null,
			  linkLocationToStoryEventOutputPort
			)
		}
	}

	private fun formatStoryEventId(storyEventId: String) = UUID.fromString(storyEventId)
	private fun formatLocationId(locationId: String) = UUID.fromString(locationId)
}