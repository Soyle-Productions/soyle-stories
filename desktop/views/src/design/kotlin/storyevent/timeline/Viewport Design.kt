package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.TimelineViewPortComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.makeStoryPointLabel
import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import javafx.scene.Node
import org.junit.jupiter.api.Test
import tornadofx.observableListOf

class `Viewport Design` : DesignTest(), StoryPointLabelComponent by StoryPointLabelComponentDouble() {

    private val component = TimelineViewPortComponentDouble()

    override val node: Node
        get() = component.TimelineViewPort(
            observableListOf(
                makeStoryPointLabel(time = UnitOfTime(8)),
                makeStoryPointLabel(time = UnitOfTime(4)),
                makeStoryPointLabel(time = UnitOfTime(5)),
                makeStoryPointLabel(time = UnitOfTime(12))
            )
        )

    @Test
    fun default() {
        verifyDesign {
        }
    }
}