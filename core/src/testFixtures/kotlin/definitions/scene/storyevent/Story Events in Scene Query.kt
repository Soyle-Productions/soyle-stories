package com.soyle.stories.core.definitions.scene.storyevent

import com.soyle.stories.core.framework.scene.`Covered Story Events Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredBySceneUseCase
import com.soyle.stories.usecase.scene.storyevent.list.StoryEventsInScene
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.runBlocking

class `Story Events in Scene Query`(
    private val sceneRepository: SceneRepository,
    private val storyEventRepository: StoryEventRepository,
    private val characterRepository: CharacterRepository
) : `Covered Story Events Steps`.When.UserQueries {

    override fun `lists the story events covered by the`(scene: Scene.Id): StoryEventsInScene {
        val useCase = ListStoryEventsCoveredBySceneUseCase(sceneRepository, storyEventRepository, characterRepository)
        lateinit var storyEventsInScene: StoryEventsInScene
        runBlocking {
            useCase(scene) {
                storyEventsInScene = it
            }
        }
        return storyEventsInScene
    }

    override fun `lists the story events covered by the`(
        scene: Scene.Id,
        andInvolveThe: Character.Id
    ): StoryEventsInScene {
        val storyEventsInScene = `lists the story events covered by the`(scene)
        return StoryEventsInScene(
            storyEventsInScene.sceneId,
            storyEventsInScene.filter { item -> item.involvedCharacters.any { it.characterId == andInvolveThe.uuid } }
        )
    }

}