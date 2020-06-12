package com.soyle.stories.scene.reorderSceneDialog

interface ReorderSceneDialogViewListener {

	fun getValidState(sceneId: String, sceneName: String, index: Int)
	fun reorderScene(sceneId: String, index: Int, showNextTime: Boolean)

}