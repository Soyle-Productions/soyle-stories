package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.domain.storyevent.storyEventName
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEvent
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEventUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Rename Story Event Unit Test` {

	// Summary
	/** The name of the specified story event is changed to the new specified name */

	// Preconditions
	/** A project has been created and opened */
	private val projectId = Project.Id()
	/** The story event exists */
	private val storyEvent = makeStoryEvent()

	// post conditions
	/** outputs a story event renamed event */
	private var renamedStoryEvent: StoryEventRenamed? = null
	/** updates the story event in the repository */
	private var updatedStoryEvent: StoryEvent? = null
	private val storyEventRepository = StoryEventRepositoryDouble(onUpdateStoryEvent = ::updatedStoryEvent::set)

	// Use Case
	private val useCase: RenameStoryEvent = RenameStoryEventUseCase(storyEventRepository)
	private fun renameStoryEvent(inputName: NonBlankString = storyEventName()) {
		runBlocking {
			useCase.invoke(storyEvent.id, inputName) {
				renamedStoryEvent = it.storyEventRenamed
			}
		}
	}

	@Test
	fun `should throw error if story event doesn't exist`() {
		val error = assertThrows<StoryEventDoesNotExist> { renameStoryEvent() }
		error.storyEventId.mustEqual(storyEvent.id.uuid)
	}

	@Nested
	inner class `Given Story Event Exists` {

		init {
		    storyEventRepository.givenStoryEvent(storyEvent)
		}

		@Test
		fun `should update story event`() {
			val inputName = storyEventName()
			renameStoryEvent(inputName)
			updatedStoryEvent!!.mustEqual(storyEvent.withName(inputName).storyEvent)
		}

		@Test
		fun `should output story event renamed event`() {
			val inputName = storyEventName()
			renameStoryEvent(inputName)
			renamedStoryEvent!!.mustEqual((storyEvent.withName(inputName) as Successful).change)
		}

		@Nested
		inner class `When input name is same as current name` {

			@Test
			fun `should not update story event`() {
				renameStoryEvent(storyEvent.name)
				assertNull(updatedStoryEvent)
			}

			@Test
			fun `should not produce event`() {
				renameStoryEvent(storyEvent.name)
				assertNull(renamedStoryEvent)
			}

		}

	}

}