package com.soyle.stories.scene.locationsInScene.replace

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.location.replace.ReplaceSettingInScene
import kotlinx.coroutines.Job

interface ReplaceSettingInSceneController {
    companion object {
        operator fun invoke(
            threadTransformer: ThreadTransformer,
            replaceSettingInScene: ReplaceSettingInScene,
            replaceSettingInSceneOutput: ReplaceSettingInScene.OutputPort
        ): ReplaceSettingInSceneController = object : ReplaceSettingInSceneController {
            override fun replaceSettingInScene(
                sceneId: Scene.Id,
                settingId: Location.Id,
                replacementId: Location.Id
            ): Job {
                val request = ReplaceSettingInScene.RequestModel(
                    sceneId,
                    settingId,
                    replacementId
                )
                return threadTransformer.async {
                    replaceSettingInScene.invoke(request, replaceSettingInSceneOutput)
                }
            }
        }
    }

    fun replaceSettingInScene(sceneId: Scene.Id, settingId: Location.Id, replacementId: Location.Id): Job
}