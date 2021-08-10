package com.soyle.stories.storyevent.createStoryEventDialog

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.create.CreateStoryEventController

class CreateStoryEventDialogController(
  private val presenter: CreateStoryEventDialogPresenter,
  private val createStoryEventController: CreateStoryEventController
) : CreateStoryEventDialogViewListener {

	override fun getValidState() {
		presenter.displayCreateStoryEventDialog()
	}

	override fun createStoryEvent(name: NonBlankString, relativeStoryEventId: String?, relativePosition: String?) {
		when (relativePosition) {
			"before" -> createStoryEventController.createStoryEventBefore(name, relativeStoryEventId!!)
			"after" -> createStoryEventController.createStoryEventAfter(name, relativeStoryEventId!!)
			null -> createStoryEventController.createStoryEvent(name)
		}
	}

}