package com.soyle.stories.usecase.scene.character.inspect

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.character.CharacterInScene
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneItem
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneSourceItem
import com.soyle.stories.usecase.scene.common.InheritedMotivation
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class InspectCharacterInSceneUseCase(
    private val scenes: SceneRepository,
    private val characters: CharacterRepository,
    private val storyEvents: StoryEventRepository
) : InspectCharacterInScene {

    private sealed class Prerequisites {
        abstract val scene: Scene
        abstract val character: Character
        abstract val sources: List<StoryEvent>

        open val characterDesire: String get() = ""
        open val isExplicitlyIncluded: Boolean get() = false
        open val characterRoleInScene: RoleInScene? get() = null
    }

    private class CharacterImplicitlyIncluded(
        override val scene: Scene,
        override val character: Character,
        override val sources: List<StoryEvent>
    ) : Prerequisites()

    private class CharacterExplicitlyIncluded(
        override val scene: Scene,
        override val character: Character,
        override val sources: List<StoryEvent>,
        val characterInScene: CharacterInScene
    ) : Prerequisites() {
        override val characterDesire: String get() = characterInScene.desire
        override val isExplicitlyIncluded: Boolean get() = true
        override val characterRoleInScene: RoleInScene? get() = characterInScene.roleInScene
    }

    override suspend fun invoke(
        sceneId: Scene.Id,
        characterId: Character.Id,
        output: InspectCharacterInScene.OutputPort
    ) {
        val result = runCatching {
            with(getPrerequisiteState(sceneId, characterId)) {
                getInspection()
            }
        }
        output.receiveCharacterInSceneInspection(result)
    }

    private suspend fun getPrerequisiteState(
        sceneId: Scene.Id,
        characterId: Character.Id
    ): Prerequisites {
        val scene = scenes.getSceneOrError(sceneId.uuid)
        val character = characters.getCharacterOrError(characterId.uuid)
        val characterInScene = scene.includedCharacters[character.id]
        val sources = storyEvents.getStoryEventsCoveredByScene(scene.id)
            .filter { it.involvedCharacters.containsEntityWithId(character.id) }

        if (characterInScene != null) {
            return CharacterExplicitlyIncluded(scene, character, sources, characterInScene)
        }

        return getCharacterImplicitlyIncludedPrereq(scene, character, sources)
    }

    private fun getCharacterImplicitlyIncludedPrereq(
        scene: Scene,
        character: Character,
        sources: List<StoryEvent>
    ): CharacterImplicitlyIncluded {
        if (sources.isEmpty()) throw SceneDoesNotIncludeCharacter(scene.id, character.id)
        return CharacterImplicitlyIncluded(
            scene,
            character,
            sources
        )
    }

    private suspend fun Prerequisites.getInspection(): CharacterInSceneInspection {
        val sceneOrder = scenes.getSceneIdsInOrder(scene.projectId)!!

        return CharacterInSceneInspection(
            createCharacterInSceneItem(),
            characterDesire,
            sceneOrder.order.toList(),
            getAllMotivationsForCharacterInProject()
        )
    }

    private suspend fun Prerequisites.getAllMotivationsForCharacterInProject(): Map<Scene.Id, InheritedMotivation> =
        scenes.getScenesIncludingCharacter(character.id)
            .asSequence()
            .filter {
                it.includedCharacters.getOrError(character.id).motivation != null
            }
            .associate {
                it.id to InheritedMotivation(
                    it.id,
                    character.id,
                    it.name.value,
                    it.includedCharacters.getOrError(character.id).motivation!!
                )
            }

    private fun Prerequisites.createCharacterInSceneItem(): CharacterInSceneItem =
        CharacterInSceneItem(
            character.id,
            scene.id,
            character.projectId,
            character.displayName.value,
            isExplicitlyIncluded,
            characterRoleInScene,
            sources.mapTo(LinkedHashSet(sources.size)) {
                CharacterInSceneSourceItem(it.id, it.name.value)
            }
        )
}