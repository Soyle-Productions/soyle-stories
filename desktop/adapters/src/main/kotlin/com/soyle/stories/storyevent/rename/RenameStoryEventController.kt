package com.soyle.stories.storyevent.rename

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEvent

interface RenameStoryEventController {
	fun renameStoryEvent(storyEventId: StoryEvent.Id, newName: NonBlankString)

	companion object {
		operator fun invoke(
			threadTransformer: ThreadTransformer,
			renameStoryEvent: RenameStoryEvent,
			renameStoryEventOutputPort: RenameStoryEvent.OutputPort
		) = object : RenameStoryEventController {
			override fun renameStoryEvent(storyEventId: StoryEvent.Id, newName: NonBlankString) {
				threadTransformer.async {
					renameStoryEvent.invoke(storyEventId, newName, renameStoryEventOutputPort)
				}
			}
		}
	}
}