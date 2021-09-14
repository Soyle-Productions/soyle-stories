package com.soyle.stories.desktop.view.storyevent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.storyevent.remove.RemoveStoryEventConfirmationPromptView
import com.soyle.stories.storyevent.remove.RemoveStoryEventConfirmationPromptViewActions
import com.soyle.stories.storyevent.remove.RemoveStoryEventConfirmationPromptViewModel
import javafx.application.Platform
import javafx.scene.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test

class `Remove Story Event Confirmation Prompt Design` : DesignTest() {

    private val actions = object : RemoveStoryEventConfirmationPromptViewActions {
        override fun confirm() = Unit
        override fun cancel() = Unit
    }
    private val threadTransformer = object : ThreadTransformer {
        override fun isGuiThread(): Boolean = Platform.isFxApplicationThread()
        override fun async(task: suspend CoroutineScope.() -> Unit): Job = Job()
        override fun gui(update: suspend CoroutineScope.() -> Unit) {
            CoroutineScope(Dispatchers.JavaFx).launch { update() }
        }
    }
    private val viewModel = RemoveStoryEventConfirmationPromptViewModel(threadTransformer)
    override val node: Node
        get() = RemoveStoryEventConfirmationPromptView(viewModel).root

    @Test
    fun `awaiting confirmation`() {
        verifyDesign {
            viewModel.needed()
        }
    }

    @Test
    fun confirming() {
        verifyDesign {
            viewModel.needed()
            viewModel.confirm()
        }
    }
}