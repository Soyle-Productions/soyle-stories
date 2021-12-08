package com.soyle.stories.domain.scene.character

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneUpdate
import com.soyle.stories.domain.scene.character.events.CharacterInSceneEvent
import com.soyle.stories.domain.scene.character.events.CharacterRoleInSceneChanged
import com.soyle.stories.domain.scene.character.events.CharacterInSceneRenamed
import com.soyle.stories.domain.scene.events.CharacterDesireInSceneChanged
import com.soyle.stories.domain.scene.events.CompoundEvent
import com.soyle.stories.domain.storyevent.StoryEvent

interface CharacterInSceneOperations {

    fun renamed(newName: String): SceneUpdate<CharacterInSceneRenamed>

    fun assignedRole(role: RoleInScene?): SceneUpdate<CompoundEvent<CharacterRoleInSceneChanged>>

    fun desireChanged(desire: String): SceneUpdate<CharacterDesireInSceneChanged>

    fun motivationChanged(motivation: String?): Scene

    @Deprecated(message = "Should not be called from application code.  Only use from domain service", level = DeprecationLevel.WARNING)
    fun withoutSource(storyEventId: StoryEvent.Id): SceneUpdate<CharacterInSceneEvent>

    fun removed(): Scene

}