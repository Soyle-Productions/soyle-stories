package com.soyle.stories.usecase.scene.createNewScene

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.scene.order.SceneOrderService
import com.soyle.stories.domain.scene.order.SceneOrderUpdate
import com.soyle.stories.domain.scene.order.SuccessfulSceneOrderUpdate
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.StoryEventTimeService
import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.domain.storyevent.SuccessfulStoryEventUpdate
import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class CreateNewSceneUseCase(
    private val sceneOrderService: SceneOrderService,
    private val storyEventTimeService: StoryEventTimeService,
    private val sceneRepository: SceneRepository,
    private val proseRepository: ProseRepository,
    private val storyEventRepository: StoryEventRepository
) : CreateNewScene {

    override suspend fun invoke(request: CreateNewScene.RequestModel, output: CreateNewScene.OutputPort) {
        val (prose) = Prose.create(request.projectId)
        val storyEventUpdate = createCorrespondingStoryEvent(request)
        val sceneOrderUpdate = createSceneInProject(request, storyEventUpdate.storyEvent.id, prose.id)
        val storyEventCovered = storyEventUpdate.storyEvent.coveredByScene(sceneOrderUpdate.change.scene.id)
        storyEventCovered as Successful

        commitChanges(sceneOrderUpdate, prose, storyEventCovered)

        output.newSceneCreated(response(sceneOrderUpdate, storyEventUpdate.change to storyEventCovered.change))
    }

    private suspend fun commitChanges(
        sceneOrderUpdate: SceneOrderUpdate.Successful<Updated<SceneCreated>>,
        prose: Prose,
        storyEventUpdate: Successful<*>
    ) {
        sceneRepository.createNewScene(sceneOrderUpdate.change.scene)
        sceneRepository.updateSceneOrder(sceneOrderUpdate.sceneOrder)
        proseRepository.addProse(prose)
        storyEventRepository.addNewStoryEvent(storyEventUpdate.storyEvent)
    }

    private fun response(
        sceneOrderUpdate: SceneOrderUpdate.Successful<Updated<SceneCreated>>,
        storyEventUpdates: Pair<StoryEventCreated, StoryEventCoveredByScene>
    ) = CreateNewScene.ResponseModel(
        sceneOrderUpdate.change.change,
        storyEventUpdates.first,
        sceneOrderUpdate,
        storyEventUpdates.second
    )

    private suspend fun createSceneInProject(
        request: CreateNewScene.RequestModel,
        storyEventId: StoryEvent.Id,
        proseId: Prose.Id
    ): SceneOrderUpdate.Successful<Updated<SceneCreated>> {
        val sceneOrder = sceneRepository.getSceneIdsInOrder(request.projectId) ?: SceneOrder.initializeInProject(request.projectId)

        return sceneOrderService.createScene(
            sceneOrder,
            request.name,
            storyEventId,
            proseId,
            relativeSceneIndex(request, sceneOrder.order)
        ) as SuccessfulSceneOrderUpdate
    }

    private fun relativeSceneIndex(
        request: CreateNewScene.RequestModel,
        sceneOrder: Set<Scene.Id>
    ): Int {
        val relativeSceneId = request.relativeSceneId ?: return -1
        if (relativeSceneId !in sceneOrder) throw SceneDoesNotExist(relativeSceneId.uuid)
        val indexOfRelativeScene = sceneOrder.indexOf(relativeSceneId)
        return if (request.isBeforeScene) {
            indexOfRelativeScene
        } else indexOfRelativeScene + 1
    }

    private suspend fun createCorrespondingStoryEvent(request: CreateNewScene.RequestModel): Successful<StoryEventCreated> {
        val update = storyEventTimeService.createStoryEvent(
            request.name,
            maxStoryEventTimeInProject(request.projectId),
            request.projectId
        ).single() as Successful

        update.change as StoryEventCreated

        @Suppress("UNCHECKED_CAST")
        return update as Successful<StoryEventCreated>
    }

    private suspend fun maxStoryEventTimeInProject(projectId: Project.Id) =
        (storyEventRepository.listStoryEventsInProject(projectId)
            .maxOfOrNull { it.time.toLong() } ?: 0L)
            .plus(1)


}