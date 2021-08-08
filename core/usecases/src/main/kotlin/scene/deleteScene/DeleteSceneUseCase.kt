package com.soyle.stories.usecase.scene.deleteScene

import com.soyle.stories.domain.location.Updated
import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneRepository
import java.util.*

class DeleteSceneUseCase(
  private val sceneRepository: SceneRepository,
  private val locationRepository: LocationRepository
) : DeleteScene {
	override suspend fun invoke(sceneId: UUID, locale: SceneLocale, output: DeleteScene.OutputPort) {
		val response = try {
			val scene = sceneRepository.getSceneById(Scene.Id(sceneId)) ?: throw SceneDoesNotExist(locale, sceneId)
			val locations = locationRepository.getLocationsById(scene.settings.map(SceneSettingLocation::id).toSet())
			sceneRepository.removeScene(scene)
			val locationUpdates = locations.mapNotNull {
				it.withHostedScene(scene.id)?.removed()
			}
			locationRepository.updateLocations(locationUpdates.map { it.location }.toSet())
			DeleteScene.ResponseModel(sceneId, locationUpdates.filterIsInstance<Updated<HostedSceneRemoved>>().map { it.event })
		} catch (s: Exception) {
			return output.receiveDeleteSceneFailure(s)
		}
		output.receiveDeleteSceneResponse(response)
	}
}
