package com.soyle.stories.scene.repositories

import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.SceneDoesNotExist
import java.util.*

suspend fun SceneRepository.getSceneOrError(sceneId: UUID) = getSceneById(Scene.Id(sceneId))
    ?: throw SceneDoesNotExist(sceneId)