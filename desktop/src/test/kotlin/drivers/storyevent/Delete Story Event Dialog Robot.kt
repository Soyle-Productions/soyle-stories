package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.remove.RemoveStoryEventConfirmationDialogView
import org.junit.jupiter.api.fail

fun WorkBench.getOpenDeleteStoryEventDialog(): RemoveStoryEventConfirmationDialogView? =
    robot.getOpenDialog()

fun WorkBench.getOpenDeleteStoryEventDialogOrError(): RemoveStoryEventConfirmationDialogView =
    getOpenDeleteStoryEventDialog() ?: fail("Delete story event dialog is not open")

fun WorkBench.givenDeleteStoryEventDialogHasBeenOpened(storyEvents: List<StoryEvent>): RemoveStoryEventConfirmationDialogView =
    getOpenDeleteStoryEventDialog() ?: run {
        givenStoryEventListToolHasBeenOpened()
            .givenStoryEventsHaveBeenSelected(storyEvents)
            .openDeleteStoryEventDialog()
        getOpenDeleteStoryEventDialogOrError()
    }

fun RemoveStoryEventConfirmationDialogView.confirm() {
    robot.interact {
        robot.from(root).lookup("#confirm").queryButton().fire()
    }
}