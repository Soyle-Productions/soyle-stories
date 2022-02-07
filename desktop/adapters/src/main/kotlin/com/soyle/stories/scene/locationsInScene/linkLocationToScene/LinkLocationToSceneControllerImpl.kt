package com.soyle.stories.scene.locationsInScene.linkLocationToScene

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToScene

class LinkLocationToSceneControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val linkLocationToScene: LinkLocationToScene,
    private val linkLocationToSceneOutputPort: LinkLocationToScene.OutputPort
) : LinkLocationToSceneController {

    override fun linkLocationToScene(sceneId: Scene.Id, locationId: Location.Id) {
        threadTransformer.async {
            linkLocationToScene.invoke(
                LinkLocationToScene.RequestModel(
                    sceneId,
                    locationId,
                ),
                linkLocationToSceneOutputPort
            )
        }
    }

}