package com.soyle.stories.scene.items

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem

data class SceneItemViewModel(
    val id: String,
    val proseId: Prose.Id,
    val name: String,
    val index: Int,
    val invalidEntitiesMentioned: Boolean,
    val unusedSymbols: Boolean,
    val inconsistentSettings: Boolean
) {
    constructor(sceneItem: SceneItem, invalidEntitiesMentioned: Boolean = false, unusedSymbols: Boolean = false, inconsistentSettings: Boolean = false) : this(
        sceneItem.id.toString(),
        sceneItem.proseId,
        sceneItem.sceneName,
        sceneItem.index,
        invalidEntitiesMentioned,
        unusedSymbols,
        inconsistentSettings
    )

    val hasProblem: Boolean
        get() = invalidEntitiesMentioned || unusedSymbols || inconsistentSettings

    override fun toString(): String {
        return "SceneItemViewModel(id=$id, name=$name)"
    }
}