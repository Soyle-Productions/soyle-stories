package com.soyle.stories.scene.deleteScene

import com.soyle.stories.domain.scene.Scene

interface SceneDeletedReceiver {

    suspend fun receiveSceneDeleted(event: Scene.Id)

}
