package com.soyle.stories.storyevent.time.reschedule

import com.soyle.stories.storyevent.time.StoryEventTimeChangeViewModel
import javafx.beans.binding.BooleanExpression
import tornadofx.booleanBinding

class ReschedulePromptViewModel(private val currentTime: Long) : StoryEventTimeChangeViewModel() {

    override val canSubmitExpression: BooleanExpression = booleanBinding(timeText(), submitting()) {
        !isSubmitting && time != null && time != currentTime
    }

    init {
        time = currentTime
    }

}