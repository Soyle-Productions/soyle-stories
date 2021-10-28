package com.soyle.stories.storyevent.item.icon

import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.layout.Pane
import tornadofx.add
import tornadofx.addClass

interface StoryEventItemIconComponent {

    @Suppress("FunctionName")
    fun StoryEventItemIcon(): Node = Pane().apply {
        addClass(StoryEventItemIconStyles.storyEventItemIcon)
    }

    fun EventTarget.storyEventItemIcon(): Node = StoryEventItemIcon().also { add(it) }

}