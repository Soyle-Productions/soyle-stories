package com.soyle.stories.scene.sceneList

interface SceneListViewListener {

	fun getValidState()
	fun renameScene(sceneId: String, newName: String)
	fun openSceneDetails(sceneId: String)


}