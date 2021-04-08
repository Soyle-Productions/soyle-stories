package com.soyle.stories.usecase.scene.character.assignRole

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.*
import com.soyle.stories.domain.scene.events.CharacterRoleInSceneChanged
import com.soyle.stories.domain.scene.events.CompoundEvent
import com.soyle.stories.usecase.scene.SceneRepository

class AssignRoleToCharacterInSceneUseCase(
    private val sceneRepository: SceneRepository
) : AssignRoleToCharacterInScene {


    override suspend fun invoke(
        request: AssignRoleToCharacterInScene.RequestModel,
        output: AssignRoleToCharacterInScene.OutputPort
    ) {
        val scene = sceneRepository.getSceneOrError(request.sceneId.uuid)
        val changeEvent = updateSceneWithNewRole(scene, request.characterId, request.role)
        output.roleAssignedToCharacterInScene(AssignRoleToCharacterInScene.ResponseModel(changeEvent))
    }

    private suspend fun updateSceneWithNewRole(
        scene: Scene,
        characterId: Character.Id,
        role: RoleInScene?
    ): CompoundEvent<CharacterRoleInSceneChanged>? {
        val sceneUpdate = scene.withRoleForCharacter(characterId, role)
        if (sceneUpdate is Updated) commitUpdate(sceneUpdate)
        return (sceneUpdate as? Updated)?.event
    }

    private suspend fun commitUpdate(update: Updated<*>)
    {
        sceneRepository.updateScene(update.scene)
    }
}