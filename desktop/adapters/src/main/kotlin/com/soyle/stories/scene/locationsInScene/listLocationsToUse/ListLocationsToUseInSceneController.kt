package com.soyle.stories.scene.locationsInScene.listLocationsToUse

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.locationsInScene.listLocationsToUse.ListAvailableLocationsToUseInScene
import kotlinx.coroutines.Job

interface ListLocationsToUseInSceneController {

    fun listLocationsToUse(sceneId: Scene.Id, output: ListAvailableLocationsToUseInScene.OutputPort): Job

}