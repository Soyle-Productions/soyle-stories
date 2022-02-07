package com.soyle.stories.scene.outline

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.common.scopedListener
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.FocusedSceneQueries
import com.soyle.stories.scene.outline.item.OutlinedStoryEventItemComponent
import com.soyle.stories.storyevent.coverage.StoryEventCoveredBySceneNotifier
import com.soyle.stories.storyevent.coverage.uncover.StoryEventUncoveredBySceneNotifier
import javafx.scene.Node
import javafx.scene.Parent
import tornadofx.UI_COMPONENT_PROPERTY
import tornadofx.View
import kotlin.coroutines.CoroutineContext

@Suppress("FunctionName")
interface SceneOutlineComponent {

    fun SceneOutline() : Node

    interface Gui : OutlinedStoryEventItemComponent

    companion object {
        fun Implementation(
            projectScope: ProjectScope,

            guiContext: CoroutineContext,

            outlineSceneController: OutlineSceneController,

            gui: Gui
        ): SceneOutlineComponent = object : SceneOutlineComponent, View() {

            private val viewModel by lazy { projectScope.get<SceneOutlineViewModel>() }
            override val root: Parent by lazy {
                SceneOutlineView(viewModel, gui).also {
                    it.properties[UI_COMPONENT_PROPERTY] = this
                }
            }

            override fun SceneOutline(): Node {
                return root.also { activateComponent() }
            }

            private fun activateComponent() {
                if (root.properties["com.soyle.stories.active"] == true) return
                val eventHandler = SceneOutlineEventHandler(guiContext, outlineSceneController, viewModel)
                root.sceneProperty().onChangeUntil({ scene -> scene == null }) { scene ->
                    if (scene != null) return@onChangeUntil
                    deactivateComponent(eventHandler)
                }
                val outlineSceneHandler = eventHandler::outlineScene
                eventHandler.scopedListener(projectScope.get<FocusedSceneQueries>().focusedScene()) { outlineSceneHandler(it) }
                projectScope.get<StoryEventCoveredBySceneNotifier>().addListener(eventHandler)
                projectScope.get<StoryEventUncoveredBySceneNotifier>().addListener(eventHandler)
                root.properties["com.soyle.stories.active"] = true
            }

            private fun deactivateComponent(eventHandler: SceneOutlineEventHandler) {
                projectScope.get<StoryEventCoveredBySceneNotifier>().removeListener(eventHandler)
                projectScope.get<StoryEventUncoveredBySceneNotifier>().removeListener(eventHandler)
                root.properties["com.soyle.stories.active"] = false
            }
        }
    }

}