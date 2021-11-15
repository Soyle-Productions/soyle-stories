package com.soyle.stories.domain.scene.order.exceptions

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.ValidationException

data class InvalidSceneOrderOperation(override val message: String) : ValidationException(), SceneOrderException

internal fun cannotAddSceneOutOfBounds(sceneId: Scene.Id, index: Int) =
    InvalidSceneOrderOperation("Cannot add scene $sceneId out of bounds.  Requested index: $index")