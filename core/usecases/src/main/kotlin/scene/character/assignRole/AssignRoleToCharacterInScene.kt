package com.soyle.stories.usecase.scene.character.assignRole

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.domain.scene.character.events.CharacterRoleInSceneChanged
import com.soyle.stories.domain.scene.events.CompoundEvent

interface AssignRoleToCharacterInScene {

    class RequestModel(
        val sceneId: Scene.Id,
        val characterId: Character.Id,
        val role: RoleInScene?
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val characterIncludedInScene: CharacterIncludedInScene?,
        val characterRolesInSceneChanged: CompoundEvent<CharacterRoleInSceneChanged>?
    )

    fun interface OutputPort {
        suspend fun roleAssignedToCharacterInScene(response: ResponseModel)
    }
}