package com.soyle.stories.desktop.view.storyevent

import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.storyevent.rename.RenameStoryEventPromptUserActions
import com.soyle.stories.storyevent.rename.RenameStoryEventPromptView
import com.soyle.stories.storyevent.rename.RenameStoryEventPromptViewModel
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.stage.StageStyle
import org.junit.jupiter.api.Test

class `Rename Story Event Prompt Design` : DesignTest() {

    private val viewModel = RenameStoryEventPromptViewModel("Current Name")

    private val actions = object : RenameStoryEventPromptUserActions {
        override fun rename() = Unit
        override fun cancel() = Unit
    }

    override val node: Node
        get() = RenameStoryEventPromptView(viewModel, actions).root

    @Test
    fun `initial`() {
        verifyDesign {
        }
    }

    @Test
    fun `disabled`() {
        viewModel.nameProperty().value = "New Name"
        viewModel.disable()
        verifyDesign()
    }


}