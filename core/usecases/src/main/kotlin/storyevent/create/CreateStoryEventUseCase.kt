package com.soyle.stories.usecase.storyevent.create

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.StoryEventUpdate
import com.soyle.stories.domain.storyevent.SuccessfulStoryEventUpdate
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.getOrderOfEventsInProject
import com.soyle.stories.usecase.storyevent.toItem
import java.util.*

class CreateStoryEventUseCase(
    private val storyEventRepository: StoryEventRepository
) : CreateStoryEvent {

    override suspend fun invoke(request: CreateStoryEvent.RequestModel, output: CreateStoryEvent.OutputPort) {
        val response = createStoryEvent(request)
        output.receiveCreateStoryEventResponse(response)
    }

    private suspend fun createStoryEvent(request: CreateStoryEvent.RequestModel): CreateStoryEvent.ResponseModel {
        val (storyEvent, storyEventCreated) = makeNewStoryEvent(request) as SuccessfulStoryEventUpdate

        storyEventRepository.addNewStoryEvent(storyEvent)

        return CreateStoryEvent.ResponseModel(
            storyEventCreated
        )
    }

    private suspend fun makeNewStoryEvent(request: CreateStoryEvent.RequestModel): StoryEventUpdate<StoryEventCreated> {
        return StoryEvent.create(
            request.name,
            calculateStoryEventTime(request),
            request.projectId
        )
    }

    private suspend fun calculateStoryEventTime(request: CreateStoryEvent.RequestModel): Long {
        return when (request.time) {
            is CreateStoryEvent.RequestModel.RequestedStoryEventTime.Absolute -> request.time.time
            is CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative -> calculateRelativeTime(request.time)
            else -> getMaximumStoryEventTimeInProject(request.projectId) + 1
        }
    }

    private suspend fun calculateRelativeTime(relativeTime: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative): Long {
        val relativeStoryEvent = getRelativeStoryEvent(relativeTime.relativeStoryEventId)
        return relativeStoryEvent.time + relativeTime.delta
    }

    private suspend fun getRelativeStoryEvent(storyEventId: StoryEvent.Id): StoryEvent {
        return storyEventRepository.getStoryEventById(storyEventId)
            ?: throw StoryEventDoesNotExist(storyEventId.uuid)
    }

    private suspend fun getMaximumStoryEventTimeInProject(projectId: Project.Id): Long {
        return storyEventRepository.listStoryEventsInProject(projectId).maxOfOrNull { it.time } ?: 0L
    }

}