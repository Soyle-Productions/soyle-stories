package com.soyle.stories.usecase.scene.inconsistencies

import com.soyle.stories.domain.scene.Scene

class SceneInconsistencies(
    val sceneId: Scene.Id,
    inconsistencies: Set<SceneInconsistency>
) : Set<SceneInconsistencies.SceneInconsistency> by inconsistencies {

    sealed class SceneInconsistency {

        class SceneSettingInconsistency(settingInconsistencies: Set<SceneSettingLocationInconsistencies>) :
            Set<SceneSettingLocationInconsistencies> by settingInconsistencies, SceneInconsistency()

    }

}