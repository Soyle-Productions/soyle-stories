package com.soyle.stories.scene.repositories

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene

interface SceneRepository {

	suspend fun createNewScene(scene: Scene)
	suspend fun listAllScenesInProject(projectId: Project.Id): List<Scene>

}