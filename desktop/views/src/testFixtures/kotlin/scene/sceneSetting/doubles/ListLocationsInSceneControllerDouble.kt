package com.soyle.stories.desktop.view.scene.sceneSetting.doubles

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.locationsInScene.listLocationsInScene.ListLocationsInSceneController
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInScene
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job

class ListLocationsInSceneControllerDouble(
    var onListLocationsInScene: (Scene.Id, ListLocationsUsedInScene.OutputPort) -> Unit = { _, _ -> }
) : ListLocationsInSceneController {

    var job: CompletableJob = Job()

    override fun listLocationsInScene(sceneId: Scene.Id, output: ListLocationsUsedInScene.OutputPort): Job {
        job = Job()
        onListLocationsInScene(sceneId, output)
        return job
    }

}