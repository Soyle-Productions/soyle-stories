package com.soyle.stories.usecase.scene.includedCharacter.assignRole

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.RoleInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.CharacterRoleInSceneChanged

interface AssignRoleToCharacterInScene {

    class RequestModel(
        val sceneId: Scene.Id,
        val characterId: Character.Id,
        val role: RoleInScene?
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(val characterRoleInSceneChanged: CharacterRoleInSceneChanged?)

    interface OutputPort {
        suspend fun roleAssignedToCharacterInScene(response: ResponseModel)
    }
}