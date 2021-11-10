package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.time.StoryEventTimeChangeView
import javafx.scene.control.Spinner

@Suppress("unused")
fun WorkBench.getOpenStoryEventTimeAdjustmentDialog(): StoryEventTimeChangeView? =
    robot.getOpenDialog()

fun WorkBench.getOpenStoryEventTimeAdjustmentDialogOrError(): StoryEventTimeChangeView =
    getOpenStoryEventTimeAdjustmentDialog() ?: error("Reschedule Story Event Dialog is not open")

fun StoryEventTimeChangeView.reschedule(to: Long) {
    robot.interact {
        val timeInput = robot.from(root).lookup("#time").query<Spinner<Long?>>()
        timeInput.editor.text = to.toString()
        timeInput.commitValue()
        robot.from(root).lookup("#save").queryButton().fire()
    }
}

fun StoryEventTimeChangeView.adjustTime(by: Long) {
    robot.interact {
        val timeInput = robot.from(root).lookup("#time").query<Spinner<Long?>>()
        timeInput.editor.text = by.toString()
        timeInput.commitValue()
        robot.from(root).lookup("#save").queryButton().fire()
    }
}