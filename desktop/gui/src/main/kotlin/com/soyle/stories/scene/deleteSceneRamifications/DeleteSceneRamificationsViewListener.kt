package com.soyle.stories.scene.deleteSceneRamifications

import com.soyle.stories.domain.scene.Scene

interface DeleteSceneRamificationsViewListener {

	fun getValidState()
	fun deleteScene(sceneId: Scene.Id)
	fun cancel()

}