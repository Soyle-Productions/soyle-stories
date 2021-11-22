package com.soyle.stories.scene.sceneList

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.items.SceneItemViewModel

interface SceneListViewListener {

	fun getValidState()
	fun editScene(sceneId: String, proseId: Prose.Id)
	fun renameScene(sceneId: String, newName: NonBlankString)
	fun trackCharacters(sceneItem: SceneItemViewModel)
	fun trackLocations(sceneItem: SceneItemViewModel)
	fun trackSymbols(sceneItem: SceneItemViewModel)
	fun outlineScene(sceneItem: SceneItemViewModel)
	fun reorderScene(sceneId: Scene.Id, newIndex: Int)


}