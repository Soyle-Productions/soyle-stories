package com.soyle.stories.storyevent.timeline

import com.soyle.stories.common.components.ComponentsStyles.Companion.loaded
import com.soyle.stories.common.components.ComponentsStyles.Companion.loading
import javafx.scene.Parent
import javafx.scene.layout.VBox
import tornadofx.*

class TimelineView(
    viewModel: TimelineViewModel
) : View() {

    override val root: Parent = vbox {
        addClass(TimelineStyles.timeline)
        toggleClass(loading, viewModel.loading())
        toggleClass(loaded, viewModel.loaded())
        dynamicContent(viewModel.type(), ::content)
    }

    private fun content(parent: VBox, viewModel: TimelineViewModel?) {
        when (viewModel) {
            is TimelineViewModel.Loading -> parent.loading()
            is TimelineViewModel.Failed -> parent.failed()
            is TimelineViewModel.Loaded -> parent.loaded()
        }
    }

    private fun VBox.loading() {
        progressindicator()
    }

    private fun VBox.failed() {
        button("Retry")
    }

    private fun VBox.loaded() {

    }

}