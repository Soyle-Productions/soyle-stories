package com.soyle.stories.usecase.scene.character.listAvailableCharacters

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import java.util.*

class ListAvailableCharactersToIncludeInSceneUseCase(
    private val scenes: SceneRepository,
    private val storyEvents: StoryEventRepository,
    private val characters: CharacterRepository
) : ListAvailableCharactersToIncludeInScene {

    override suspend fun invoke(
        sceneId: Scene.Id,
        output: ListAvailableCharactersToIncludeInScene.OutputPort
    ): Result<Unit> {
        return runCatching { scenes.getSceneOrError(sceneId.uuid) }
            .map { Prerequisites(it, storyEvents.getStoryEventsCoveredByScene(it.id)) }
            .map { it.getAvailableCharacters() }
            .onSuccess { output.receiveAvailableCharactersToAddToScene(it) }
            .map {  }
    }

    private class Prerequisites(
        val scene: Scene,
        coveredStoryEvents: List<StoryEvent>
    ) {
        val characterIdsInStoryEvents: Set<Character.Id> by lazy {
            coveredStoryEvents.flatMap { it.involvedCharacters }
                .map { it.id }
                .toSet()
        }
    }

    private suspend fun Prerequisites.getAvailableCharacters(): AvailableCharactersToAddToScene {
        return AvailableCharactersToAddToScene(
            scene.id,
            characters.listCharactersInProject(scene.projectId)
                .asSequence()
                .filterNot { scene.includesCharacter(it.id) }
                .filterNot { it.id in characterIdsInStoryEvents }
                .map { CharacterItem(it.id.uuid, it.displayName.value, null) }
                .toList()
        )
    }
}