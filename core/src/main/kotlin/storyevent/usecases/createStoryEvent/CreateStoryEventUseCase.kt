package com.soyle.stories.storyevent.usecases.createStoryEvent

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.storyevent.StoryEventDoesNotExist
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import com.soyle.stories.storyevent.usecases.getOrderOfEventsInProject
import com.soyle.stories.storyevent.usecases.toItem
import com.soyle.stories.storyevent.usecases.validateStoryEventName
import java.util.*

class CreateStoryEventUseCase(
  private val storyEventRepository: StoryEventRepository
) : CreateStoryEvent {

	override suspend fun invoke(request: CreateStoryEvent.RequestModel, output: CreateStoryEvent.OutputPort) {
		val response = try {
			createStoryEvent(request)
		} catch (s: StoryEventException) {
			return output.receiveCreateStoryEventFailure(s)
		}
		output.receiveCreateStoryEventResponse(response)
	}

	private suspend fun createStoryEvent(request: CreateStoryEvent.RequestModel): CreateStoryEvent.ResponseModel
	{
		validateStoryEventName(request.name)

		val relativeStoryEvent = getRelativeStoryEvent(request.relativeStoryEventId, request.projectId?.let(Project::Id))
		val storyEvent = makeNewStoryEvent(request, relativeStoryEvent)
		saveStoryEvents(storyEvent, relativeStoryEvent)

		val events = getOrderOfEventsInProject(storyEventRepository, request.projectId ?: relativeStoryEvent!!.projectId.uuid)

		val indexOfNewEvent= events.indexOfFirst {
			it.id == storyEvent.id
		}

		return CreateStoryEvent.ResponseModel(storyEvent.toItem(indexOfNewEvent), events.subList(indexOfNewEvent + 1, events.size).mapIndexed { index , it ->
			it.toItem(index+(indexOfNewEvent + 1))
		})
	}

	private suspend fun getRelativeStoryEvent(storyEventId: UUID?, projectId: Project.Id?): StoryEvent?
	{
		return if (storyEventId != null) {
			storyEventRepository.getStoryEventById(StoryEvent.Id(storyEventId))
			  ?: throw StoryEventDoesNotExist(storyEventId)
		} else {
			storyEventRepository.getLastStoryEventInProject(projectId!!)
		}
	}

	private fun makeNewStoryEvent(request: CreateStoryEvent.RequestModel, relativeStoryEvent: StoryEvent?): StoryEvent
	{
		return StoryEvent(
		  StoryEvent.Id(),
		  request.name,
		  request.projectId?.let { Project.Id(it) } ?: relativeStoryEvent!!.projectId,
		  relativeStoryEvent?.id?.takeIf { ! request.before },
		  relativeStoryEvent?.id?.takeIf { request.before },
		  null,
		  listOf()
		)
	}

	private suspend fun saveStoryEvents(newStoryEvent: StoryEvent, relativeStoryEvent: StoryEvent?)
	{
		if (relativeStoryEvent != null)
		{
			if (newStoryEvent.nextStoryEventId == relativeStoryEvent.id) {
				val currentPrevious = relativeStoryEvent.previousStoryEventId?.let { storyEventRepository.getStoryEventById(it) }
				val updates: List<StoryEvent> = listOfNotNull(
				  currentPrevious?.withNextId(newStoryEvent.id),
				  relativeStoryEvent.withPreviousId(newStoryEvent.id)
				)
				storyEventRepository.updateStoryEvents(*updates.toTypedArray())
				if (currentPrevious != null) {
					storyEventRepository.addNewStoryEvent(newStoryEvent.withPreviousId(currentPrevious.id))
				} else {
					storyEventRepository.addNewStoryEvent(newStoryEvent)
				}
			}
			else {
				val currentNext = relativeStoryEvent.nextStoryEventId?.let { storyEventRepository.getStoryEventById(it) }
				val updates: List<StoryEvent> = listOfNotNull(
				  currentNext?.withPreviousId(newStoryEvent.id),
				  relativeStoryEvent.withNextId(newStoryEvent.id)
				)
				storyEventRepository.updateStoryEvents(*updates.toTypedArray())
				if (currentNext != null) {
					storyEventRepository.addNewStoryEvent(newStoryEvent.withNextId(currentNext.id))
				} else {
					storyEventRepository.addNewStoryEvent(newStoryEvent)
				}
			}
		} else {
			storyEventRepository.addNewStoryEvent(newStoryEvent)
		}
	}
}