package com.soyle.stories.scene.charactersInScene.assignRole

import com.soyle.stories.usecase.scene.includedCharacter.assignRole.AssignRoleToCharacterInScene

class AssignRoleToCharacterInSceneOutput(
    private val receiver: CharacterRoleInSceneChangedReceiver
) : AssignRoleToCharacterInScene.OutputPort {
    override suspend fun roleAssignedToCharacterInScene(response: AssignRoleToCharacterInScene.ResponseModel) {
        response.characterRoleInSceneChanged?.let {
            receiver.receiveCharacterRoleInSceneChanged(it)
        }
    }
}