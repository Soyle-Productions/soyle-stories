package com.soyle.stories.usecase.scene.storyevent.list

import arrow.core.extensions.mapk.align.empty
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.toItem

class ListStoryEventsCoveredBySceneUseCase(
    private val sceneRepository: SceneRepository,
    private val storyEventRepository: StoryEventRepository,
    private val characters: CharacterRepository
) : ListStoryEventsCoveredByScene {
    override suspend fun invoke(sceneId: Scene.Id, output: ListStoryEventsCoveredByScene.OutputPort) {
        sceneRepository.getSceneOrError(sceneId.uuid)
        val storyEvents = storyEventRepository.getStoryEventsCoveredByScene(sceneId)

        output.receiveStoryEventsCoveredByScene(
            StoryEventsInScene(
                sceneId,
                storyEvents.map {
                    StoryEventInSceneItem(
                        it.id,
                        sceneId,
                        it.name.value,
                        it.time.toLong(),
                        it.involvedCharacters
                            .mapNotNull { characters.getCharacterById(it.id) }
                            .map { CharacterItem(it.id.uuid, it.displayName.value, null) }
                    )
                }
            )
        )
    }
}