package com.soyle.stories.usecase.storyevent.coverage.cover

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.entities.updates.Update
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneUpdate
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.domain.scene.events.StoryEventAddedToScene
import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
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

        val (successfulUpdates, unsuccessfulUpdates) = updateSceneAndStoryEvent(scene, storyEvent)

        if (successfulUpdates.isEmpty()) {
            val throwable = unsuccessfulUpdates.find { it.reason != null }?.reason ?: return
            throw throwable
        }

        commitChanges(successfulUpdates)

        output.receiveCoverStoryEventInSceneResponse(response(successfulUpdates))
    }

    private suspend fun updateSceneAndStoryEvent(
        scene: Scene,
        storyEvent: StoryEvent
    ): Pair<List<Update.Successful<Entity<*>, *>>, List<Update.UnSuccessful<Entity<*>>>> {
        // may still contain [storyEvent] if it was previously covered
        val sceneWithoutOtherStoryEventUpdate = removeOtherStoryEventsFromScene(scene, storyEvent)

        val updates: List<Update<Entity<out Any>>> = listOfNotNull(
            sceneWithoutOtherStoryEventUpdate,
            (sceneWithoutOtherStoryEventUpdate?.scene ?: scene).withStoryEvent(storyEvent.id),
            storyEvent.coveredByScene(scene.id),
            uncoverCoveredStoryEvent(sceneWithoutOtherStoryEventUpdate)
        )

        return updates.filterIsInstance<Update.Successful<Entity<*>, *>>() to updates.filterIsInstance<Update.UnSuccessful<Entity<*>>>()
    }

    private suspend fun uncoverCoveredStoryEvent(sceneWithoutOtherStoryEventUpdate: SceneUpdate<StoryEventRemovedFromScene>?) =
        if (sceneWithoutOtherStoryEventUpdate is Updated) {
            val otherStoryEventId = sceneWithoutOtherStoryEventUpdate.event.storyEventId
            val coveredStoryEvent = storyEventRepository.getStoryEventOrError(otherStoryEventId)
            coveredStoryEvent.withoutCoverage()
        } else null

    private fun removeOtherStoryEventsFromScene(
        scene: Scene,
        storyEvent: StoryEvent
    ): SceneUpdate<StoryEventRemovedFromScene>? =
        if (scene.coveredStoryEvents.isNotEmpty() && storyEvent.id !in scene.coveredStoryEvents) {
            scene.withoutStoryEvent(scene.coveredStoryEvents.single())
        } else null

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