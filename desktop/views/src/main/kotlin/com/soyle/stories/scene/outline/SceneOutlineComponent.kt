package com.soyle.stories.scene.outline

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.sceneList.SceneListModel
import com.soyle.stories.scene.target.SceneTargeted
import com.soyle.stories.scene.target.SceneTargetedNotifier
import com.soyle.stories.scene.target.SceneTargetedReceiver
import javafx.scene.Node
import javafx.scene.Parent
import tornadofx.FX
import tornadofx.UI_COMPONENT_PROPERTY
import tornadofx.View
import java.util.*

@Suppress("FunctionName")
interface SceneOutlineComponent {

    fun SceneOutline() : Node

    companion object {
        fun Implementation(
            projectScope: ProjectScope,

            outlineSceneController: OutlineSceneController
        ): SceneOutlineComponent = object : SceneOutlineComponent, View() {

            private val viewModel by lazy { projectScope.get<SceneOutlineViewModel>() }
            override val root: Parent by lazy {
                SceneOutlineView(viewModel).also {
                    it.properties[UI_COMPONENT_PROPERTY] = this
                }
            }

            override fun SceneOutline(): Node {
                return root.also { activateComponent() }
            }

            private fun activateComponent() {
                root.sceneProperty().onChangeUntil({ scene -> scene == null }) { scene ->
                    if (scene == null) return@onChangeUntil
                    deactivateComponent()
                }
                projectScope.get<SceneTargetedNotifier>().addListener(sceneTargetedReceiver)
            }

            private fun deactivateComponent() {
                projectScope.get<SceneTargetedNotifier>().removeListener(sceneTargetedReceiver)
            }

            private val sceneTargetedReceiver = SceneTargetedReceiver {
                outlineSceneController.outlineScene(it.sceneId)
            }
        }
    }

}