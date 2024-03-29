package com.soyle.stories.storyevent.renameStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.storyevent.renameStoryEvent.RenameStoryEvent

class RenameStoryEventNotifier(
	private val threadTransformer: ThreadTransformer
) : Notifier<RenameStoryEvent.OutputPort>(), RenameStoryEvent.OutputPort {
	override fun receiveRenameStoryEventResponse(response: RenameStoryEvent.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveRenameStoryEventResponse(response) }
		}
	}

	override fun receiveRenameStoryEventFailure(failure: Exception) {
		threadTransformer.async {
			notifyAll { it.receiveRenameStoryEventFailure(failure) }
		}
	}
}