package com.soyle.stories.ramifications

import com.soyle.stories.common.existsWhen
import com.soyle.stories.di.resolve
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import tornadofx.*

class RamificationsView : View() {

    val viewModel by param(defaultValue = resolve<RamificationsViewModel>())

    private val reportList = VBox().apply {
        bindChildren(viewModel._reports) { key, report ->
            hbox {
                existsWhen(report.isListed())
                label {
                    textProperty().bind(report.text())
                    graphicProperty().bind(report.graphic())
                }
                button("Perform Action") {
                    action {
                        if (report.close()) viewModel._reports.remove(key)
                    }
                }
            }
        }
    }
    private val reportView = StackPane().apply {
        id = "reports"
        bindChildren(viewModel._reports) { key, report ->
            (report.content ?: Pane()).also { it.existsWhen(report.isListed()) }
        }
    }
    override val root: Parent = splitpane(Orientation.HORIZONTAL, reportList, reportView)

}