package com.soyle.stories.scene.locationsInScene.listLocationsInScene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.locationsInScene.listLocationsUsed.ListLocationsUsedInScene
import kotlinx.coroutines.Job

class ListLocationsInSceneControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val listLocationsInScene: ListLocationsUsedInScene
) : ListLocationsInSceneController {

    override fun listLocationsInScene(sceneId: Scene.Id, output: ListLocationsUsedInScene.OutputPort): Job {
        return threadTransformer.async {
            listLocationsInScene.invoke(sceneId, output)
        }
    }

}