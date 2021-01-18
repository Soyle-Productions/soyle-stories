package com.soyle.stories.scene.items

import com.soyle.stories.entities.Prose
import com.soyle.stories.scene.usecases.listAllScenes.SceneItem

data class SceneItemViewModel(
    val id: String,
    val proseId: Prose.Id,
    val name: String,
    val index: Int,
    val hasProblem: Boolean,
) {
    constructor(sceneItem: SceneItem, hasProblem: Boolean = false) : this(
        sceneItem.id.toString(),
        sceneItem.proseId,
        sceneItem.sceneName,
        sceneItem.index,
        hasProblem
    )

    override fun toString(): String {
        return "SceneItemViewModel(id=$id, name=$name)"
    }
}