package com.soyle.stories.usecase.scene.character.list

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.events.CharacterRemovedFromStory
import com.soyle.stories.domain.character.name.events.CharacterRenamed
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.character.events.*
import com.soyle.stories.domain.scene.events.SceneRemoved
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.character.changes.CharacterRemovedFromStoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.usecase.scene.character.involve.SourceAddedToCharacterInScene
import com.soyle.stories.usecase.scene.common.InheritedMotivation

data class CharacterInSceneItem(
    val characterId: Character.Id,
    val scene: Scene.Id,
    val project: Project.Id?,
    val characterName: String,
    val isExplicit: Boolean,
    val roleInScene: RoleInScene?,
    val sources: Set<CharacterInSceneSourceItem>,
) {

    val sourceIds by lazy { sources.map(CharacterInSceneSourceItem::storyEvent).toSet() }

    fun withEventApplied(event: CharacterRenamed): CharacterInSceneItem {
        if (characterId != event.characterId || characterName != event.oldName) return this
        return copy(characterName = event.name)
    }

    fun withEventApplied(event: CharacterRoleInSceneCleared): CharacterInSceneItem {
        if (event.sceneId != scene || event.characterId != characterId) return this
        return copy(roleInScene = null)
    }

    fun withEventApplied(event: CharacterAssignedRoleInScene): CharacterInSceneItem {
        if (event.sceneId != scene || event.characterId != characterId) return this
        return copy(roleInScene = event.newRole)
    }

    fun withEventApplied(event: SourceAddedToCharacterInScene): CharacterInSceneItem {
        if (event.scene != scene || event.character != characterId) return this
        return copy(sources = sources + event.source)
    }

}