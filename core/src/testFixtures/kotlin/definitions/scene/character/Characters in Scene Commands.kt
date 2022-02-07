package com.soyle.stories.core.definitions.scene.character

import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocaleDouble
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInScene
import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInSceneUseCase
import com.soyle.stories.usecase.scene.character.include.IncludeCharacterInSceneUseCase
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromSceneUseCase
import com.soyle.stories.usecase.scene.character.setDesire.SetCharacterDesireInScene
import com.soyle.stories.usecase.scene.character.setDesire.SetCharacterDesireInSceneUseCase
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInScene
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneUseCase
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.runBlocking

class `Characters in Scene Commands`(
    private val sceneRepository: SceneRepository,
    private val storyEventRepository: StoryEventRepository,
    private val characterRepository: CharacterRepository
) : `Scene Character Steps`.When {

    override fun characterInScene(
        scene: Scene.Id,
        character: Character.Id
    ): `Scene Character Steps`.When.CharacterInSceneActions = object : `Scene Character Steps`.When.CharacterInSceneActions {

        override fun `is assigned to be the`(role: RoleInScene) {
            val request = AssignRoleToCharacterInScene.RequestModel(scene, character, role)
            val useCase = AssignRoleToCharacterInSceneUseCase(sceneRepository, storyEventRepository, characterRepository)
            runBlocking {
                useCase(request) {}
            }
        }

        override fun desires(desire: String) {
            val request = SetCharacterDesireInScene.RequestModel(scene, character, desire)
            val useCase = SetCharacterDesireInSceneUseCase(sceneRepository, storyEventRepository, characterRepository)
            runBlocking {
                useCase(request) {}
            }
        }

        override fun `is motivated by`(motivation: String) {
            val useCase = SetMotivationForCharacterInSceneUseCase(sceneRepository, storyEventRepository, characterRepository)
            runBlocking {
                useCase(SetMotivationForCharacterInScene.RequestModel(scene, character, motivation)) {}
            }
        }

    }

    override fun includeCharacterInScene(scene: Scene.Id, character: Character.Id) {
        val useCase = IncludeCharacterInSceneUseCase(sceneRepository, characterRepository)
        runBlocking {
            useCase(scene, character) {}
        }
    }

    override fun removeCharacterFromScene(scene: Scene.Id, character: Character.Id) {
        val useCase = RemoveCharacterFromSceneUseCase(sceneRepository)
        runBlocking { useCase(scene, character) {} }
    }

}