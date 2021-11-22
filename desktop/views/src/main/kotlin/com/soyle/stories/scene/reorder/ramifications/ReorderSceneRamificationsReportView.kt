package com.soyle.stories.scene.reorder.ramifications

import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.common.emptyProperty
import com.soyle.stories.scene.delete.ramifications.sceneItem
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.layout.Priority
import tornadofx.*

class ReorderSceneRamificationsReportView(
    private val viewModel: ReorderSceneRamificationsReportViewModel
) : View() {

    private val notEmpty = viewModel.scenes.emptyProperty().not()

    override val root: Parent = vbox {
        stackpane {
            vgrow = Priority.ALWAYS
            emptyListDisplay(
                notEmpty,
                "No scenes will be affected by reordering this scene.".toProperty(),
                "Reorder Scene".toProperty(),
                callToAction = viewModel::reorder
            )
            vbox {
                visibleWhen(notEmpty)
                managedProperty().bind(visibleProperty())
                vgrow = Priority.ALWAYS
                bindChildren(viewModel.scenes) {
                    sceneItem(it)
                }
            }
        }
        buttonbar {
            this.padding = Insets(10.0, 10.0, 10.0, 10.0)
            button("Reorder") {
                action {
                    viewModel.reorder()
                }
            }
            button("Cancel") {
                action {
                    viewModel.cancel()
                }
            }
        }
    }
}