package com.soyle.stories.domain.location.events

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene

data class HostedSceneRenamed(val locationId: Location.Id, val sceneId: Scene.Id, val newName: String)