package com.soyle.stories.scene.locationsInScene.listLocationsInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInScene
import kotlinx.coroutines.Job

interface ListLocationsInSceneController {

    fun listLocationsInScene(sceneId: Scene.Id, output: ListLocationsUsedInScene.OutputPort): Job

}