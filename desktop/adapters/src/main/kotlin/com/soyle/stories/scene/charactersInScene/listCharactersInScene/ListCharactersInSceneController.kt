package com.soyle.stories.scene.charactersInScene.listCharactersInScene

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.getSceneDetails.GetSceneDetails
import kotlinx.coroutines.Job

class ListCharactersInSceneController(
    private val threadTransformer: ThreadTransformer,
    private val localeManager: LocaleManager,
    private val getSceneDetails: GetSceneDetails
) {

    fun listCharactersInScene(sceneId: Scene.Id, output: GetSceneDetails.OutputPort): Job
    {
        return threadTransformer.async {
            getSceneDetails.invoke(GetSceneDetails.RequestModel(sceneId.uuid, localeManager.getCurrentLocale()), output)
        }
    }

}