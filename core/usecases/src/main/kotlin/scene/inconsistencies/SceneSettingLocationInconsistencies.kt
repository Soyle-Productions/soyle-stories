package com.soyle.stories.usecase.scene.inconsistencies

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene

class SceneSettingLocationInconsistencies(
    val sceneId: Scene.Id,
    val locationId: Location.Id,
    inconsistencies: Set<SceneSettingLocationInconsistency>
) : Set<SceneSettingLocationInconsistencies.SceneSettingLocationInconsistency> by inconsistencies {

    enum class SceneSettingLocationInconsistency {
        LocationRemovedFromStory
    }

}