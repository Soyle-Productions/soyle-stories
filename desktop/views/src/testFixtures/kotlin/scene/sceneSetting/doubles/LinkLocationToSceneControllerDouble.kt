package com.soyle.stories.desktop.view.scene.sceneSetting.doubles

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController

class LinkLocationToSceneControllerDouble(
    private val onLinkLocationToScene: (Scene.Id, Location.Id) -> Unit = { _, _, -> }
) : LinkLocationToSceneController {

    override fun linkLocationToScene(sceneId: Scene.Id, locationId: Location.Id) {
        onLinkLocationToScene(sceneId, locationId)
    }
}