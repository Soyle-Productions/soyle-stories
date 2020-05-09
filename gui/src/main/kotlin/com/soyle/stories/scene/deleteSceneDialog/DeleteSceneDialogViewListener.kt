package com.soyle.stories.scene.deleteSceneDialog

import com.soyle.stories.scene.items.SceneItemViewModel

interface DeleteSceneDialogViewListener {

	fun getValidState(sceneItemViewModel: SceneItemViewModel)
	fun deleteScene(sceneId: String)

}