package com.soyle.stories.domain.scene.order.exceptions

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.DuplicateOperationException

data class DuplicateSceneOrderOperation(override val message: String) : DuplicateOperationException(), SceneOrderException

internal fun sceneCannotBeAddedTwice(sceneId: Scene.Id) =
    DuplicateSceneOrderOperation("Scene cannot be added to Scene Order twice.  Scene: $sceneId")