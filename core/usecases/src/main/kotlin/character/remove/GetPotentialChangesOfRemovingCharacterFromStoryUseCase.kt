package com.soyle.stories.usecase.character.remove

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.effects.CharacterInSceneEffect
import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.scene.character.effects.IncludedCharacterNotInProject
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.flow.*

class GetPotentialChangesOfRemovingCharacterFromStoryUseCase(
    private val characters: CharacterRepository,
    private val storyEvents: StoryEventRepository,
    private val scenes: SceneRepository
) : GetPotentialChangesOfRemovingCharacterFromStory {
    override suspend fun invoke(
        characterId: Character.Id,
        output: GetPotentialChangesOfRemovingCharacterFromStory.OutputPort
    ) {
        val character = characters.getCharacterOrError(characterId.uuid)
        val effects = getEffects(character)
        output.receivePotentialChanges(PotentialChangesOfRemovingCharacterFromStory(effects))
    }

    private suspend fun getEffects(character: Character): List<CharacterInSceneEffect>
    {
        return flow {
            emit(getScenesWithCharacterExplicitlyIncluded(character.id).map { it.toIncludedCharacterRemoved(character) })
            emit(getScenesWithCharacterImplicitlyIncluded(character.id).map { it.toImplicitCharacterRemoved(character) })
        }.flattenConcat().toList()
    }

    private suspend fun getScenesWithCharacterExplicitlyIncluded(characterId: Character.Id) =
        scenes.getScenesIncludingCharacter(characterId).asFlow()

    private fun Scene.toIncludedCharacterRemoved(character: Character) =
        IncludedCharacterNotInProject(id, name.value, character.id, character.displayName.value)

    private suspend fun getScenesWithCharacterImplicitlyIncluded(characterId: Character.Id) =
        storyEvents.getStoryEventsWithCharacter(characterId)
            .mapNotNull { it.sceneId }.toSet().asFlow()
            .mapNotNull { scenes.getSceneById(it)?.takeUnless { it.includesCharacter(characterId) } }

    private fun Scene.toImplicitCharacterRemoved(character: Character) =
        ImplicitCharacterRemovedFromScene(id, name.value, character.id, character.displayName.value)

}