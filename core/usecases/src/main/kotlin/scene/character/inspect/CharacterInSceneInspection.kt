package com.soyle.stories.usecase.scene.character.inspect

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.name.events.CharacterRenamed
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.character.events.*
import com.soyle.stories.domain.scene.order.SuccessfulSceneOrderUpdate
import com.soyle.stories.usecase.scene.character.involve.SourceAddedToCharacterInScene
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneItem
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneSourceItem
import com.soyle.stories.usecase.scene.common.InheritedMotivation

data class CharacterInSceneInspection(
    val characterItem: CharacterInSceneItem,
    val desire: String,

    private val sceneOrder: List<Scene.Id>,
    private val motivationSources: Map<Scene.Id, InheritedMotivation>
) {

    val scene get() = characterItem.scene
    val characterId: Character.Id get() = characterItem.characterId
    val project: Project.Id? get() = characterItem.project
    val characterName: String get() = characterItem.characterName
    val isExplicit: Boolean get() = characterItem.isExplicit
    val roleInScene: RoleInScene? get() = characterItem.roleInScene
    val sources: Set<CharacterInSceneSourceItem> get() = characterItem.sources

    val motivation: String? by lazy {
        motivationSources[scene]?.motivation
    }
    val inheritedMotivation: InheritedMotivation? by lazy {
        sceneOrder.takeWhile { it != scene }
            .asReversed()
            .asSequence()
            .mapNotNull { motivationSources[it] }
            .firstOrNull()
    }
    val otherMotivations: Collection<InheritedMotivation> get() = motivationSources.values

    fun withEventApplied(event: CharacterRenamed): CharacterInSceneInspection {
        return copy(characterItem = characterItem.withEventApplied(event))
    }

    fun withEventApplied(event: CharacterRoleInSceneCleared): CharacterInSceneInspection {
        return copy(characterItem = characterItem.withEventApplied(event))
    }

    fun withEventApplied(event: CharacterAssignedRoleInScene): CharacterInSceneInspection {
        return copy(characterItem = characterItem.withEventApplied(event))
    }

    fun withEventApplied(event: SourceAddedToCharacterInScene): CharacterInSceneInspection {
        return copy(characterItem = characterItem.withEventApplied(event))
    }

    fun withEventApplied(event: CharacterDesireInSceneChanged): CharacterInSceneInspection {
        if (event.sceneId != scene || event.characterId != characterId) return this
        return copy(desire = event.newDesire)
    }

    fun withEventApplied(event: CharacterMotivationInSceneCleared): CharacterInSceneInspection {
        if (event.characterId != characterId) return this
        return copy(
            motivationSources = motivationSources - event.sceneId
        )
    }

    fun withEventApplied(event: CharacterGainedMotivationInScene): CharacterInSceneInspection {
        if (event.characterId != characterId) return this
        return copy(
            motivationSources = motivationSources + (event.sceneId to InheritedMotivation(
                event.sceneId,
                event.characterId,
                "",//event.sceneName,
                event.newMotivation
            ))
        )
    }

    fun withEventApplied(event: CharacterRemovedFromScene): CharacterInSceneInspection {
        if (event.characterId != characterId) return this
        return copy(
            motivationSources = motivationSources - event.sceneId
        )
    }

    fun withEventApplied(event: SuccessfulSceneOrderUpdate<*>): CharacterInSceneInspection {
        return copy(sceneOrder = event.sceneOrder.order.toList())
    }

}