package com.soyle.stories.storyevent.linkLocationToStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.storyevent.usecases.linkLocationToStoryEvent.LinkLocationToStoryEvent

class LinkLocationToStoryEventNotifier : Notifier<LinkLocationToStoryEvent.OutputPort>(), LinkLocationToStoryEvent.OutputPort {
	override fun receiveLinkLocationToStoryEventResponse(response: LinkLocationToStoryEvent.ResponseModel) {
		notifyAll { it.receiveLinkLocationToStoryEventResponse(response) }
	}

	override fun receiveLinkLocationToStoryEventFailure(failure: Exception) {
		notifyAll { it.receiveLinkLocationToStoryEventFailure(failure) }
	}
}