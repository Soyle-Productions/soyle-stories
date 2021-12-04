package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.time.StoryEventTimeChangeView
import javafx.scene.control.Spinner


fun WorkBench.getOpenInsertTimeDialogOrError(): StoryEventTimeChangeView =
    getOpenInsertTimeDialog() ?: error("Insert time dialog is not open")

fun WorkBench.getOpenInsertTimeDialog(): StoryEventTimeChangeView? = robot.getOpenDialog()

fun StoryEventTimeChangeView.insertTime(amount: Long) {
    with(robot) {
        interact {
            val spinner = from(root).lookup("#time").query<Spinner<Long?>>()
            spinner.editor.text = amount.toString()
            spinner.commitValue()

            val saveBtn = from(root).lookup("#save").queryButton()
            saveBtn.fire()
        }
    }
}