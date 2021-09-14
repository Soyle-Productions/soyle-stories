package com.soyle.stories.desktop.view.storyevent

import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.storyevent.time.TimeAdjustmentPromptView
import com.soyle.stories.storyevent.time.TimeAdjustmentPromptViewActions
import com.soyle.stories.storyevent.time.TimeAdjustmentPromptViewModel
import javafx.scene.Node
import org.junit.jupiter.api.Test

class `Time Adjustment Prompt Design` : DesignTest() {

    private val actions = object : TimeAdjustmentPromptViewActions {
        override fun submit() = Unit
        override fun cancel() = Unit
    }

    private var currentTime: Long? = null
    private val viewModel by lazy { TimeAdjustmentPromptViewModel(currentTime) }
    override val node: Node
        get() = TimeAdjustmentPromptView(actions, viewModel).root

    @Test
    fun `created without current time`() {
        verifyDesign()
    }

    @Test
    fun `created with current time`() {
        currentTime = 9L
        verifyDesign()
    }

    @Test
    fun submitting() {
        viewModel.time.set("4")
        viewModel.submitting()
        verifyDesign()
    }


}