package com.soyle.stories.scene.reorderSceneRamifications

import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.di.resolve
import com.soyle.stories.scene.deleteSceneRamifications.sceneItem
import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.layout.Priority
import tornadofx.*

class ReorderSceneRamifications : View() {

    override val scope: ReorderSceneRamificationsScope = super.scope as ReorderSceneRamificationsScope
    private val viewListener = resolve<ReorderSceneRamificationsViewListener>()
    private val model = resolve<ReorderSceneRamificationsModel>()

    override val root: Parent = vbox {
        stackpane {
            vgrow = Priority.ALWAYS
            emptyListDisplay(
                model.scenes.select { it.isNotEmpty().toProperty() },
                "No scenes will be affected by reordering this scene.".toProperty(),
                "Reorder Scene".toProperty()
            ) {
                viewListener.reorderScene()
            }
            vbox {
                visibleWhen { model.scenes.select { it.isNotEmpty().toProperty() } }
                managedProperty().bind(visibleProperty())
                vgrow = Priority.ALWAYS
                bindChildren(model.scenes) {
                    sceneItem(it)
                }
            }
        }
        buttonbar {
            this.padding = Insets(10.0, 10.0, 10.0, 10.0)
            button("Reorder") {
                action {
                    viewListener.reorderScene()
                }
            }
            button("Cancel") {
                action {
                    viewListener.cancel()
                }
            }
        }
    }

    init {
        model.invalid.onChange {
            if (it != false) {
                viewListener.getValidState()
            }
        }
        viewListener.getValidState()
    }
}