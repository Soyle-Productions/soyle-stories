package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.TimelineViewPortGridComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.makeStoryPointLabel
import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.storyevent.timeline.Pixels
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.*
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import org.junit.jupiter.api.Test

class `Grid Design` : DesignTest(), StoryPointLabelComponent by StoryPointLabelComponentDouble() {

    private val component = TimelineViewPortGridComponentDouble()

    override val node: TimelineViewPortGrid by lazy {
        component.TimelineViewPortGrid().apply {
            labels.setAll(List(5) {
                makeStoryPointLabel(name = "Label $it", time = UnitOfTime(it.toLong()))
            })
        }
    }

    @Test
    fun default() {
        verifyDesign {
            title = "Default"
        }
    }

    @Test
    fun scrolled() {
        verifyDesign {
            title = "Scrolled"
            node.offsetX = Pixels(100.0)
        }
    }

    @Test
    fun collapsed() {
        verifyDesign {
            title = "Collapsed"
            node.areLabelsCollapsed = true
        }
    }

}