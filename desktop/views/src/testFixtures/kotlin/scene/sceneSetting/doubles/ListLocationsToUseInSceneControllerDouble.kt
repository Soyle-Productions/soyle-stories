package com.soyle.stories.desktop.view.scene.sceneSetting.doubles

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.locationsInScene.listLocationsToUse.ListLocationsToUseInSceneController
import com.soyle.stories.usecase.scene.location.listLocationsToUse.ListAvailableLocationsToUseInScene
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job

class ListLocationsToUseInSceneControllerDouble(
    var onListLocationsToUse: (Scene.Id, ListAvailableLocationsToUseInScene.OutputPort) -> Unit = { _, _ -> Unit},
    var job: CompletableJob = Job()
) : ListLocationsToUseInSceneController {

    override fun listLocationsToUse(sceneId: Scene.Id, output: ListAvailableLocationsToUseInScene.OutputPort): Job {
        onListLocationsToUse(sceneId, output)
        return job
    }
}