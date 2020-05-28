package com.soyle.stories.scene.repositories

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene

interface SceneRepository {

	suspend fun createNewScene(scene: Scene, idOrder: List<Scene.Id>)
	suspend fun listAllScenesInProject(projectId: Project.Id): List<Scene>
	suspend fun getSceneIdsInOrder(projectId: Project.Id): List<Scene.Id>
	suspend fun updateSceneOrder(projectId: Project.Id, order: List<Scene.Id>)
	suspend fun getSceneById(sceneId: Scene.Id): Scene?
	suspend fun updateScene(scene: Scene)
	suspend fun removeScene(scene: Scene)

}