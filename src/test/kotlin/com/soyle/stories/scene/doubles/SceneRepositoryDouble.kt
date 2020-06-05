package com.soyle.stories.scene.doubles

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.scene.repositories.SceneRepository

class SceneRepositoryDouble(
  initialScenes: List<Scene> = emptyList(),

  private val onAddNewScene: (Scene) -> Unit = {},
  private val onUpdateScene: (Scene) -> Unit = {},
  private val onRemoveScene: (Scene) -> Unit = {}
) : SceneRepository {

	val scenes = initialScenes.associateBy { it.id }.toMutableMap()
	val sceneOrder = initialScenes.groupBy { it.projectId }.mapValues { it.value.map(Scene::id) }.toMutableMap()

	private val _persistedItems = mutableListOf<PersistenceLog>()
	val persistedItems: List<PersistenceLog>
		get() = _persistedItems

	private fun log(data: Any) {
		val type = Thread.currentThread().stackTrace.find {
			it.methodName != "log" && it.methodName != "getStackTrace"
		}?.methodName!!
		_persistedItems += PersistenceLog(type, data)
	}

	override suspend fun createNewScene(scene: Scene, idOrder: List<Scene.Id>) {
		log(scene)
		onAddNewScene.invoke(scene)
		scenes[scene.id] = scene
		sceneOrder[scene.projectId] = idOrder
	}

	override suspend fun listAllScenesInProject(projectId: Project.Id): List<Scene> {
		return scenes.values.filter { it.projectId == projectId }
	}

	override suspend fun getSceneIdsInOrder(projectId: Project.Id): List<Scene.Id> {
		return sceneOrder[projectId] ?: emptyList()
	}

	override suspend fun updateSceneOrder(projectId: Project.Id, order: List<Scene.Id>) {
		sceneOrder[projectId] = order
	}

	override suspend fun getSceneById(sceneId: Scene.Id): Scene? =
	  scenes[sceneId]

	override suspend fun getSceneForStoryEvent(storyEventId: StoryEvent.Id): Scene? {
		return scenes.values.find { it.storyEventId == storyEventId }
	}

	override suspend fun updateScene(scene: Scene) {
		log(scene)
		onUpdateScene.invoke(scene)
		scenes[scene.id] = scene
	}

	override suspend fun removeScene(scene: Scene) {
		log(scene)
		scenes.remove(scene.id)
		onRemoveScene.invoke(scene)
	}
}