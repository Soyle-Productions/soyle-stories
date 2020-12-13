package com.soyle.stories.scene.items

import com.soyle.stories.entities.Prose
import com.soyle.stories.scene.usecases.listAllScenes.SceneItem

class SceneItemViewModel(val id: String, val proseId: Prose.Id, val name: String, val index: Int) {
	constructor(sceneItem: SceneItem) : this(sceneItem.id.toString(), sceneItem.proseId, sceneItem.sceneName, sceneItem.index)

	override fun toString(): String {
		return "SceneItemViewModel(id=$id, name=$name)"
	}
}