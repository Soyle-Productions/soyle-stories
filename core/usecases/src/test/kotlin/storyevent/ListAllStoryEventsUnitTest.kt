package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEventsUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class ListAllStoryEventsUnitTest {

	private val projectId = Project.Id()

	private var result: Any? = null

	@Test
	fun `empty repo`() {
		given(numberOfCreatedStoryEvents = 0)
		whenUseCaseIsExecuted()
		assertResponseModel(result, size = 0)
	}

	@Test
	fun `no story events in project`() {
		given(numberOfCreatedStoryEvents = 5, numberOfCreatedStoryEventsInProject = 0)
		whenUseCaseIsExecuted()
		assertResponseModel(result, size = 0)
	}

	@Test
	fun `some story events in project`() {
		given(numberOfCreatedStoryEventsInProject = 5)
		whenUseCaseIsExecuted()
		assertResponseModel(result, size = 5)
	}

	@Test
	fun `output in correct order`() {
		assertOrder("A:[C,B];C:[D,A];B:[A,];D:[,C]", "D:0;C:1;A:2;B:3")
	}

	private fun assertOrder(graph: String, expectedOrder: String)
	{
		val nodePattern = Pattern.compile("(\\D):\\[(\\D*),(\\D*)]")
		val nodes = graph.split(";")
		val storyEvents = HashMap<String, StoryEvent>(nodes.size)
		nodes.forEach {
			val matcher = nodePattern.matcher(it)
			if (matcher.matches()) {
				val nodeId = matcher.group(1)
				val prevNodeId = matcher.group(2).takeUnless { it.isBlank() }
				val nextNodeId = matcher.group(3).takeUnless { it.isBlank() }
				val prev = prevNodeId?.let { storyEvents[it] }
				val next = nextNodeId?.let { storyEvents[it] }
				val event = StoryEvent(StoryEvent.Id(), "", projectId, prev?.id, next?.id, null, listOf())
				storyEvents[nodeId] = event
				prevNodeId?.let { storyEvents[it]?.withNextId(event.id)?.let { storyEvents[prevNodeId] = it } }
				nextNodeId?.let { storyEvents[it]?.withPreviousId(event.id)?.let { storyEvents[nextNodeId] = it } }
			}
		}
		repository = StoryEventRepositoryDouble(
		  initialStoryEvents = storyEvents.values.toList()
		)
		whenUseCaseIsExecuted()
		val result = result as ListAllStoryEvents.ResponseModel

		val storyEventIdToNodeId = storyEvents.toList().associate { it.second.id.uuid to it.first }
		val orderPattern = Pattern.compile("(\\D):(\\d+)")
		val orderNodes = expectedOrder.split(";")
		val expectedIndices = HashMap<String, Int>(orderNodes.size)
		orderNodes.forEach {
			val matcher = orderPattern.matcher(it)
			if (matcher.matches())
			{
				val nodeId = matcher.group(1)
				val expectedIndex = matcher.group(2).toInt()
				expectedIndices[nodeId] = expectedIndex
			}
		}

		result.storyEventItems.forEach {
			assertEquals(
			  expectedIndices.getValue(storyEventIdToNodeId.getValue(it.storyEventId)),
			  it.influenceOrderIndex
			) { "Story Event Item does not have the correct index: $it, ${storyEventIdToNodeId.getValue(it.storyEventId)}" }
		}
	}

	private fun given(numberOfCreatedStoryEventsInProject: Int = 0, numberOfCreatedStoryEvents: Int = numberOfCreatedStoryEventsInProject)
	{
		var previous: StoryEvent? = null
		val storyEventMap: MutableMap<StoryEvent.Id, StoryEvent> = mutableMapOf()
		repeat(numberOfCreatedStoryEvents) {
			if (it < numberOfCreatedStoryEventsInProject) {
				val event = StoryEvent(StoryEvent.Id(), "", projectId, previous?.id, null, null, listOf())
				storyEventMap[event.id] = event
				previous?.let { storyEventMap[it.id] = it.withNextId(event.id) }
				previous = event
			} else {
				val event = StoryEvent(StoryEvent.Id(), "", Project.Id(), null, null, null, listOf())
				storyEventMap[event.id] = event
			}
		}
		repository = StoryEventRepositoryDouble(
		  initialStoryEvents = storyEventMap.values.toList()
		)
	}

	lateinit var repository: StoryEventRepository

	private fun whenUseCaseIsExecuted()
	{
		val useCase: ListAllStoryEvents = ListAllStoryEventsUseCase(repository)
		runBlocking {
			useCase.invoke(projectId.uuid, object : ListAllStoryEvents.OutputPort {
				override fun receiveListAllStoryEventsResponse(response: ListAllStoryEvents.ResponseModel) {
					result = response
				}
			})
		}
	}

	private fun assertResponseModel(actual: Any?, size: Int)
	{
		actual as ListAllStoryEvents.ResponseModel
		assertEquals(size, actual.storyEventItems.size)
	}

}