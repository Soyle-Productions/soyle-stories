package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.desktop.view.storyevent.create.`Create Story Event Dialog Access`.Companion.access
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.create.CreateStoryEventPromptView

@Suppress("unused")
fun WorkBench.getOpenCreateStoryEventDialog(): CreateStoryEventPromptView? =
    robot.getOpenDialog()

fun WorkBench.getOpenCreateStoryEventDialogOrError(): CreateStoryEventPromptView =
    getOpenCreateStoryEventDialog() ?: error("Create Story Event Dialog is not open")

fun WorkBench.givenCreateStoryEventDialogHasBeenOpened(): CreateStoryEventPromptView =
    getOpenCreateStoryEventDialog() ?: run {
        openCreateStoryEventDialog()
        getOpenCreateStoryEventDialogOrError()
    }

fun WorkBench.openCreateStoryEventDialog() {
    findMenuItemById("file_new_storyevent")!!
        .apply { robot.interact { fire() } }
}

fun CreateStoryEventPromptView.createStoryEventNamed(name: String, time: Int? = null) {
    with(access()) {
        interact {
            nameInput.text = name
            if (time != null) {
                timeInput?.editor?.text = "$time"
                timeInput?.commitValue()
            }
            submitButton.fire()
        }
    }
}