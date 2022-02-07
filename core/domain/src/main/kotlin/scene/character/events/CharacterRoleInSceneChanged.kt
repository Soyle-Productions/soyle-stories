package com.soyle.stories.domain.scene.character.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.Scene

sealed class CharacterRoleInSceneChanged : CharacterInSceneEvent() {
    abstract val newRole: RoleInScene?
}

data class CharacterAssignedRoleInScene(
    override val sceneId: Scene.Id,
    override val characterId: Character.Id,
    override val newRole: RoleInScene
) : CharacterRoleInSceneChanged()

data class CharacterRoleInSceneCleared(
    override val sceneId: Scene.Id,
    override val characterId: Character.Id
) : CharacterRoleInSceneChanged() {

    /**
     * always `null`
     */
    override val newRole: RoleInScene?
        get() = null
}