package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.desktop.view.storyevent.rename.`Rename Story Event Dialog Access`.Companion.drive
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.rename.RenameStoryEventDialogView
import org.junit.jupiter.api.fail

fun WorkBench.getOpenRenameStoryEventDialog(): RenameStoryEventDialogView? {
    return robot.getOpenDialog()
}

fun WorkBench.getOpenRenameStoryEventDialogOrError(): RenameStoryEventDialogView =
    getOpenRenameStoryEventDialog() ?: fail { "Rename Story Event Dialog is not open" }

fun RenameStoryEventDialogView.renameStoryEvent(newName: String) {
    drive {
        nameInput.text = newName
        submitButton.fire()
    }
}