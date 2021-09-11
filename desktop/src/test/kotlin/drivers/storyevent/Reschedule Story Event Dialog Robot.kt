package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.time.TimeAdjustmentPromptView
import javafx.scene.control.Spinner

@Suppress("unused")
fun WorkBench.getOpenStoryEventTimeAdjustmentDialog(): TimeAdjustmentPromptView? =
    robot.getOpenDialog()

fun WorkBench.getOpenStoryEventTimeAdjustmentDialogOrError(): TimeAdjustmentPromptView =
    getOpenStoryEventTimeAdjustmentDialog() ?: error("Reschedule Story Event Dialog is not open")

fun TimeAdjustmentPromptView.reschedule(to: Long) {
    robot.interact {
        val timeInput = robot.from(root).lookup("#time").query<Spinner<Long?>>()
        timeInput.editor.text = to.toString()
        timeInput.commitValue()
        robot.from(root).lookup("#save").queryButton().fire()
    }
}

fun TimeAdjustmentPromptView.adjustTime(by: Long) {
    robot.interact {
        val timeInput = robot.from(root).lookup("#time").query<Spinner<Long?>>()
        timeInput.editor.text = by.toString()
        timeInput.commitValue()
        robot.from(root).lookup("#save").queryButton().fire()
    }
}