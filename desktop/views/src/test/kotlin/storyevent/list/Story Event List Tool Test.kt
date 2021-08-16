package com.soyle.stories.desktop.view.storyevent.list

import com.soyle.stories.desktop.adapter.storyevent.create.CreateStoryEventControllerDouble
import com.soyle.stories.desktop.view.runHeadless
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.access
import com.soyle.stories.storyevent.create.CreateStoryEventForm
import com.soyle.stories.storyevent.list.creationButton.StoryEventListTool
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat

class `Story Event List Tool Test` : FxRobot() {

    init {
        runHeadless()
    }

    private val primaryStage = FxToolkit.registerPrimaryStage()

    private val createStoryEventForm = CreateStoryEventForm(CreateStoryEventControllerDouble())
    private val createStoryEventFormFactory = {
        createStoryEventForm
    }

    private val tool = StoryEventListTool(createStoryEventFormFactory)

    @Test
    fun `should not open create story event dialog immediately`() {
        interact {}
        listTargetWindows().asSequence()
            .filterNot { it == primaryStage }
            .filter { it.scene.root == createStoryEventForm.root }
            .none { it.isShowing }
            .let(::assertTrue)
    }

    @Nested
    inner class `When Create Story Event Button is Clicked` {

        init {
            interact {
                tool.access().createStoryEventButton.fire()
            }
        }

        @Test
        fun `should open create story event dialog`() {
            listTargetWindows().asSequence()
                .filterNot { it == primaryStage }
                .filter { it.scene.root == createStoryEventForm.root }
                .single { it.isShowing }
        }

    }

}