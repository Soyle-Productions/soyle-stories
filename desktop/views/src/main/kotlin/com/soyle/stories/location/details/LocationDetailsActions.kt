package com.soyle.stories.location.details

import com.soyle.stories.domain.scene.Scene

interface LocationDetailsActions {

    fun reDescribeLocation(description: String)
    fun loadAvailableScenes()
    fun hostScene(sceneId: Scene.Id)
    fun createSceneToHost()
    fun removeScene(sceneId: Scene.Id)

}