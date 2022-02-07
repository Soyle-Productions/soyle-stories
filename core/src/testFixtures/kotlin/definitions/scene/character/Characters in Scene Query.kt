package com.soyle.stories.core.definitions.scene.character

import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.inspect.CharacterInSceneInspection
import com.soyle.stories.usecase.scene.character.inspect.InspectCharacterInSceneUseCase
import com.soyle.stories.usecase.scene.character.list.CharactersInScene
import com.soyle.stories.usecase.scene.character.list.ListCharactersInSceneUseCase
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.AvailableCharactersToAddToScene
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.ListAvailableCharactersToIncludeInSceneUseCase
import kotlinx.coroutines.runBlocking

class `Characters in Scene Query`(
    private val sceneRepository: SceneRepository,
    private val characterRepository: CharacterRepository,
    private val storyEventRepository: StoryEventRepository
) : `Scene Character Steps`.When.UserQueries {

    override fun `lists the characters in the`(scene: Scene.Id): CharactersInScene {
        val useCase = ListCharactersInSceneUseCase(sceneRepository, characterRepository, storyEventRepository)
        lateinit var characters: CharactersInScene
        runBlocking {
            useCase(scene) {
                characters = it
            }
        }
        return characters
    }

    override fun `lists the available characters to include in the`(scene: Scene.Id): AvailableCharactersToAddToScene {
        val useCase = ListAvailableCharactersToIncludeInSceneUseCase(sceneRepository, storyEventRepository, characterRepository)
        lateinit var availableCharacters: AvailableCharactersToAddToScene
        runBlocking {
            useCase(scene) {
                availableCharacters = it
            }
        }
        return availableCharacters
    }

    override fun `inspects the`(character: Character.Id, inThe: Scene.Id): CharacterInSceneInspection {
        val useCase = InspectCharacterInSceneUseCase(sceneRepository, characterRepository, storyEventRepository)
        lateinit var inspection: CharacterInSceneInspection
        runBlocking {
            useCase(inThe, character) {
                inspection = it.getOrThrow()
            }
        }
        return inspection
    }

}