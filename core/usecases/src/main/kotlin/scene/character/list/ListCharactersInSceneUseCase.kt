package com.soyle.stories.usecase.scene.character.list

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.coroutineScope
import java.util.logging.Logger

class ListCharactersInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val characterRepository: CharacterRepository,
    private val storyEvents: StoryEventRepository
) : ListCharactersInScene {
    override suspend fun invoke(sceneId: Scene.Id, output: ListCharactersInScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val coveredStoryEvents = storyEvents.getStoryEventsCoveredByScene(sceneId)

        val allInvolvedCharacterIds = scene.includedCharacters.map { it.id } +
                coveredStoryEvents.flatMap { it.involvedCharacters }.map { it.id }

        output.receiveCharactersInScene(CharactersInScene(
            scene.id,
            scene.name.value,
            allInvolvedCharacterIds.toSet().mapNotNull { id ->
                val character =  characterRepository.getCharacterById(id) ?: return@mapNotNull null
                val characterInScene = scene.includedCharacters[id]
                CharacterInSceneItem(
                    id,
                    scene.id,
                    character.projectId,
                    character.displayName.value,
                    scene.includesCharacter(character.id),
                    characterInScene?.roleInScene,
                    listOfNotNull(coveredStoryEvents.find { it.involvedCharacters.containsEntityWithId(id) }?.id)
                        .map { CharacterInSceneSourceItem(it, "") }.toSet()
                )
            })
        )
    }
}