package com.soyle.stories.scene.sceneList

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.Prose

interface SceneListViewListener {

	fun getValidState()
	fun editScene(sceneId: String, proseId: Prose.Id)
	fun renameScene(sceneId: String, newName: NonBlankString)
	fun openSceneDetails(sceneId: String)
	fun reorderScene(sceneId: String, newIndex: Int)


}