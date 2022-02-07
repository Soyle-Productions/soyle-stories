package com.soyle.stories.scene.items

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.list.ListAllScenes
import com.soyle.stories.usecase.scene.list.SceneItem

data class SceneItemViewModel(
    val id: Scene.Id,
    val proseId: Prose.Id,
    val name: String,
    val index: Int,
    val invalidEntitiesMentioned: Boolean,
    val unusedSymbols: Boolean,
    val inconsistentSettings: Boolean
) {
    constructor(
        sceneItem: ListAllScenes.SceneListItem,
        invalidEntitiesMentioned: Boolean = false,
        unusedSymbols: Boolean = false,
        inconsistentSettings: Boolean = false
    ) : this(
        sceneItem.scene,
        sceneItem.prose,
        sceneItem.name,
        0,
        invalidEntitiesMentioned,
        unusedSymbols,
        inconsistentSettings
    )

    private fun copy(
        index: Int = this.index
    ) = SceneItemViewModel(
        id,
        proseId,
        name,
        index, invalidEntitiesMentioned, unusedSymbols, inconsistentSettings
    )

    fun withIndex(index: Int) = copy(index = index)

    val hasProblem: Boolean
        get() = invalidEntitiesMentioned || unusedSymbols || inconsistentSettings

    override fun toString(): String {
        return "SceneItemViewModel(id=$id, name=$name)"
    }
}