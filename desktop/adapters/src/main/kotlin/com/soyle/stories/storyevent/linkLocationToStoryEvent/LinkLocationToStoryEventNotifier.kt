package com.soyle.stories.storyevent.linkLocationToStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEvent

class LinkLocationToStoryEventNotifier(
	private val threadTransformer: ThreadTransformer
) : Notifier<LinkLocationToStoryEvent.OutputPort>(), LinkLocationToStoryEvent.OutputPort {
	override fun receiveLinkLocationToStoryEventResponse(response: LinkLocationToStoryEvent.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveLinkLocationToStoryEventResponse(response) }
		}
	}

	override fun receiveLinkLocationToStoryEventFailure(failure: Exception) {
		threadTransformer.async {
			notifyAll { it.receiveLinkLocationToStoryEventFailure(failure) }
		}
	}
}