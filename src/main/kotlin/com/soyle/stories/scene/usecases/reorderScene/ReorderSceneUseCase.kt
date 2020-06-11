package com.soyle.stories.scene.usecases.reorderScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.usecases.listAllScenes.SceneItem

class ReorderSceneUseCase(
  private val sceneRepository: SceneRepository
) : ReorderScene {

	override suspend fun invoke(request: ReorderScene.RequestModel, output: ReorderScene.OutputPort) {
		val response = try {
			execute(request)
		} catch (e: Exception) {
			return output.failedToReorderScene(e)
		}
		output.sceneReordered(response)
	}

	private suspend fun execute(request: ReorderScene.RequestModel): ReorderScene.ResponseModel {
		val (scene, orderedSceneIds) = getScenes(request)
		val currentIndex = getSceneIndex(scene, orderedSceneIds)
		val newIndex = sanitizeIndex(request.newIndex, currentIndex, orderedSceneIds)
		val newOrder = updateSceneOrderIfNeeded(newIndex, currentIndex, orderedSceneIds, scene)
		return createResponse(scene, newIndex, currentIndex, getScenesWithChangedIndices(orderedSceneIds, newOrder))
	}

	private suspend fun getScenes(request: ReorderScene.RequestModel): Pair<Scene, List<Scene.Id>>
	{
		val scene = getScene(request)
		val orderedSceneIds = sceneRepository.getSceneIdsInOrder(scene.projectId)
		return scene to orderedSceneIds
	}

	private fun getSceneIndex(scene: Scene, orderedSceneIds: List<Scene.Id>): Int
	{
		val currentIndex = orderedSceneIds.indexOfFirst { it == scene.id }
		if (currentIndex == -1) {
			throw Error("Scene exists but does not have an index in the project.")
		}
		return currentIndex
	}

	private fun sanitizeIndex(index: Int, currentIndex: Int, orderedSceneIds: List<Scene.Id>): Int
	{
		if (index < 0) throw IndexOutOfBoundsException(index)
		if (index > orderedSceneIds.size) throw IndexOutOfBoundsException(index)
		return if (index == currentIndex + 1) currentIndex else index
	}

	private suspend fun updateSceneOrderIfNeeded(sanitizedIndex: Int, currentIndex: Int, orderedSceneIds: List<Scene.Id>, scene: Scene): List<Scene.Id> {
		return if (sanitizedIndex != currentIndex) {
			updateSceneOrder(orderedSceneIds, sanitizedIndex, scene)
		} else orderedSceneIds
	}

	private suspend fun updateSceneOrder(orderedSceneIds: List<Scene.Id>, sanitizedIndex: Int, scene: Scene): List<Scene.Id> {
		val update = orderedSceneIds.toMutableList().let {
			it.add(sanitizedIndex, scene.id)
			it.withIndex().filter { (index, id) ->
				id != scene.id || index == sanitizedIndex
			}.map { it.value }
		}
		sceneRepository.updateSceneOrder(scene.projectId, update)
		return update
	}

	private fun getScenesWithChangedIndices(originalOrder: List<Scene.Id>, newOrder: List<Scene.Id>): List<IndexedValue<Scene.Id>>
	{
		val originalIndex = originalOrder.withIndex().associate { it.value to it.index }
		return newOrder.withIndex()
		  .filter { it.index != originalIndex.getValue(it.value) }
	}

	private suspend fun createResponse(scene: Scene, newIndex: Int, oldIndex: Int, updatedIds: List<IndexedValue<Scene.Id>>): ReorderScene.ResponseModel {
		return ReorderScene.ResponseModel(
		  SceneItem(scene.id.uuid, scene.name, newIndex),
		  oldIndex,
		  updatedIds.mapNotNull {
			  if (it.value == scene.id) return@mapNotNull null
			  SceneItem(it.value.uuid, sceneRepository.getSceneById(it.value)!!.name, it.index)
		  })
	}

	private suspend fun getScene(request: ReorderScene.RequestModel) =
	  (sceneRepository.getSceneById(Scene.Id(request.sceneId))
		?: throw SceneDoesNotExist(request.locale, request.sceneId))
}