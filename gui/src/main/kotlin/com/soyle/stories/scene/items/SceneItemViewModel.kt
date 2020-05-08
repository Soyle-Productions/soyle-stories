package com.soyle.stories.scene.items

import com.soyle.stories.scene.usecases.listAllScenes.SceneItem

class SceneItemViewModel(val id: String, val name: String) {
	constructor(sceneItem: SceneItem) : this(sceneItem.id.toString(), sceneItem.sceneName)

	override fun toString(): String {
		return "SceneItemViewModel(id=$id, name=$name)"
	}
}