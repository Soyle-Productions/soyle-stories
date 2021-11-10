package com.soyle.stories.desktop.view.storyevent

import com.soyle.stories.desktop.view.storyevent.create.`Create Story Event Dialog Access`.Companion.access
import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.storyevent.create.CreateStoryEventPrompt
import com.soyle.stories.storyevent.create.CreateStoryEventPromptView
import javafx.scene.Node
import org.junit.jupiter.api.Test
import tornadofx.uiComponent

class `Create Story Event Dialog Design` : DesignTest() {

    private var viewModel = CreateStoryEventPrompt()
    override val node: Node by lazy {
        CreateStoryEventPromptView(viewModel).root
    }

    @Test
    fun `Created without Time`() {
        viewModel.isTimeFieldShown = false
        verifyDesign()
    }

    @Test
    fun `Created with Time`() {
        viewModel.isTimeFieldShown = true
        verifyDesign()
    }

    @Test
    fun `Created without Time and Valid Inputs`() {
        viewModel.isTimeFieldShown = false
        verifyDesign {
            node.uiComponent<CreateStoryEventPromptView>()!!.access().nameInput.text = "Some Name"
            node.uiComponent<CreateStoryEventPromptView>()!!.access().timeInput!!.editor.text = "9"
        }
    }

    @Test
    fun `Created with Time and Valid Inputs`() {
        viewModel.isTimeFieldShown = true
        verifyDesign {
            node.uiComponent<CreateStoryEventPromptView>()!!.access().nameInput.text = "Some Name"
        }
    }
}