package com.soyle.stories.usecase.exceptions.scene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows

fun assertThrowsSceneDoesNotExist(sceneId: Scene.Id, test: () -> Unit) {
    val error = assertThrows<SceneDoesNotExist> {
        test()
    }
    assertEquals(sceneId.uuid, error.sceneId) { "Scene Does Not Exist error has wrong scene id" }
}