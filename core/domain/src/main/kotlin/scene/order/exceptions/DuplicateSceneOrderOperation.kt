package com.soyle.stories.domain.scene.order.exceptions

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.DuplicateOperationException

data class DuplicateSceneOrderOperation(override val message: String) : DuplicateOperationException(), SceneOrderException

fun sceneCannotBeAddedTwice(sceneId: Scene.Id) =
    DuplicateSceneOrderOperation("Scene cannot be added to Scene Order twice.  Scene: $sceneId")

fun sceneAlreadyAtIndex(sceneId: Scene.Id, index: Int) =
    DuplicateSceneOrderOperation("$sceneId is already at index $index in scene order")