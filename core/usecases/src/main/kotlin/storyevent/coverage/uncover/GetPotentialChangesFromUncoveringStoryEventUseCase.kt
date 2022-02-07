package com.soyle.stories.usecase.storyevent.coverage.uncover

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.exceptions.StoryEventAlreadyWithoutCoverage
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class GetPotentialChangesFromUncoveringStoryEventUseCase(
    private val storyEvents: StoryEventRepository,
    private val scenes: SceneRepository,
    private val characters: CharacterRepository
) : GetPotentialChangesFromUncoveringStoryEvent {

    override suspend fun invoke(
        storyEventId: StoryEvent.Id,
        output: GetPotentialChangesFromUncoveringStoryEvent.OutputPort
    ) {
        val storyEvent = storyEvents.getStoryEventOrError(storyEventId)
        if (storyEvent.sceneId == null) throw StoryEventAlreadyWithoutCoverage(storyEvent.id)
        val scene = scenes.getSceneById(storyEvent.sceneId!!)
            ?: return output.receivePotentialChanges(PotentialChangesFromUncoveringStoryEvent(
                listOf()
            ))
        val coveredStoryEvents = storyEvents.getStoryEventsCoveredByScene(scene.id)
        val coverageByCharacter = coveredStoryEvents.asSequence().flatMap { storyEvent ->
            storyEvent.involvedCharacters.asSequence().map {
                it.id to storyEvent
            }
        }.groupBy { it.first }.mapValues { it.value.size }

        output.receivePotentialChanges(
        PotentialChangesFromUncoveringStoryEvent(storyEvent.involvedCharacters
            .filterNot { scene.includesCharacter(it.id) }
            .filterNot {
                val coverageCount = coverageByCharacter[it.id]
                coverageCount == null || coverageCount > 1
            }
            .map {
                val character = characters.getCharacterById(it.id)
                ImplicitCharacterRemovedFromScene(scene.id, scene.name.value, it.id,
                    character?.displayName?.value
                )
            })
        )
    }
}