package com.soyle.stories.usecase.storyevent.coverage.cover

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.entities.updates.Update
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class CoverStoryEventInSceneUseCase(
    private val storyEventRepository: StoryEventRepository,
    private val sceneRepository: SceneRepository
) : CoverStoryEventInScene {

    override suspend fun invoke(
        storyEventId: StoryEvent.Id,
        sceneId: Scene.Id,
        output: CoverStoryEventInScene.OutputPort
    ) {
        val storyEvent = storyEventRepository.getStoryEventOrError(storyEventId)
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)

        // update scene, possibly twice
        val sceneWithoutOtherStoryEventUpdate =
            if (scene.coveredStoryEvents.isNotEmpty() && storyEvent.id !in scene.coveredStoryEvents) {
                scene.withoutStoryEvent(scene.coveredStoryEvents.single())
            } else null

        // if scene had to remove previous story event, update the story event too
        val otherStoryEventUpdate = (sceneWithoutOtherStoryEventUpdate as? Updated)?.let {
            val otherStoryEventId = sceneWithoutOtherStoryEventUpdate.event.storyEventId
            val coveredStoryEvent = storyEventRepository.getStoryEventOrError(otherStoryEventId)
            coveredStoryEvent.withoutCoverage()
        }

        // add new story event to scene
        val sceneWithStoryEventUpdate = (sceneWithoutOtherStoryEventUpdate?.scene ?: scene).withStoryEvent(storyEvent)

        // cover story event with scene
        val storyEventUpdate = storyEvent.coveredByScene(scene.id)

        val (successfulUpdates, unsuccessfulUpdates) = listOfNotNull(
            sceneWithoutOtherStoryEventUpdate,
            otherStoryEventUpdate,
            sceneWithStoryEventUpdate,
            storyEventUpdate
        ).run {
            Pair(
                filterIsInstance<Update.Successful<Entity<*>, *>>(),
                filterIsInstance<Update.UnSuccessful<*>>()
            )
        }

        if (successfulUpdates.isEmpty()) {
            val throwable = unsuccessfulUpdates.find { it.reason != null }?.reason ?: return
            throw throwable
        }

        // remove story event from previous scene
        @Suppress("UNCHECKED_CAST")
        val previousCoverageSceneUpdate = storyEvent.sceneId
            ?.takeUnless { it == scene.id }
            ?.let { sceneRepository.getSceneById(it) }
            ?.withoutStoryEvent(storyEvent.id)
            as? Update.Successful<Entity<*>, *>

        commitChanges(successfulUpdates + listOfNotNull(previousCoverageSceneUpdate))

        output.receiveCoverStoryEventInSceneResponse(response(listOfNotNull(previousCoverageSceneUpdate) + successfulUpdates))
    }

    private suspend fun commitChanges(successfulUpdates: List<Update.Successful<Entity<*>, *>>) {
        // Grouping by component id ensures that only the last version is updated
        successfulUpdates.groupBy { it.component1().id }.values
            .forEach { updates ->
                when (val entity = updates.last().component1()) {
                    is StoryEvent -> storyEventRepository.updateStoryEvent(entity)
                    is Scene -> sceneRepository.updateScene(entity)
                }
            }
    }

    private fun response(successfulUpdates: List<Update.Successful<Entity<*>, *>>) =
        CoverStoryEventInScene.ResponseModel(
            storyEventAddedToScene = successfulUpdates.findSingleEvent(),
            storyEventCoveredByScene = successfulUpdates.findSingleEvent(),
            storyEventRemovedFromScene = successfulUpdates.findSingleEvent(),
            storyEventUncoveredFromScene = successfulUpdates.findSingleEvent()
        )

    private inline fun <reified T : Any> List<Update.Successful<*, *>>.findSingleEvent(): T? = asSequence()
        .mapNotNull { it.change as? T }.singleOrNull()

}