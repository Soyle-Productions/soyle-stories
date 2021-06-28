package com.soyle.stories.scene.reorderSceneRamifications

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.closeTool.CloseToolController
import com.soyle.stories.scene.reorderScene.ReorderSceneController
import com.soyle.stories.usecase.scene.getPotentialChangeFromReorderingScene.GetPotentialChangesFromReorderingScene
import java.util.*

class ReorderSceneRamificationsController(
    sceneId: String,
    private val toolId: String,
    private val newIndex: Int,
    private val threadTransformer: ThreadTransformer,
    private val getPotentialChangesFromReorderingScene: GetPotentialChangesFromReorderingScene,
    private val getPotentialChangesFromReorderingSceneOutputPort: GetPotentialChangesFromReorderingScene.OutputPort,
    private val reorderSceneController: ReorderSceneController,
    private val closeToolController: CloseToolController
) : ReorderSceneRamificationsViewListener {

    private val sceneId = UUID.fromString(sceneId)

    override fun getValidState() {
        threadTransformer.async {
            getPotentialChangesFromReorderingScene.invoke(
                sceneId, newIndex,
                getPotentialChangesFromReorderingSceneOutputPort
            )
        }
    }

    override fun reorderScene() {
        reorderSceneController.reorderScene(sceneId.toString(), newIndex)
        closeToolController.closeTool(toolId)
    }

    override fun cancel() {
        closeToolController.closeTool(toolId)
    }

}