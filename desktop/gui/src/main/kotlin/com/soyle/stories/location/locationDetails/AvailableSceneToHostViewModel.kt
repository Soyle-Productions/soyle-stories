package com.soyle.stories.location.locationDetails

import com.soyle.stories.domain.scene.Scene

data class AvailableSceneToHostViewModel(
    val sceneId: Scene.Id,
    val sceneName: String
)
