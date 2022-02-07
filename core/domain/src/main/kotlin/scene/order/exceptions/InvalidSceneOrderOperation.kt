package com.soyle.stories.domain.scene.order.exceptions

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.ValidationException

data class InvalidSceneOrderOperation(override val message: String) : ValidationException(), SceneOrderException

fun cannotAddSceneOutOfBounds(sceneId: Scene.Id, index: Int) =
    InvalidSceneOrderOperation("Cannot add scene $sceneId out of bounds.  Requested index: $index")

fun sceneIndexOutOfBounds(index: Int, validRange: IntRange) =
    InvalidSceneOrderOperation("$index is out of bounds of range $validRange")