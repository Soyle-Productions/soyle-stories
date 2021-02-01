package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.common.components.ComponentsStyles.Companion.cardBody
import com.soyle.stories.common.components.Styles.Companion.fieldLabel
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.SceneTargeted
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView.Styles.Companion.symbolsInScene
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView.Styles.Companion.warningLabel
import com.soyle.stories.soylestories.Styles.Companion.Orange
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
            style {
                padding = box(16.px)
                backgroundColor = multi(Color.WHITE)
                effect = DropShadow(BlurType.GAUSSIAN, Color.rgb(0,0,0,0.25), 4.0, 0.0, 0.0, 4.0)

            }
            label("Scene: ")
            label(state.targetScene.stringBinding { it?.name ?: "No Scene Targeted" }) {
                toggleClass(warningLabel, state.targetScene.isNull)
            }
        }
    }

    private fun Parent.body(): Node {
        return stackpane {
            dynamicContent(state.targetScene) {
                determineBodyContent(it).apply {
                    vgrow = Priority.ALWAYS
                }
            }
        }
    }

    private fun Parent.determineBodyContent(targetScene: SceneItemViewModel?): Node
    {
        return when (targetScene) {
            null -> emptyBody()
            else -> scope.get<SymbolTracker>().also { add(it) }.root
        }
    }

    private fun Parent.emptyBody(): Node
    {
        return vbox(spacing = 16) {
            alignment = Pos.CENTER
            addClass(cardBody)
            imageview("com/soyle/stories/scene/Symbols-design.png") {
                this.isPreserveRatio = true
                this.isSmooth = true
                fitHeight = 260.0
            }
            label("Track Symbols in Scene") { addClass(fieldLabel) }
            textflow {
                textAlignment = TextAlignment.CENTER

                text("No scene has been targeted to track symbols.  Click on a scene in the")
                hyperlink("Scene List") {
                    style { fontWeight = FontWeight.BOLD }
                    action { viewListener.openSceneListTool() }
                }
                text("to click anywhere in side of an open Scene Editor to ")
                label("target") { addClass(warningLabel) }
                text(" a scene and see what symbols are being tracked.")
            }
        }
    }

    init {
        subscribe<SceneTargeted> {
            if (state.targetScene.value?.id != it.sceneItem.id) {
                viewListener.getSymbolsInScene(Scene.Id(UUID.fromString(it.sceneItem.id)))
            }
            state.targetScene.value = it.sceneItem
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

            val warningLabel by cssclass()

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
            warningLabel {
                textFill = Orange
                fontWeight = FontWeight.BOLD
            }
        }

    }

}