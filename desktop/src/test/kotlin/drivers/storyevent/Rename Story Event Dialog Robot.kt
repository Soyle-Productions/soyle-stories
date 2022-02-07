package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.desktop.view.storyevent.rename.`Rename Story Event Dialog Access`.Companion.drive
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.rename.RenameStoryEventPromptView
import org.junit.jupiter.api.fail

fun WorkBench.givenRenameStoryEventDialogHasBeenOpened(storyEvent: StoryEvent): RenameStoryEventPromptView
{
    return getOpenRenameStoryEventDialog() ?: run {
        givenStoryEventListToolHasBeenOpened()
            .givenStoryEventHasBeenSelected(storyEvent)
            .openRenameStoryEventDialog()
        getOpenRenameStoryEventDialogOrError()
    }
}

fun WorkBench.getOpenRenameStoryEventDialog(): RenameStoryEventPromptView? {
    return robot.getOpenDialog()
}

fun WorkBench.getOpenRenameStoryEventDialogOrError(): RenameStoryEventPromptView =
    getOpenRenameStoryEventDialog() ?: fail { "Rename Story Event Dialog is not open" }

fun RenameStoryEventPromptView.renameStoryEvent(newName: String) {
    drive {
        nameInput.text = newName
        submitButton.fire()
    }
}