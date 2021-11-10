package com.soyle.stories.storyevent.time.adjust

import com.soyle.stories.storyevent.time.StoryEventTimeChangeViewModel
import javafx.beans.binding.BooleanExpression
import tornadofx.booleanBinding

class AdjustTimePromptViewModel : StoryEventTimeChangeViewModel() {

    override val canSubmitExpression: BooleanExpression = booleanBinding(timeText(), submitting()) {
        !isSubmitting && time != null && time != 0L
    }

}