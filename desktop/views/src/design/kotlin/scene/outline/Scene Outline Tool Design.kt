package com.soyle.stories.desktop.view.scene.outline

import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.scene.outline.SceneOutlineView
import com.soyle.stories.scene.outline.SceneOutlineViewModel
import com.soyle.stories.usecase.storyevent.StoryEventItem
import javafx.scene.Node
import org.junit.jupiter.api.Test

class `Scene Outline Tool Design` : DesignTest() {

    private val viewModel = SceneOutlineViewModel()
    override val node: Node
        get() = SceneOutlineView(viewModel)

    @Test
    fun `opened without targeted scene`() {
        verifyDesign()
    }

    @Test
    fun `loading targeted scene`() {
        viewModel.reset(Scene.Id(), "Frank Dies")
        verifyDesign()
    }

    @Test
    fun `outline failed to load`() {
        viewModel.reset(Scene.Id(), "Frank Dies")
        viewModel.failed(Error("This failure was actually intended"))
        verifyDesign()
    }

    @Test
    fun `no story events in scene yet`() {
        viewModel.reset(Scene.Id(), "Frank Dies")
        viewModel.setItems(emptyList())
        verifyDesign()
    }

    @Test
    fun `populated with story events`() {
        viewModel.reset(Scene.Id(), "Frank Dies")
        viewModel.setItems(List(5) { StoryEventItem(StoryEvent.Id(), "Story Event $it", (0L..100L).random()) })
        verifyDesign()
    }

}