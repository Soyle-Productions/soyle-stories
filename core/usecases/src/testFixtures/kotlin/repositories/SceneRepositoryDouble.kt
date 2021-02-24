package com.soyle.stories.usecase.repositories

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.scene.SceneRepository

class SceneRepositoryDouble(
	initialScenes: List<Scene> = emptyList(),

	private val onAddNewScene: (Scene) -> Unit = {},
	private val onUpdateSceneOrder: (Project.Id, List<Scene.Id>) -> Unit = { _, _ -> },
	private val onUpdateScene: (Scene) -> Unit = {},
	private val onRemoveScene: (Scene) -> Unit = {}
) : SceneRepository {

	val scenes = initialScenes.associateBy { it.id }.toMutableMap()
	val sceneOrder = initialScenes.groupBy { it.projectId }.mapValues { it.value.map(Scene::id) }.toMutableMap()

	fun givenScene(scene: Scene)
	{
		scenes[scene.id] = scene
		sceneOrder[scene.projectId] = sceneOrder[scene.projectId]?.plus(scene.id) ?: listOf(scene.id)
	}

	override suspend fun createNewScene(scene: Scene, idOrder: List<Scene.Id>) {
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
		onUpdateSceneOrder.invoke(projectId, order)
		sceneOrder[projectId] = order
	}

	override suspend fun getSceneById(sceneId: Scene.Id): Scene? =
	  scenes[sceneId]

	override suspend fun getSceneForStoryEvent(storyEventId: StoryEvent.Id): Scene? {
		return scenes.values.find { it.storyEventId == storyEventId }
	}

	override suspend fun getSceneThatOwnsProse(proseId: Prose.Id): Scene? {
		return scenes.values.find { it.proseId == proseId }
	}

	override suspend fun updateScene(scene: Scene) {
		onUpdateScene.invoke(scene)
		scenes[scene.id] = scene
	}

	override suspend fun updateScenes(scenes: List<Scene>) {
		scenes.forEach { updateScene(it) }
	}

	override suspend fun removeScene(scene: Scene) {
		scenes.remove(scene.id)
		onRemoveScene.invoke(scene)
	}

	override suspend fun getScenesTrackingSymbol(symbolId: Symbol.Id): List<Scene> {
		return scenes.values.filter { it.trackedSymbols.isSymbolTracked(symbolId) }
	}

	override suspend fun getScenesUsingLocation(locationId: Location.Id): List<Scene> {
		return scenes.values.filter { it.settings.containsEntityWithId(locationId) }
	}

}