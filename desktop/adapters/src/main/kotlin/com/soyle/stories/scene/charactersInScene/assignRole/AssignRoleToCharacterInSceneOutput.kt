package com.soyle.stories.scene.charactersInScene.assignRole

import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInScene

class AssignRoleToCharacterInSceneOutput(
    private val receiver: CharacterRoleInSceneChangedReceiver
) : AssignRoleToCharacterInScene.OutputPort {
    override suspend fun roleAssignedToCharacterInScene(response: AssignRoleToCharacterInScene.ResponseModel) {
        response.characterRolesInSceneChanged?.let {
            receiver.receiveCharacterRolesInSceneChanged(it)
        }
    }
}