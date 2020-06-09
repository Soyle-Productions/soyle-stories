package com.soyle.stories.scene.usecases.linkLocationToScene

import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Scene
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.usecases.linkLocationToScene.LinkLocationToScene.*
import java.util.*

class LinkLocationToSceneUseCase(
  private val sceneRepository: SceneRepository,
  private val locationRepository: LocationRepository
) : LinkLocationToScene {
	override suspend fun invoke(request: RequestModel, output: OutputPort) {
		val response = try { execute(request) }
		catch (e: Exception) { return output.failedToLinkLocationToScene(e) }
		output.locationLinkedToScene(response)
	}

	private suspend fun execute(request: RequestModel): ResponseModel
	{
		val scene = getScene(request)
		updateIfNeeded(scene, request)
		return ResponseModel(request.sceneId, request.locationId)
	}

	private suspend fun getScene(request: RequestModel) =
	  (sceneRepository.getSceneById(Scene.Id(request.sceneId))
		?: throw SceneDoesNotExist(request.locale, request.sceneId))

	private suspend fun updateIfNeeded(scene: Scene, request: RequestModel)
	{
		if (
		  (request.locationId != null && scene.locationId?.uuid != request.locationId) ||
		  (request.locationId == null && scene.locationId != null)
		  ) {
			update(scene, request)
		}
	}

	private suspend fun update(scene: Scene, request: RequestModel)
	{
		val update = if (request.locationId != null) {
			val location = getLocation(request.locationId)
			scene.withLocationLinked(location.id)
		} else {
			scene.withoutLocation()
		}
		sceneRepository.updateScene(update)
	}

	private suspend fun getLocation(locationId: UUID) =
	  (locationRepository.getLocationById(Location.Id(locationId))
	  ?: throw LocationDoesNotExist(locationId))

}