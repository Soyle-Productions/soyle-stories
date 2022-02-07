package com.soyle.stories.usecase.storyevent.create

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.*
import com.soyle.stories.domain.storyevent.events.StoryEventChange
import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.*
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class CreateStoryEventUseCase(
    private val storyEventRepository: StoryEventRepository,
    private val scenes: SceneRepository
) : CreateStoryEvent {

    override suspend fun invoke(request: CreateStoryEvent.RequestModel, output: CreateStoryEvent.OutputPort) {
        val (creationUpdate, rescheduleUpdates) = createStoryEvent(request)
        val coveredBySceneUpdate = creationUpdate.coverBySceneIfNeeded(request.sceneId)

        commitChanges(coveredBySceneUpdate ?: creationUpdate, rescheduleUpdates)

        val response = CreateStoryEvent.ResponseModel(
            creationUpdate.change,
            coveredBySceneUpdate?.result()?.getOrNull(),
            rescheduleUpdates.map { it.change }
        )
        output.receiveCreateStoryEventResponse(response)
    }

    private suspend fun createStoryEvent(
        request: CreateStoryEvent.RequestModel
    ): Pair<SuccessfulStoryEventCreatedUpdate, List<SuccessfulStoryEventRescheduledUpdate>> {
        val (creationUpdate, rescheduleUpdates) = StoryEventTimeService(storyEventRepository)
            .createStoryEvent(
                request.name,
                calculateStoryEventTime(request),
                request.projectId
            )
        return creationUpdate as SuccessfulStoryEventUpdate to
                rescheduleUpdates.filterIsInstance<SuccessfulStoryEventRescheduledUpdate>()
    }

    private suspend fun StoryEventCreatedUpdate.coverBySceneIfNeeded(
        sceneId: Scene.Id?
    ): StoryEventCoveredBySceneUpdate? {
        if (sceneId == null) return null
        scenes.getSceneOrError(sceneId.uuid)
        return storyEvent.coveredByScene(sceneId)
    }

    private suspend fun commitChanges(
        storyEventUpdate: StoryEventUpdate<*>,
        rescheduleUpdates: List<SuccessfulStoryEventRescheduledUpdate>
    ) {
        storyEventRepository.addNewStoryEvent(storyEventUpdate.storyEvent)
        rescheduleUpdates.forEach { storyEventRepository.updateStoryEvent(it.storyEvent) }
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
        return relativeStoryEvent.time.toLong() + relativeTime.delta
    }

    private suspend fun getRelativeStoryEvent(storyEventId: StoryEvent.Id): StoryEvent {
        return storyEventRepository.getStoryEventById(storyEventId)
            ?: throw StoryEventDoesNotExist(storyEventId.uuid)
    }

    private suspend fun getMaximumStoryEventTimeInProject(projectId: Project.Id): Long {
        return storyEventRepository.listStoryEventsInProject(projectId).maxOfOrNull { it.time.toLong() } ?: 0L
    }

}