package com.soyle.stories.usecase.scene.location.detectInconsistencies

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.inconsistencies.SceneInconsistencies
import com.soyle.stories.usecase.scene.inconsistencies.SceneInconsistencies.SceneInconsistency.SceneSettingInconsistency
import com.soyle.stories.usecase.scene.inconsistencies.SceneSettingLocationInconsistencies
import com.soyle.stories.usecase.scene.inconsistencies.SceneSettingLocationInconsistencies.SceneSettingLocationInconsistency
import java.util.*
import kotlin.collections.LinkedHashSet

class DetectInconsistenciesInSceneSettingsUseCase(
    private val sceneRepository: SceneRepository,
    private val locationRepository: LocationRepository
) : DetectInconsistenciesInSceneSettings {

    override suspend fun invoke(sceneId: Scene.Id, output: DetectInconsistenciesInSceneSettings.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val sceneSettingLocationIds = scene.settings.mapTo(LinkedHashSet(scene.settings.size)) { it.id }
        val removedLocationIds = locationRepository.getLocationIdsThatDoNotExist(sceneSettingLocationIds)
        val inconsistencies = scene.settings.mapTo(LinkedHashSet(scene.settings.size)) {
            SceneSettingLocationInconsistencies(
                scene.id,
                it.id,
                if (it.id in removedLocationIds) setOf(SceneSettingLocationInconsistency.LocationRemovedFromStory)
                else emptySet()
            )
        }
        output.receiveSceneInconsistencyReport(
            SceneInconsistencies(sceneId, setOf(SceneSettingInconsistency(inconsistencies)))
        )
    }
}