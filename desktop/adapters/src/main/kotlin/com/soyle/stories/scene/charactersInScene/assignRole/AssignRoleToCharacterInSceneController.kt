package com.soyle.stories.scene.charactersInScene.assignRole

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.RoleInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInScene
import kotlinx.coroutines.Job

class AssignRoleToCharacterInSceneController(
    private val threadTransformer: ThreadTransformer,
    private val assignRoleToCharacterInScene: AssignRoleToCharacterInScene,
    private val assignRoleToCharacterInSceneOutput: AssignRoleToCharacterInScene.OutputPort
) {

    fun assignRole(sceneId: Scene.Id, characterId: Character.Id, role: RoleInScene): Job
    {
        val request = AssignRoleToCharacterInScene.RequestModel(
            sceneId, characterId, role
        )
        return assignRole(request)
    }

    fun clearRole(sceneId: Scene.Id, characterId: Character.Id): Job
    {
        val request = AssignRoleToCharacterInScene.RequestModel(
            sceneId, characterId, null
        )
        return assignRole(request)
    }

    private fun assignRole(request: AssignRoleToCharacterInScene.RequestModel): Job
    {
        return threadTransformer.async {
            assignRoleToCharacterInScene.invoke(request, assignRoleToCharacterInSceneOutput)
        }
    }

}