package com.soyle.stories.scene.delete

import com.soyle.stories.domain.scene.events.SceneRemoved

interface SceneDeletedReceiver {

    suspend fun receiveSceneDeleted(event: SceneRemoved)

}
