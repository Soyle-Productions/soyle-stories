package com.soyle.stories.scene.repositories

import com.soyle.stories.entities.Scene

interface SceneRepository {

	suspend fun createNewScene(scene: Scene)

}