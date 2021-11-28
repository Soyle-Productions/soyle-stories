package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.domain.storyevent.storyEventName
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEventsUseCase
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEvent
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEventUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class `List All Story Events Unit Test` {

	// Summary
	/** Lists all the story event metadata for a given project */

	// Preconditions
	/** The project exists */
	private val projectId = Project.Id()

	// post conditions
	/** outputs story event metadata */
	private var responseModel: ListAllStoryEvents.ResponseModel? = null
	private val storyEventRepository = StoryEventRepositoryDouble()

	// Use Case
	private val useCase: ListAllStoryEvents = ListAllStoryEventsUseCase(storyEventRepository)
	private fun listAllStoryEvents() {
		runBlocking {
			useCase.invoke(projectId) {
				responseModel = it
			}
		}
	}

	@Test
	fun `should return an empty result`() {
		listAllStoryEvents()
		responseModel!!.size.mustEqual(0)
	}

	@Nested
	inner class `Given Story Events have been Created` {

		private val storyEventsInProject = List(8) { makeStoryEvent(projectId = projectId) }
		private val storyEventsInOtherProjects = List(5) { makeStoryEvent() }
		init {
			storyEventsInProject.forEach(storyEventRepository::givenStoryEvent)
			storyEventsInOtherProjects.forEach(storyEventRepository::givenStoryEvent)
		}

		@Test
		fun `should output story event ids`() {
			listAllStoryEvents()
			responseModel!!.size.mustEqual(8)
			responseModel!!.map { it.storyEventId }.toSet()
				.mustEqual(storyEventsInProject.map { it.id }.toSet())
		}

		@Test
		fun `should output story event names`() {
			listAllStoryEvents()
			val storyEventMap = storyEventsInProject.associateBy { it.id }
			responseModel!!.forEach {
				it.storyEventName.mustEqual(storyEventMap.getValue(it.storyEventId).name.value)
			}
		}

		@Test
		fun `should output story event times`() {
			listAllStoryEvents()
			val storyEventMap = storyEventsInProject.associateBy { it.id }
			responseModel!!.forEach {
				it.time.mustEqual(storyEventMap.getValue(it.storyEventId).time.toLong())
			}
		}

	}

}