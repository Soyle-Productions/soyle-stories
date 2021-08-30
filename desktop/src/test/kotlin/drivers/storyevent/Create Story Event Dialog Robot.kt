package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.desktop.view.storyevent.create.`Create Story Event Dialog Access`.Companion.access
import com.soyle.stories.di.DI
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.create.CreateStoryEventDialogView

fun WorkBench.getOpenCreateStoryEventDialog(): CreateStoryEventDialogView? =
    robot.getOpenDialog<CreateStoryEventDialogView>()

fun WorkBench.getOpenCreateStoryEventDialogOrError(): CreateStoryEventDialogView =
    getOpenCreateStoryEventDialog() ?: error("Create Story Event Dialog is not open")

fun WorkBench.givenCreateStoryEventDialogHasBeenOpened(): CreateStoryEventDialogView =
    getOpenCreateStoryEventDialog() ?: run {
        givenStoryEventListToolHasBeenOpened().openCreateStoryEventDialog()
        getOpenCreateStoryEventDialogOrError()
    }

fun CreateStoryEventDialogView.createStoryEventNamed(name: String, time: Int? = null) {
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