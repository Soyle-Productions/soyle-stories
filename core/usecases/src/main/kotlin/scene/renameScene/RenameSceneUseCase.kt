package com.soyle.stories.usecase.scene.renameScene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events
import com.soyle.stories.domain.location.locations
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneRepository

class RenameSceneUseCase(
  private val sceneRepository: SceneRepository,
  private val locationRepository: LocationRepository
) : RenameScene {
	override suspend fun invoke(request: RenameScene.RequestModel, output: RenameScene.OutputPort) {
		val response = try {
			renameScene(request)
		} catch (s: Exception) {
			return output.receiveRenameSceneFailure(s)
		}
		output.receiveRenameSceneResponse(response)
	}

	private suspend fun renameScene(request: RenameScene.RequestModel): RenameScene.ResponseModel {
		val scene = sceneRepository.getSceneOrError(request.sceneId)
		val sceneUpdate = scene.withName(request.name)
		return if (sceneUpdate is Successful) {

			val sceneSettings = scene.locationsInWhichItTakesPlace()

			val (newScene, sceneRenamed) = sceneUpdate

			val sceneSettingUpdates = sceneSettings.map {
				it.withHostedScene(scene.id)!!.renamed(to = request.name.value)
			}

			sceneRepository.updateScene(newScene)
			locationRepository.updateLocations(sceneSettingUpdates.locations().toSet())

			RenameScene.ResponseModel(scene.id, request.name.value, sceneRenamed, sceneSettingUpdates.events())
		} else return noUpdate(scene)
	}

	private suspend fun Scene.locationsInWhichItTakesPlace(): List<Location> =
		locationRepository.getLocationsById(settings.map(SceneSettingLocation::id).toSet())

	private fun noUpdate(scene: Scene) =
		RenameScene.ResponseModel(scene.id, scene.name.value, null, emptyList())

}
