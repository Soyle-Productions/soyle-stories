package com.soyle.stories.scene.outline

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.buttons.primaryButton
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.scene.outline.SceneOutlineStyles.Companion.sceneOutline
import com.soyle.stories.common.components.ComponentsStyles.Companion.loading
import com.soyle.stories.common.components.ComponentsStyles.Companion.failed
import com.soyle.stories.common.components.card
import com.soyle.stories.common.components.cardBody
import com.soyle.stories.common.components.cardHeader
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Companion.chip
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.components.text.Caption.Companion.caption
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.common.emptyProperty
import com.soyle.stories.scene.outline.SceneOutlineStyles.Companion.untargeted
import com.soyle.stories.usecase.storyevent.StoryEventItem
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.ListCell
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*
import tornadofx.Stylesheet.Companion.headerPanel

class SceneOutlineView(
    private val viewModel: SceneOutlineViewModel,
) : VBox() {

    init {
        asSurface {
            absoluteElevation = Elevation[4]!!
        }
    }

    init {
        dynamicContent(viewModel.isLoading()) {
            styleClass.clear()
            addClass(sceneOutline)
            when (viewModel.isLoading().get()) {
                true -> loading()
                false -> loaded()
            }
        }
    }

    private fun loading() {
        addClass(loading)
        sectionTitle("Loading ${viewModel.sceneName().get()}")
        progressindicator()
    }

    private fun loaded() {
        when (val sceneId = viewModel.sceneId) {
            null -> awaitingTarget()
            else -> targeted()
        }
    }

    private fun awaitingTarget() {
        addClass(untargeted)
        toolTitle("Scene Outline")
        sectionTitle("No scene is currently selected.  Select a scene in the scene list to see it's outline.")
    }

    private fun targeted() {
        when (val failureMessage = viewModel.failureMessage().get()) {
            null -> success()
            else -> failure(failureMessage)
        }
    }

    private fun failure(failureMessage: String) {
        addClass(failed)
        sectionTitle(failureMessage)
        primaryButton("Retry") {
            action { }
        }
    }

    private fun success() {
        header()
        vbox {
            val emptyProperty = viewModel.items().emptyProperty()
            properties["com.soyle.stories.scene.outline.empty"] = emptyProperty
            addClass(Stylesheet.content)
            vgrow = Priority.ALWAYS
            asSurface {
                inheritedElevation = Elevation[4]!!
                relativeElevation = Elevation[4]!!
            }
            dynamicContent(emptyProperty) {
                when (viewModel.items().isEmpty()) {
                    true -> empty()
                    false -> populated()
                }
            }
        }
    }

    private fun header() {
        hbox {
            addClass(headerPanel)
            asSurface {
                inheritedElevation = Elevation[4]!!
                relativeElevation = Elevation[12]!!
            }
            sectionTitle(viewModel.sceneName()) {
                graphic = chip(viewModel.itemCount().asString())
            }
        }
    }

    @ViewBuilder
    private fun Node.empty() {
        addClass(Stylesheet.empty)
        toolTitle("Scene Outline")
        sectionTitle("There are no story events covered by this scene yet.")
    }

    @ViewBuilder
    private fun EventTarget.populated() {
        removeClass(Stylesheet.empty)
        listview<StoryEventItem>(viewModel.items()) {
            vgrow = Priority.ALWAYS
            asSurface {
                inheritedElevation = Elevation[4]!!
            }
            setOnMouseClicked { if ((it.target as? ListCell<*>)?.isEmpty == true) selectionModel?.clearSelection() }

            cellFormat {
                graphic = card {
                    asSurface {
                        inheritedElevation = Elevation[4]!!
                        relativeElevation = Elevation[8]!!
                    }
                    cardHeader {}
                    cardBody {
                        fieldLabel(it.storyEventName)
                    }
                }
            }
        }
    }

}