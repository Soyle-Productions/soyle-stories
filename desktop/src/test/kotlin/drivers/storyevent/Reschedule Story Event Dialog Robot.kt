package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.time.RescheduleStoryEventDialogView
import javafx.scene.control.Spinner

fun WorkBench.getOpenStoryEventTimeAdjustmentDialog(): RescheduleStoryEventDialogView? =
    robot.getOpenDialog()

fun WorkBench.getOpenStoryEventTimeAdjustmentDialogOrError(): RescheduleStoryEventDialogView =
    getOpenStoryEventTimeAdjustmentDialog() ?: error("Reschedule Story Event Dialog is not open")

fun RescheduleStoryEventDialogView.adjustTime(to: Long) {
    robot.interact {
        val timeInput = robot.from(root).lookup("#time").query<Spinner<Long?>>()
        timeInput.editor.text = to.toString()
        timeInput.commitValue()
        robot.from(root).lookup("#save").queryButton().fire()
    }
}