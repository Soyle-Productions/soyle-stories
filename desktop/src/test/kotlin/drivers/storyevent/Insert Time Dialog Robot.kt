package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.time.insert.InsertTimeForm


fun WorkBench.getOpenInsertTimeDialogOrError(): InsertTimeForm =
    getOpenInsertTimeDialog() ?: error("Insert time dialog is not open")

fun WorkBench.getOpenInsertTimeDialog(): InsertTimeForm? = null

fun InsertTimeForm.insertTime(amount: Long) {

}