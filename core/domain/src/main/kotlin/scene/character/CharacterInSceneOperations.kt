package com.soyle.stories.domain.scene.character

import com.soyle.stories.domain.scene.SceneUpdate
import com.soyle.stories.domain.scene.character.events.CharacterRemovedFromScene
import com.soyle.stories.domain.scene.character.events.CharacterRoleInSceneChanged
import com.soyle.stories.domain.scene.character.events.CharacterDesireInSceneChanged
import com.soyle.stories.domain.scene.character.events.CharacterMotivationInSceneChanged
import com.soyle.stories.domain.scene.events.CompoundEvent
import com.soyle.stories.domain.scene.events.SceneEvent

interface CharacterInSceneOperations {

    fun assignedRole(role: RoleInScene?): SceneUpdate<CompoundEvent<CharacterRoleInSceneChanged>>

    fun desireChanged(desire: String): SceneUpdate<CharacterDesireInSceneChanged>

    fun motivationChanged(motivation: String?): SceneUpdate<CharacterMotivationInSceneChanged>

    fun removed(): SceneUpdate<CharacterRemovedFromScene>

}