package com.soyle.stories.storyevent.timeline.header

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.storyevent.create.CreateStoryEventController
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Button
import tornadofx.action
import tornadofx.add
import tornadofx.addClass

@Suppress("FunctionName")
interface TimelineHeaderCreateButtonComponent {

    fun TimelineHeaderCreateButton(): Node

    @ViewBuilder
    fun EventTarget.timelineHeaderCreateButton(): Node = TimelineHeaderCreateButton().also { add(it) }

    companion object {
        fun Implementation(
            createStoryEventController: CreateStoryEventController
        ) = object : TimelineHeaderCreateButtonComponent {
            override fun TimelineHeaderCreateButton(): Node = timelineHeaderCreateButton(TimelineHeaderCreateButtonPresenter(createStoryEventController))
        }
    }

}

interface TimelineHeaderCreateButtonActions {
    fun createStoryEvent()
}

fun timelineHeaderCreateButton(
    actions: TimelineHeaderCreateButtonActions
) = Button("Create Story Event").apply {
    addClass(ComponentsStyles.primary, ComponentsStyles.filled)
    action(actions::createStoryEvent)
}

class TimelineHeaderCreateButtonPresenter(
    private val createStoryEventController: CreateStoryEventController
) : TimelineHeaderCreateButtonActions {
    override fun createStoryEvent() { createStoryEventController.create() }
}