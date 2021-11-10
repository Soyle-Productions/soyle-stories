package com.soyle.stories.desktop.view.storyevent

import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.storyevent.time.StoryEventTimeChangeView
import com.soyle.stories.storyevent.time.StoryEventTimeChangeViewModel
import com.soyle.stories.storyevent.time.adjust.AdjustTimePromptViewModel
import com.soyle.stories.storyevent.time.reschedule.ReschedulePromptViewModel
import javafx.scene.Node
import org.junit.jupiter.api.Test

class `Time Adjustment Prompt Design` : DesignTest() {

    private var viewModel: StoryEventTimeChangeViewModel = AdjustTimePromptViewModel()
    override val node: Node
        get() = StoryEventTimeChangeView(viewModel).root

    @Test
    fun `created without current time`() {
        verifyDesign()
    }

    @Test
    fun `created with current time`() {
        viewModel = ReschedulePromptViewModel(9L)
        verifyDesign()
    }

    @Test
    fun submitting() {
        viewModel = ReschedulePromptViewModel(4)
        viewModel.submitting()
        verifyDesign()
    }


}