package com.soyle.stories.scene.sceneSetting

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.SceneTargeted
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneListModel
import com.soyle.stories.scene.sceneSetting.SceneSettingView.Styles.Companion.sceneSetting
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView.Styles.Companion.warningLabel
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

class SceneSettingView : View() {

    private val state = resolve<SceneSettingState>()
    private val viewListener = resolve<SceneSettingViewListener>()

    override val root: Parent = vbox {
        addClass(sceneSetting)
        targetSceneHeader().apply {
            viewOrder = 0.0
        }
        body().apply {
            vgrow = Priority.ALWAYS
            viewOrder = 1.0
        }
    }

    private fun Parent.targetSceneHeader(): Node {
        return hbox {
            style {
                padding = box(16.px)
                backgroundColor = multi(Color.WHITE)
                effect = DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.25), 4.0, 0.0, 0.0, 4.0)

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

    private fun Parent.determineBodyContent(targetScene: SceneItemViewModel?): Node {
        return when (targetScene) {
            null -> emptyBody()
            else -> scope.get<LocationSetter>().also { add(it) }.root
        }
    }

    private fun Parent.emptyBody(): Node {
        return vbox(spacing = 16) {
            alignment = Pos.CENTER
            addClass(ComponentsStyles.cardBody)
            imageview("com/soyle/stories/scene/Symbols-design.png") {
                this.isPreserveRatio = true
                this.isSmooth = true
                fitHeight = 260.0
            }
            label("Use Locations as Scene Setting") { addClass(com.soyle.stories.common.components.Styles.fieldLabel) }
            textflow {
                textAlignment = TextAlignment.CENTER

                text("No scene has been targeted to use locations.  Click on a scene in the")
                hyperlink("Scene List") {
                    style { fontWeight = FontWeight.BOLD }
                    action { viewListener.openSceneListTool() }
                }
                text("to click anywhere in side of an open Scene Editor to ")
                label("target") { addClass(warningLabel) }
                text(" a scene and see what locations are being used.")
            }
        }
    }

    private fun targetSceneItem(sceneItem: SceneItemViewModel) {
        if (state.targetScene.value?.id != sceneItem.id) {
            viewListener.getLocationsUsedForSceneSetting(Scene.Id(UUID.fromString(sceneItem.id)))
        }
        state.targetScene.value = sceneItem
    }

    init {
        subscribe<SceneTargeted> {
            targetSceneItem(it.sceneItem)
        }
        (FX.getComponents(scope)[SceneListModel::class] as? SceneListModel)?.let {
            it.selectedItem.value?.let(this::targetSceneItem)
        }
    }

    class Styles : Stylesheet() {

        companion object {

            val sceneSetting by cssclass()
            val locationSetter by cssclass()
            val hasLocations by csspseudoclass()

            val sceneSettingChip by cssclass()

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            sceneSetting {
                fillWidth = true
            }
            locationSetter and hasLocations {
                padding = box(16.px)
                backgroundColor = multi(Color.WHITE)
            }
            sceneSettingChip {
                padding = box(5.px, 6.px)
                borderRadius = multi(box(12.px))
                backgroundRadius = multi(box(16.px))
                backgroundColor = multi(Color.LAVENDER)
                contentDisplay = ContentDisplay.RIGHT

                Stylesheet.button {
                    padding = box(0.px)
                    backgroundColor = multi(Color.TRANSPARENT)
                    contentDisplay = ContentDisplay.GRAPHIC_ONLY

                    Stylesheet.imageView {
                        translateX = 2.px
                        translateY = (-2).px
                    }
                }
            }
        }

    }
}