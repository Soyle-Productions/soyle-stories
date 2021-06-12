package com.soyle.stories.desktop.view.location.details

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.details.LocationDetailsActions

class UserActionsMock(
    private val onReDescribeLocation: (String) -> Unit = {},
    private val onLoadAvailableScenes: () -> Unit = {},
    private val onHostScene: (Map<String, Any?>) -> Unit = {},
    private val onCreateSceneToHost: () -> Unit = {},
    private val onRemoveScene: (Map<String, Any?>) -> Unit = {}
) : LocationDetailsActions {

    override fun reDescribeLocation(description: String) = onReDescribeLocation(description)
    override fun loadAvailableScenes() = onLoadAvailableScenes()
    override fun hostScene(sceneId: Scene.Id) = onHostScene(mapOf("sceneId" to sceneId))
    override fun createSceneToHost() = onCreateSceneToHost()
    override fun removeScene(sceneId: Scene.Id) = onRemoveScene(mapOf("sceneId" to sceneId))
}