package com.soyle.stories.storyevent.renameStoryEvent

import com.soyle.stories.common.Notifier
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.usecases.renameStoryEvent.RenameStoryEvent

class RenameStoryEventNotifier : Notifier<RenameStoryEvent.OutputPort>(), RenameStoryEvent.OutputPort {
	override fun receiveRenameStoryEventResponse(response: RenameStoryEvent.ResponseModel) {
		notifyAll { it.receiveRenameStoryEventResponse(response) }
	}

	override fun receiveRenameStoryEventFailure(failure: StoryEventException) {
		notifyAll { it.receiveRenameStoryEventFailure(failure) }
	}
}