package com.soyle.stories.usecase.storyevent.character.remove

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.UnSuccessful
import com.soyle.stories.domain.storyevent.character.InvolvedCharacter
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class GetPotentialChangesOfRemovingCharacterFromStoryEventUseCase(
    private val storyEvents: StoryEventRepository,
    private val characters: CharacterRepository,
    private val scenes: SceneRepository
) : GetPotentialChangesOfRemovingCharacterFromStoryEvent {

    override suspend fun invoke(
        storyEventId: StoryEvent.Id,
        characterId: Character.Id,
        output: GetPotentialChangesOfRemovingCharacterFromStoryEvent.OutputPort
    ) {
        val storyEvent = storyEvents.getStoryEventOrError(storyEventId)
        val character = characters.getCharacterOrError(characterId.uuid)
        val potentialUpdate = storyEvent.withCharacterRemoved(character.id)
        if (potentialUpdate is UnSuccessful) throw potentialUpdate.reason!!
        val effects = getEffects(storyEvent.sceneId, character)
        output.receivePotentialChanges(PotentialChangesOfRemovingCharacterFromStoryEvent(effects))
    }

    private suspend fun getEffects(sceneId: Scene.Id?, character: Character): List<ImplicitCharacterRemovedFromScene> {
        if (sceneId == null) return emptyList()
        val scene = scenes.getSceneById(sceneId) ?: return emptyList()
        val coveredStoryEvents = storyEvents.getStoryEventsCoveredByScene(scene.id)
        val coverage = coveredStoryEvents.count { it.involvedCharacters.containsEntityWithId(character.id) }
        return listOfNotNull(
            ImplicitCharacterRemovedFromScene(scene.id, scene.name.value, character.id, character.displayName.value)
                .takeUnless { scene.includesCharacter(character.id) }
                ?.takeUnless { coverage > 1 }
        )
    }
}