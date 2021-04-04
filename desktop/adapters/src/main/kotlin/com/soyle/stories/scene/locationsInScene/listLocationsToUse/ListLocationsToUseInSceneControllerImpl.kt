package com.soyle.stories.scene.locationsInScene.listLocationsToUse

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.locationsInScene.listLocationsToUse.ListAvailableLocationsToUseInScene
import kotlinx.coroutines.Job

class ListLocationsToUseInSceneControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val listAvailableLocationsToUseInScene: ListAvailableLocationsToUseInScene
) : ListLocationsToUseInSceneController {
    override fun listLocationsToUse(sceneId: Scene.Id, output: ListAvailableLocationsToUseInScene.OutputPort): Job {
        return threadTransformer.async {
            listAvailableLocationsToUseInScene.invoke(sceneId, output)
        }
    }
}