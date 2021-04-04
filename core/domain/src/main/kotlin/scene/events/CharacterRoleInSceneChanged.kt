package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.RoleInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneFrameValue

sealed class CharacterRoleInSceneChanged : SceneEvent() {
    abstract val newRole: RoleInScene?
}

data class CharacterAssignedRoleInScene(override val sceneId: Scene.Id, override val newRole: RoleInScene) : CharacterRoleInSceneChanged()
data class CharacterRoleInSceneCleared(override val sceneId: Scene.Id) : CharacterRoleInSceneChanged() {
    /**
     * always null
     */
    override val newRole: RoleInScene?
        get() = null
}