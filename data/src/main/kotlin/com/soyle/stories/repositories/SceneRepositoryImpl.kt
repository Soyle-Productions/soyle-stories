package com.soyle.stories.repositories

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.scene.repositories.SceneRepository

class SceneRepositoryImpl : SceneRepository {

	private val scenes = mutableMapOf<Scene.Id, Scene>()
	private val sceneOrder = mutableMapOf<Project.Id, List<Scene.Id>>()

	override suspend fun createNewScene(scene: Scene, idOrder: List<Scene.Id>) {
		scenes[scene.id] = scene
		sceneOrder[scene.projectId] = idOrder
	}

	override suspend fun listAllScenesInProject(projectId: Project.Id): List<Scene> =
	  scenes.values.toList()

	override suspend fun getSceneIdsInOrder(projectId: Project.Id): List<Scene.Id> {
		return sceneOrder[projectId] ?: emptyList()
	}

	override suspend fun getSceneForStoryEvent(storyEventId: StoryEvent.Id): Scene? {
		return scenes.values.find { it.storyEventId == storyEventId }
	}

	override suspend fun updateSceneOrder(projectId: Project.Id, order: List<Scene.Id>) {
		sceneOrder[projectId] = order
	}

	override suspend fun getSceneById(sceneId: Scene.Id): Scene? =
	  scenes[sceneId]

	override suspend fun updateScene(scene: Scene) {
		scenes[scene.id] = scene
	}

	override suspend fun removeScene(scene: Scene) {
		scenes.remove(scene.id)
	}
}