package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.desktop.view.storyevent.create.`Create Story Event Dialog Access`.Companion.access
import com.soyle.stories.di.DI
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.create.CreateStoryEventPromptView

fun WorkBench.getOpenCreateStoryEventDialog(): CreateStoryEventPromptView? =
    robot.getOpenDialog<CreateStoryEventPromptView>()

fun WorkBench.getOpenCreateStoryEventDialogOrError(): CreateStoryEventPromptView =
    getOpenCreateStoryEventDialog() ?: error("Create Story Event Dialog is not open")

fun WorkBench.givenCreateStoryEventDialogHasBeenOpened(): CreateStoryEventPromptView =
    getOpenCreateStoryEventDialog() ?: run {
        givenStoryEventListToolHasBeenOpened().openCreateStoryEventDialog()
        getOpenCreateStoryEventDialogOrError()
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