package com.soyle.stories.usecase.repositories

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.scene.order.SceneOrderUpdate
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.scene.SceneRepository

class SceneRepositoryDouble(
	initialScenes: List<Scene> = emptyList(),

	private val onAddNewScene: (Scene) -> Unit = {},
	private val onUpdateSceneOrder: (SceneOrder) -> Unit = {},
	private val onUpdateScene: (Scene) -> Unit = {},
	private val onRemoveScene: (Scene.Id) -> Unit = {}
) : SceneRepository {

	val scenes = initialScenes.associateBy { it.id }.toMutableMap()
	val sceneOrders = initialScenes.groupBy { it.projectId }.mapValues { SceneOrder.reInstantiate(it.key, it.value.map(Scene::id)) }.toMutableMap()

	fun givenScene(scene: Scene)
	{
		scenes[scene.id] = scene
		val sceneCreated = SceneCreated(scene.id, scene.name.value, scene.proseId, scene.storyEventId)
		sceneOrders[scene.projectId] = (sceneOrders[scene.projectId] ?: SceneOrder.initializeInProject(scene.projectId))
			.withScene(sceneCreated).sceneOrder
	}

	override suspend fun createNewScene(scene: Scene) {
		onAddNewScene.invoke(scene)
		scenes[scene.id] = scene
	}

	override suspend fun listAllScenesInProject(projectId: Project.Id, exclude: Set<Scene.Id>): List<Scene> {
		return scenes.values.filter { it.projectId == projectId && it.id !in exclude }
	}

	override suspend fun getSceneIdsInOrder(projectId: Project.Id): SceneOrder? {
		return sceneOrders[projectId]
	}

	override suspend fun updateSceneOrder(sceneOrder: SceneOrder) {
		onUpdateSceneOrder.invoke(sceneOrder)
		sceneOrders[sceneOrder.projectId] = sceneOrder
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

	override suspend fun removeScene(sceneId: Scene.Id) {
		scenes.remove(sceneId)
		onRemoveScene.invoke(sceneId)
	}

	override suspend fun getScenesTrackingSymbol(symbolId: Symbol.Id): List<Scene> {
		return scenes.values.filter { it.trackedSymbols.isSymbolTracked(symbolId) }
	}

	override suspend fun getScenesUsingLocation(locationId: Location.Id): List<Scene> {
		return scenes.values.filter { it.settings.containsEntityWithId(locationId) }
	}

	override suspend fun getScenesIncludingCharacter(characterId: Character.Id): List<Scene> {
		return scenes.values.filter { it.includesCharacter(characterId) }
	}

}