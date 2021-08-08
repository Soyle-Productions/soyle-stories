package com.soyle.stories.scene.renameScene

import com.soyle.stories.domain.scene.events.SceneRenamed

interface SceneRenamedReceiver {

    suspend fun receiveSceneRenamed(event: SceneRenamed)

}
