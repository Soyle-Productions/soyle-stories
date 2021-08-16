package com.soyle.stories.storyevent.list.creationButton

import com.soyle.stories.storyevent.create.CreateStoryEventForm
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.stage.Stage
import tornadofx.action
import tornadofx.add
import tornadofx.button
import tornadofx.runLater

class StoryEventListTool(
    private val createStoryEventFormFactory: () -> CreateStoryEventForm
) {

    private val createStoryEventButton = Button().apply {
        action(::openCreateStoryEventDialog)
    }

    val root: Node = Pane().apply {
        add(createStoryEventButton)
    }

    private fun openCreateStoryEventDialog() {
        Stage().apply {
            scene = Scene(createStoryEventFormFactory().root as Parent)
            show()
        }
    }

}