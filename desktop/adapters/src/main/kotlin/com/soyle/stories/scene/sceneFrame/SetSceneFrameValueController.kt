package com.soyle.stories.scene.sceneFrame

import com.soyle.stories.domain.scene.Scene

interface SetSceneFrameValueController {
    fun setSceneConflict(sceneId: Scene.Id, conflict: String)
    fun setSceneResolution(sceneId: Scene.Id, resolution: String)
}