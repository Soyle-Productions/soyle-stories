package com.soyle.stories.scene.sceneSetting

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chip
import com.soyle.stories.common.components.layouts.emptyToolInvitation
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.SceneStyles
import com.soyle.stories.scene.SceneTargeted
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneListModel
import com.soyle.stories.scene.sceneSetting.SceneSettingView.Styles.Companion.sceneSetting
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
            addClass(SceneStyles.selectedSceneHeader)
            label("Scene: ")
            label(state.targetScene.stringBinding { it?.name ?: "No Scene Targeted" }) {
                toggleClass(TextStyles.warning, state.targetScene.isNull)
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
        return emptyToolInvitation {
            imageview("com/soyle/stories/scene/Symbols-design.png") {
                this.isPreserveRatio = true
                this.isSmooth = true
                fitHeight = 260.0
            }
            toolTitle("Use Locations as Scene Setting")
            textflow {
                textAlignment = TextAlignment.CENTER

                text("No scene has been targeted to use locations.  Click on a scene in the")
                hyperlink("Scene List") {
                    style { fontWeight = FontWeight.BOLD }
                    action { viewListener.openSceneListTool() }
                }
                text("to click anywhere in side of an open Scene Editor to ")
                label("target") { addClass(TextStyles.warning) }
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

            val locationList by cssclass()

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
            locationList {
                chip {
                    backgroundColor = multi(Color.LAVENDER)
                }
            }/*
            sceneSettingChip {
                backgroundColor = multi(Color.LAVENDER)
                padding = box(5.px, 6.px)
                borderRadius = multi(box(12.px))
                backgroundRadius = multi(box(16.px))
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
            }*/
        }

    }
}