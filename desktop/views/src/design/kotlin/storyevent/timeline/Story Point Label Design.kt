package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelComponentDouble
import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.layout.VBox
import org.junit.jupiter.api.Test
import tornadofx.add
import tornadofx.box
import tornadofx.px
import tornadofx.style

class `Story Point Label Design` : DesignTest(), StoryPointLabelComponent, StoryPointLabelComponent.GUIComponents {

    private val component = StoryPointLabelComponentDouble()

    private val label by lazy { StoryPointLabel(StoryEvent.Id(), "Some text", UnitOfTime(8)) }

    override val node: Node
        get() = VBox().apply {
            style { padding = box(8.px) }
            add(label)
        }

    @Test
    fun default() {
        verifyDesign {
            title = "Default"
        }
    }

    @Test
    fun collapsed() {
        verifyDesign {
            title = "Collapsed"
            label.isCollapsed = true
        }
    }

    override fun StoryPointLabel(storyEventId: StoryEvent.Id, name: String, time: UnitOfTime): StoryPointLabel =
        component.StoryPointLabel(storyEventId, name, time)

}