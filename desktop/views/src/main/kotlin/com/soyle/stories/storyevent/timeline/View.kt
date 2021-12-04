package com.soyle.stories.storyevent.timeline

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import javafx.event.EventTarget
import javafx.scene.control.ScrollToEvent
import javafx.scene.control.SkinBase
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*

class TimelineSkin(
    private val timeline: Timeline,
    private val gui: TimelineComponent.GUI
) : SkinBase<Timeline>(timeline) {

    private var scrollToHandler: (ScrollToEvent<StoryPointLabel>) -> Unit = {}

    private val root = VBox().apply {
        toggleClass(ComponentsStyles.loading, timeline.isLoading)
        toggleClass(Stylesheet.error, timeline.isFailed)
        toggleClass(ComponentsStyles.loaded, timeline.isLoaded)

        dynamicContent(timeline.state()) {
            when (it) {
                is Timeline.State.Loading -> loading(it)
                is Timeline.State.Failed -> failed(it)
                is Timeline.State.Loaded -> loaded(it)
                null -> {
                }
            }
        }
    }

    @ViewBuilder
    private fun EventTarget.loading(loadingState: Timeline.State.Loading) {
        progressindicator()
    }

    @ViewBuilder
    private fun EventTarget.failed(failedState: Timeline.State.Failed) {
        button("Retry")
    }

    @ViewBuilder
    private fun EventTarget.loaded(loadedState: Timeline.State.Loaded) {
        with(gui) {
            timelineHeader(timeline.condensedLabels(), loadedState.selection, loadedState.storyEventItems()).apply {
                asSurface {
                    relativeElevation = Elevation.getValue(8)
                    inheritedElevation = Elevation.getValue(4)
                }
            }
            timelineViewPort(loadedState.storyEventItems()) {
                vgrow = Priority.ALWAYS
                labelsCollapsed().bind(timeline.condensedLabels())
                selection = loadedState.selection
                scrollToHandler = {
                    scrollToLabel(it.scrollTarget)
                }
            }
        }
    }

    init {
        children.add(root)
        timeline.addEventHandler(Timeline.SCROLL_TO_LABEl) {
            scrollToHandler(it)
        }
    }


}