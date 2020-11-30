package com.soyle.stories.scene.sceneList

import com.soyle.stories.common.NonBlankString

interface SceneListViewListener {

	fun getValidState()
	fun renameScene(sceneId: String, newName: NonBlankString)
	fun openSceneDetails(sceneId: String)
	fun reorderScene(sceneId: String, newIndex: Int)


}