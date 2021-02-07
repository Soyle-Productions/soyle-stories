package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.storyevent.createStoryEvent.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.createStoryEvent.CreateStoryEventUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class CreateStoryEventUnitTest {

	companion object {
		val ValidStoryEventName = nonBlankStr("Valid Story Event Name")
		val NonExistentId = UUID.randomUUID()
		val ExistingId = UUID.randomUUID()
		const val After = false
		const val Before = true
		val projectId = Project.Id()
	}

	private var savedStoryEvent: StoryEvent? = null
	private var updatedStoryEvents: List<StoryEvent> = emptyList()
	var result: Any? = null

	@Test
	fun `name is not blank`() {
		given(providedName = ValidStoryEventName)
		whenUseCaseIsExecuted()
		assertStoryEvent(savedStoryEvent)
		assertValidResponseModel(result)
	}

	@Test
	fun `relative story event doesn't exist`()
	{
		given(providedName = ValidStoryEventName, providedRelativeStoryEventId = NonExistentId)
		whenUseCaseIsExecuted()
		assertStoryEventDoesNotExist(result)
	}

	@Test
	fun `insert before relative story event`()
	{
		given(providedName = ValidStoryEventName, providedRelativeStoryEventId = ExistingId, insert = Before)
		whenUseCaseIsExecuted()
		assertStoryEvent(savedStoryEvent)
		val response = assertValidResponseModel(result)
		assertEquals(0, response.influenceOrderIndex)
	}

	@Test
	fun `insert after relative story event`()
	{
		given(providedName = ValidStoryEventName, providedRelativeStoryEventId = ExistingId, insert = After)
		whenUseCaseIsExecuted()
		assertStoryEvent(savedStoryEvent)
		val response = assertValidResponseModel(result)
		assertEquals(1, response.influenceOrderIndex)
	}

	@Test
	fun `place at end of list when relative event not provided`()
	{
		given(providedName = ValidStoryEventName, numberOfExistingStoryEvents = 6)
		whenUseCaseIsExecuted()
		assertStoryEvent(savedStoryEvent)
		val response = assertValidResponseModel(result)
		assertEquals(6, response.influenceOrderIndex)
	}

	@Test
	fun `no updates output at end of list`() {
		given(providedName = ValidStoryEventName, numberOfExistingStoryEvents = 1)
		whenUseCaseIsExecuted()
		assertStoryEvent(savedStoryEvent)
		val response = assertValidResponseModel(result)
		assertEquals(0, response.updatedStoryEvents.size)
	}

	@Test
	fun `save last story event when inserted at the end`() {
		given(providedName = ValidStoryEventName, numberOfExistingStoryEvents = 1)
		whenUseCaseIsExecuted()
		val storyEvent = assertStoryEvent(savedStoryEvent)
		assertValidResponseModel(result)
		assertEquals(1, updatedStoryEvents.size)
		assertNotNull(storyEvent.previousStoryEventId)
		assertTrue(updatedStoryEvents.all { it.projectId == projectId })
	}

	@Test
	fun `output story events after insert index`()
	{
		given(providedName = ValidStoryEventName, providedRelativeStoryEventId = ExistingId, numberOfExistingStoryEvents = 6, relativeStoryEventIndex = 3, insert = Before)
		whenUseCaseIsExecuted()
		assertStoryEvent(savedStoryEvent)
		val response = assertValidResponseModel(result)
		assertEquals(3, response.influenceOrderIndex)
		assertEquals(3, response.updatedStoryEvents.size)
		assertEquals(2, updatedStoryEvents.size)
		assertTrue(updatedStoryEvents.all { it.projectId == projectId })
	}

	@Test
	fun `insert before first`() {
		given(providedName = ValidStoryEventName, providedRelativeStoryEventId = ExistingId, numberOfExistingStoryEvents = 6, relativeStoryEventIndex = 0, insert = Before)
		whenUseCaseIsExecuted()
		assertStoryEvent(savedStoryEvent)
		val response = assertValidResponseModel(result)
		assertEquals(0, response.influenceOrderIndex)
		assertEquals(1, response.updatedStoryEvents.first().influenceOrderIndex)
	}

	private fun given(providedName: NonBlankString, providedRelativeStoryEventId: UUID? = null, insert: Boolean = Before, numberOfExistingStoryEvents: Int = 0, relativeStoryEventIndex: Int = 0)
	{
		request = if (providedRelativeStoryEventId != null) {
			if (insert) {
				CreateStoryEvent.RequestModel.insertBefore(providedName, providedRelativeStoryEventId)
			} else {
				CreateStoryEvent.RequestModel.insertAfter(providedName, providedRelativeStoryEventId)
			}
		} else {
			CreateStoryEvent.RequestModel(providedName, projectId.uuid)
		}
		val storyEvents = mutableMapOf<StoryEvent.Id, StoryEvent>()

		val numberToMake = if (providedRelativeStoryEventId != null && numberOfExistingStoryEvents == 0) 1 else numberOfExistingStoryEvents

		var previous: StoryEvent? = null
		repeat(numberToMake) {
			val id = if (it == relativeStoryEventIndex && providedRelativeStoryEventId == ExistingId) StoryEvent.Id(
                ExistingId
            ) else StoryEvent.Id()
			val current = StoryEvent(id, "", projectId, previous?.id, null, null, listOf())
			storyEvents[id] = current
			if (previous != null) storyEvents[previous!!.id] = previous!!.withNextId(current.id)
			previous = current
		}
		repository = StoryEventRepositoryDouble(
		  initialStoryEvents = storyEvents.values.toList(),
		  onAddNewStoryEvent = {
			  savedStoryEvent = it
		  },
		  onUpdateStoryEvent = {
			  updatedStoryEvents = updatedStoryEvents + it
		  }
		)
	}

	lateinit var request: CreateStoryEvent.RequestModel
	lateinit var repository: StoryEventRepository

	private fun whenUseCaseIsExecuted()
	{
		val useCase: CreateStoryEvent = CreateStoryEventUseCase(repository)
		runBlocking {
			useCase.invoke(request, object : CreateStoryEvent.OutputPort {
				override fun receiveCreateStoryEventFailure(failure: Exception) {
					result = failure
				}

				override fun receiveCreateStoryEventResponse(response: CreateStoryEvent.ResponseModel) {
					result = response
				}
			})
		}
	}

	fun assertStoryEventDoesNotExist(actual: Any?)
	{
		actual as StoryEventDoesNotExist
		assertEquals(request.relativeStoryEventId, actual.storyEventId)
	}

	fun assertStoryEvent(actual: Any?): StoryEvent
	{
		actual as StoryEvent
		assertEquals(projectId, actual.projectId)
		assertEquals(request.name, actual.name)
		if (request.relativeStoryEventId != null) {
			if (request.before) assertEquals(request.relativeStoryEventId!!, actual.nextStoryEventId?.uuid)
			else assertEquals(request.relativeStoryEventId!!, actual.previousStoryEventId?.uuid)
		}
		return actual
	}

	fun assertValidResponseModel(actual: Any?): CreateStoryEvent.ResponseModel
	{
		actual as CreateStoryEvent.ResponseModel
		assertEquals(savedStoryEvent!!.id.uuid, actual.storyEventId)
		assertEquals(request.name, actual.storyEventName)
		return actual
	}

}