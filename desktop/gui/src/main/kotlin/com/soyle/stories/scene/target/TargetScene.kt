package com.soyle.stories.scene.target

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene

interface TargetScene {

    companion object {
        operator fun invoke(
            threadTransformer: ThreadTransformer,
            sceneTargetedReceiver: SceneTargetedReceiver
        ): TargetScene = object : TargetScene {
            override fun invoke(sceneId: Scene.Id, proseId: Prose.Id, sceneName: String) {
                threadTransformer.gui {
                    sceneTargetedReceiver.receiveSceneTargeted(SceneTargeted(sceneId, proseId, sceneName))
                }
            }
        }
    }

    operator fun invoke(sceneId: Scene.Id, proseId: Prose.Id, sceneName: String)

}