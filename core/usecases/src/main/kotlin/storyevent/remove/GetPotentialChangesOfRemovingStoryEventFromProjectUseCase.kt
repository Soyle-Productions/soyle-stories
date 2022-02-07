package com.soyle.stories.usecase.storyevent.remove

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.character.InvolvedCharacter
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class GetPotentialChangesOfRemovingStoryEventFromProjectUseCase(
    private val storyEvents: StoryEventRepository,
    private val scenes: SceneRepository,
    private val characters: CharacterRepository
) : GetPotentialChangesOfRemovingStoryEventFromProject {

    override suspend fun invoke(
        storyEventId: StoryEvent.Id,
        output: GetPotentialChangesOfRemovingStoryEventFromProject.OutputPort
    ) {
        val storyEvent = storyEvents.getStoryEventOrError(storyEventId)
        val effects = getEffects(storyEvent.sceneId, storyEvent.involvedCharacters)
        output.receivePotentialChanges(PotentialChangesOfRemovingStoryEventFromProject(effects))
    }

    private suspend fun getEffects(sceneId: Scene.Id?, involvedCharacters: Collection<InvolvedCharacter>): List<ImplicitCharacterRemovedFromScene> {
        if (sceneId == null) return emptyList()
        val scene = scenes.getSceneById(sceneId) ?: return emptyList()
        val coveredStoryEvents = storyEvents.getStoryEventsCoveredByScene(scene.id)
        val coverageCountByCharacter = coveredStoryEvents.asSequence()
            .flatMap { storyEvent -> storyEvent.involvedCharacters.asSequence().map { it.id to storyEvent.id } }
            .groupBy { it.first }
            .mapValues { it.value.size }
        return involvedCharacters
            .filterNot { scene.includesCharacter(it.id) }
            .filterNot { coverageCountByCharacter.getValue(it.id) > 1 }
            .map {
                val characterName = characters.getCharacterById(it.id)?.displayName?.value
                ImplicitCharacterRemovedFromScene(scene.id, scene.name.value, it.id, characterName)
            }
    }

}