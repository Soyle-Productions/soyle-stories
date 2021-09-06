package com.soyle.stories.storyevent.rename

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEvent
import kotlinx.coroutines.Job

interface RenameStoryEventController {

	/**
	 * incomplete request.  Will prompt the user for the new name.
 	 */
	fun requestToRenameStoryEvent(storyEventId: StoryEvent.Id, currentName: String)
	fun renameStoryEvent(storyEventId: StoryEvent.Id, newName: NonBlankString): Job

	companion object {
		operator fun invoke(
			threadTransformer: ThreadTransformer,
			renameStoryEvent: RenameStoryEvent,
			renameStoryEventOutputPort: RenameStoryEvent.OutputPort,

			renameStoryEventPrompt: RenameStoryEventPrompt
		) = object : RenameStoryEventController {

			override fun requestToRenameStoryEvent(storyEventId: StoryEvent.Id, currentName: String) {
				renameStoryEventPrompt.promptForNewName(storyEventId, currentName)
			}

			override fun renameStoryEvent(storyEventId: StoryEvent.Id, newName: NonBlankString): Job {
				return threadTransformer.async {
					renameStoryEvent.invoke(storyEventId, newName, renameStoryEventOutputPort)
				}
			}
		}
	}
}