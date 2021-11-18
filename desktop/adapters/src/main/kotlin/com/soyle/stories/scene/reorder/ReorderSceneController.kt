package com.soyle.stories.scene.reorder

import com.soyle.stories.domain.scene.Scene
import kotlinx.coroutines.Job

interface ReorderSceneController {
	fun reorderScene(sceneId: Scene.Id, newIndex: Int): Job
}