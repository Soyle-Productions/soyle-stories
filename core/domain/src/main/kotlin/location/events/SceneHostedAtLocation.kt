package com.soyle.stories.domain.location.events

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene

data class SceneHostedAtLocation(val locationId: Location.Id, val sceneId: Scene.Id, val sceneName: String) {
}