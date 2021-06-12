package com.soyle.stories.domain.location.exceptions

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.DuplicateOperationException

data class LocationAlreadyHostsScene(override val locationId: Location.Id, val sceneId: Scene.Id) : DuplicateOperationException(), LocationException