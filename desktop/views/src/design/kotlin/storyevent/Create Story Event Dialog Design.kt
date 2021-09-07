package com.soyle.stories.desktop.view.storyevent

import com.soyle.stories.desktop.view.storyevent.create.`Create Story Event Dialog Access`.Companion.access
import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.storyevent.create.CreateStoryEventPrompt
import com.soyle.stories.storyevent.create.CreateStoryEventPromptUserActions
import com.soyle.stories.storyevent.create.CreateStoryEventPromptView
import com.soyle.stories.storyevent.create.CreateStoryEventPromptViewModel
import javafx.scene.Node
import org.junit.jupiter.api.Test
import tornadofx.text
import tornadofx.uiComponent

class `Create Story Event Dialog Design` : DesignTest() {

    private val actions = object : CreateStoryEventPromptUserActions {
        override fun createStoryEvent() = Unit
        override fun cancel() = Unit
    }
    private lateinit var viewModel: CreateStoryEventPromptViewModel
    override val node: Node by lazy {
        CreateStoryEventPromptView(actions, viewModel).root
    }

    @Test
    fun `Created without Time`() {
        viewModel = CreateStoryEventPromptViewModel(false)
        verifyDesign()
    }

    @Test
    fun `Created with Time`() {
        viewModel = CreateStoryEventPromptViewModel(true)
        verifyDesign()
    }

    @Test
    fun `Created without Time and Valid Inputs`() {
        viewModel = CreateStoryEventPromptViewModel(false)
        verifyDesign {
            node.uiComponent<CreateStoryEventPromptView>()!!.access().nameInput.text = "Some Name"
            node.uiComponent<CreateStoryEventPromptView>()!!.access().timeInput!!.editor.text = "9"
        }
    }

    @Test
    fun `Created with Time and Valid Inputs`() {
        viewModel = CreateStoryEventPromptViewModel(true)
        verifyDesign {
            node.uiComponent<CreateStoryEventPromptView>()!!.access().nameInput.text = "Some Name"
        }
    }
}