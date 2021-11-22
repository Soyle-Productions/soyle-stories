package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.common.components.ComponentsStyles.Companion.cardBody
import com.soyle.stories.common.components.layouts.emptyToolInvitation
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.SceneStyles
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneListModel
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView.Styles.Companion.symbolsInScene
import com.soyle.stories.scene.target.SceneTargeted
import com.soyle.stories.scene.target.SceneTargetedNotifier
import com.soyle.stories.scene.target.SceneTargetedReceiver
import com.soyle.stories.soylestories.Styles.Companion.Orange
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ContentDisplay
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.withContext
import tornadofx.*
import java.util.*

class SymbolsInSceneView : View() {

    private val state = resolve<SymbolsInSceneState>()
    private val viewListener = resolve<SymbolsInSceneViewListener>()

    override val root: Parent = vbox {
        addClass(symbolsInScene)
        targetSceneHeader().apply {
            viewOrder = 0.0
        }
        body().apply {
            vgrow = Priority.ALWAYS
            viewOrder = 1.0
        }
    }

    private fun Parent.targetSceneHeader(): Node
    {
        return hbox {
            addClass(SceneStyles.selectedSceneHeader)
            label("Scene: ")
            label(state.targetScene.stringBinding { it?.third ?: "No Scene Targeted" }) {
                toggleClass(TextStyles.warning, state.targetScene.isNull)
            }
        }
    }

    private fun Parent.body(): Node {
        return stackpane {
            dynamicContent(state.targetScene) {
                determineBodyContent(it?.first).apply {
                    vgrow = Priority.ALWAYS
                }
            }
        }
    }

    private fun Parent.determineBodyContent(targetScene: Scene.Id?): Node
    {
        return when (targetScene) {
            null -> emptyBody()
            else -> scope.get<SymbolTracker>().also { add(it) }.root
        }
    }

    private fun Parent.emptyBody(): Node
    {
        return emptyToolInvitation {
            imageview("com/soyle/stories/scene/Symbols-design.png") {
                this.isPreserveRatio = true
                this.isSmooth = true
                fitHeight = 260.0
            }
            toolTitle("Track Symbols in Scene")
            textflow {
                textAlignment = TextAlignment.CENTER

                text("No scene has been targeted to track symbols.  Click on a scene in the")
                hyperlink("Scene List") {
                    style { fontWeight = FontWeight.BOLD }
                    action { viewListener.openSceneListTool() }
                }
                text("to click anywhere in side of an open Scene Editor to ")
                label("target") { addClass(TextStyles.warning) }
                text(" a scene and see what symbols are being tracked.")
            }
        }
    }
    private fun targetSceneItem(sceneId: Scene.Id, proseId: Prose.Id, sceneName: String) {
        if (state.targetScene.value?.first != sceneId) {
            viewListener.getSymbolsInScene(sceneId)
        }
        state.targetScene.value = Triple(sceneId, proseId, sceneName)
    }

    private val guiEventListener = object :
        SceneTargetedReceiver
    {
        override suspend fun receiveSceneTargeted(event: SceneTargeted) {
            if (! Platform.isFxApplicationThread()) {
                withContext(Dispatchers.JavaFx) {
                    targetSceneItem(event.sceneId, event.proseId, event.sceneName)
                }
            } else {
                targetSceneItem(event.sceneId, event.proseId, event.sceneName)
            }
        }
    }

    init {
        (scope as ProjectScope).get<SceneTargetedNotifier>().addListener(guiEventListener)
        (FX.getComponents(scope)[SceneListModel::class] as? SceneListModel)?.let {
            it.selectedItem.value?.let { targetSceneItem(it.id, it.proseId, it.name) }
        }
    }

    class Styles : Stylesheet()
    {

        companion object {

            val symbolsInScene by cssclass()
            val symbolTracker by cssclass()
            val hasSymbols by csspseudoclass()

            val trackedSymbolChip by cssclass()
            val pinned by csspseudoclass()
            val unused by csspseudoclass()

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            symbolsInScene {
                fillWidth = true
            }
            symbolTracker and hasSymbols {
                padding = box(16.px)
                backgroundColor = multi(Color.WHITE)
            }
            trackedSymbolChip {
                padding = box(5.px, 6.px)
                borderRadius = multi(box(12.px))
                backgroundRadius = multi(box(16.px))
                backgroundColor = multi(Color.LAVENDER)
                contentDisplay = ContentDisplay.RIGHT

                button {
                    padding = box(0.px)
                    backgroundColor = multi(Color.TRANSPARENT)
                    contentDisplay = ContentDisplay.GRAPHIC_ONLY

                    imageView {
                        translateX = 2.px
                        translateY = (-2).px
                    }
                }
            }
            trackedSymbolChip and unused {
                backgroundColor = multi(Color.TRANSPARENT)
                borderColor = multi(box(Orange))
                borderWidth += box(2.px)
            }
        }

    }

}