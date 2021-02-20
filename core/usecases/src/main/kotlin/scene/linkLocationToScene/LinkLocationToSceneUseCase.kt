package com.soyle.stories.usecase.scene.linkLocationToScene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.location.LocationDoesNotExist
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.linkLocationToScene.LinkLocationToScene.*
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
		  (request.locationId != null && scene.settings.firstOrNull()?.id?.uuid != request.locationId) ||
		  (request.locationId == null && scene.settings.firstOrNull() != null)
		  ) {
			update(scene, request)
		}
	}

	private suspend fun update(scene: Scene, request: RequestModel)
	{
		val update = if (request.locationId != null) {
			val location = getLocation(request.locationId)
			scene.withLocationLinked(location)
		} else {
			scene.settings.fold(scene) { newScene, location -> newScene.withoutLocation(location.id) }
		}
		sceneRepository.updateScene(update)
	}

	private suspend fun getLocation(locationId: UUID) =
	  (locationRepository.getLocationById(Location.Id(locationId))
	  ?: throw LocationDoesNotExist(locationId))

}