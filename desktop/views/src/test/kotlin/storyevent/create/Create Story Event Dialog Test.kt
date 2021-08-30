package com.soyle.stories.desktop.view.storyevent.create

import com.soyle.stories.desktop.adapter.storyevent.create.CreateStoryEventControllerDouble
import com.soyle.stories.desktop.view.common.runHeadless
import com.soyle.stories.desktop.view.storyevent.create.`Create Story Event Dialog Access`.Companion.access
import com.soyle.stories.desktop.view.storyevent.create.`Create Story Event Dialog Access`.Companion.drive
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.create.CreateStoryEventDialog
import com.soyle.stories.storyevent.create.CreateStoryEventDialogView
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import javafx.stage.Window
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.cancel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import org.testfx.internal.JavaVersionAdapter
import tornadofx.uiComponent
import java.util.function.Consumer


class `Create Story Event Dialog Test` : FxRobot() {
    companion object {
        @JvmStatic
        @BeforeAll
        fun `setup toolkit`() {
            runHeadless()
            FxToolkit.registerPrimaryStage()
        }
    }

    private var createStoryEventBehavior: (name: NonBlankString) -> Job = { Job() }
    var requestedName: NonBlankString? = null
    var requestedTime: Long? = null
    var requestedRelativity: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative? = null

    private val createStoryEventController = CreateStoryEventControllerDouble(
        onCreateStoryEvent = { name, time ->
            requestedName = name
            requestedTime = time
            createStoryEventBehavior(name)
        },
        onCreateStoryEventRelativeTo = { name, relativeTo ->
            requestedName = name
            requestedRelativity = relativeTo
            createStoryEventBehavior(name)
        }
    )
    private var relativePlacement: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative? = null
    private fun createStoryEventDialog(props: CreateStoryEventDialog.Props): CreateStoryEventDialogView {
        var dialog: CreateStoryEventDialogView? = null
        interact {
            dialog = CreateStoryEventDialogView(props, createStoryEventController)
        }
        return dialog!!
    }

    private fun getOpenDialogs() = listTargetWindows().asSequence()
        .filter { it.isShowing }
        .filter { it.scene.root.uiComponent<CreateStoryEventDialogView>() != null }

    @AfterEach
    fun `close open dialogs`() {
        FxToolkit.cleanupStages()
    }

    @Nested
    inner class `Create with Default Props` {

        operator fun invoke() = createStoryEventDialog(CreateStoryEventDialog.Props())

        @Test
        fun `should open a new dialog`() {
            invoke()
            getOpenDialogs().single()
        }

        @Test
        fun `should show name input`() {
            val dialog = invoke()
            assertThat(dialog.access().nameInput).isNotNull()
        }

        @Test
        fun `should show time input`() {
            val dialog = invoke()
            assertThat(dialog.access().timeInput).isNotNull()
        }

        @Test
        fun `submit button should be disabled`() {
            val dialog = invoke()
            assertThat(dialog.access().submitButton).isDisabled
        }

        @Test
        fun `cancel button should be disabled`() {
            val dialog = invoke()
            assertThat(dialog.access().cancelButton).isEnabled
        }

    }

    @Nested
    inner class `Create with Relative Story Event` {

        operator fun invoke() = createStoryEventDialog(CreateStoryEventDialog.Props(
            CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative(StoryEvent.Id(), 1L)
        ))

        @Test
        fun `time input should not be visible`() {
             val dialog = invoke()
            assertThat(dialog.access().timeInput).isNull()
        }

    }

    @Nested
    inner class `Enter Text into Name` {

        @Nested
        inner class `When Blank Space is Entered` {

            operator fun invoke(dialog: CreateStoryEventDialogView) {
                dialog.drive { nameInput.text = "   " }
            }

            @Test
            fun `submit button should be disabled`() {
                val dialog = `Create with Default Props`().invoke()
                invoke(dialog)
                assertThat(dialog.access().submitButton).isDisabled
            }

        }

        @Nested
        inner class `When Letters are Entered` {

            operator fun invoke(dialog: CreateStoryEventDialogView) {
                dialog.drive { nameInput.text = "That thing" }
            }

            @Test
            fun `submit button should be enabled`() {
                val dialog = `Create with Default Props`().invoke()
                invoke(dialog)
                assertThat(dialog.access().submitButton).isEnabled
            }

        }

    }

    @Nested
    inner class `Cancel` {

        operator fun invoke(dialog: CreateStoryEventDialogView) {
            dialog.drive { cancelButton.fire() }
        }

        @Test
        fun `dialog should be closed`() {
            val dialog = `Create with Default Props`().invoke()
            invoke(dialog)

            assertTrue(getOpenDialogs().none())
        }

        @Test
        fun `should notify creator`() {
            var notified = false
            val dialog = createStoryEventDialog(CreateStoryEventDialog.Props(onCancelled = { notified = true }))
            invoke(dialog)

            assertTrue(notified)
        }

    }

    @Nested
    inner class `Submit` {

        private var notified = false
        private val dialog = createStoryEventDialog(CreateStoryEventDialog.Props(onCreated = {
            notified = true
        }))

        private val submission = Job()
        init {
            createStoryEventBehavior = { submission }
            dialog.drive { nameInput.text = "Some name" }
        }

        @Test
        fun `should not be able to submit again while awaiting submission`() {
            dialog.drive { submitButton.fire() }
            assertThat(dialog.access().nameInput).isDisabled
            assertThat(dialog.access().timeInput).isDisabled
            assertThat(dialog.access().submitButton).isDisabled
        }

        @Nested
        inner class `Success` {

            @Test
            fun `dialog should be closed`() {
                dialog.drive { submitButton.fire() }
                submission.complete()
                interact {  }

                assertTrue(getOpenDialogs().none())
            }

            @Test
            fun `should have received notice`() {
                dialog.drive { submitButton.fire() }
                submission.complete()
                interact {  }

                assertTrue(notified)
            }

        }

        @Nested
        inner class `Failure` {

            @Test
            fun `dialog should still be open`() {
                dialog.drive { submitButton.fire() }
                submission.cancel("Big 'ole nope")
                interact {  }

                getOpenDialogs().single()
            }

            @Test
            fun `inputs should be enabled`() {
                dialog.drive { submitButton.fire() }
                submission.cancel("Big 'ole nope")
                interact {  }

                assertThat(dialog.access().nameInput).isEnabled
                assertThat(dialog.access().timeInput).isEnabled
                assertThat(dialog.access().submitButton).isEnabled
            }

        }

    }

}