package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.time.StoryEventTimeChangeView


fun WorkBench.getOpenInsertTimeDialogOrError(): StoryEventTimeChangeView =
    getOpenInsertTimeDialog() ?: error("Insert time dialog is not open")

fun WorkBench.getOpenInsertTimeDialog(): StoryEventTimeChangeView? = null

fun StoryEventTimeChangeView.insertTime(amount: Long) {

}