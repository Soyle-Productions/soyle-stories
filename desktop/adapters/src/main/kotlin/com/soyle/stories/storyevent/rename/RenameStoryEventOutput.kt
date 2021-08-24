package com.soyle.stories.storyevent.rename

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEvent

class RenameStoryEventOutput(
	private val storyEventRenamedReceiver: StoryEventRenamedReceiver
) : RenameStoryEvent.OutputPort {
	override suspend fun receiveRenameStoryEventResponse(response: RenameStoryEvent.ResponseModel) {
		storyEventRenamedReceiver.receiveStoryEventRenamed(response.storyEventRenamed)
	}
}