package com.soyle.stories.desktop.view.storyevent.time

import com.soyle.stories.desktop.view.common.runHeadless
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.time.RescheduleStoryEventDialog
import com.soyle.stories.storyevent.time.RescheduleStoryEventDialogView
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import com.soyle.stories.usecase.storyevent.time.reschedule.RescheduleStoryEvent
import javafx.scene.control.Spinner
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import org.testfx.util.WaitForAsyncUtils
import tornadofx.FX
import tornadofx.uiComponent

class `Reschedule Story Event Dialog Unit Test` : FxRobot() {

    // required inputs
    /** the story event to adjust */
    private val storyEventId = StoryEvent.Id()
    /** the current time for the story event */
    private val storyEventTime = 17L
    /** the props to pass in */
    private val props = RescheduleStoryEventDialog.Props(storyEventId, storyEventTime)

    // dependencies
    private var requestedStoryEventId: StoryEvent.Id? = null
    private var requestedStoryEventTime: Long? = null
    private val rescheduleStoryEventController = object : RescheduleStoryEventController {
        lateinit var currentJob: CompletableJob
        override fun rescheduleStoryEvent(storyEventId: StoryEvent.Id, time: Long): Job {
            requestedStoryEventId = storyEventId
            requestedStoryEventTime = time
            currentJob = Job()
            return currentJob
        }
    }

    // expected ui
    private fun view(): RescheduleStoryEventDialogView? = listWindows().asSequence()
        .filter { it.isShowing }
        .filter { it != primaryStage }
        .mapNotNull { it.scene.root.uiComponent<RescheduleStoryEventDialogView>() }
        .firstOrNull()

    private fun timeInput(view: RescheduleStoryEventDialogView? = view()) = view?.let {
        from(it.root).lookup("#time").query<Spinner<Long?>>()
    }

    private fun saveButton(view: RescheduleStoryEventDialogView? = view()) = view?.let {
        from(it.root).lookup("#save").queryButton()
    }

    private fun cancelButton(view: RescheduleStoryEventDialogView? = view()) = view?.let {
        from(it.root).lookup("#cancel").queryButton()
    }

    @Test
    fun `should open dialog`() {
        interact {
            RescheduleStoryEventDialogView(props, rescheduleStoryEventController)
        }

        assertNotNull(view())
    }

    @Test
    fun `should show current time value in time input`() {
        interact {
            RescheduleStoryEventDialogView(props, rescheduleStoryEventController)
        }

        val timeInput = timeInput(view()!!)!!
        assertThat(timeInput.editor.text).isEqualTo("$storyEventTime")
        assertThat(timeInput.value).isEqualTo(storyEventTime)
    }

    @Test
    fun `should show enabled cancel button`() {
        interact {
            RescheduleStoryEventDialogView(props, rescheduleStoryEventController)
        }

        assertThat(cancelButton(view()!!)!!).isEnabled
    }

    @Nested
    inner class `Cannot submit adjustment until time input is valid` {

        @Test
        fun `submit button should be disabled if time input has same value as current time`() {
            interact {
                RescheduleStoryEventDialogView(props, rescheduleStoryEventController)
            }

            val saveButton = saveButton(view()!!)!!
            assertThat(saveButton).isDisabled
        }

        @Test
        fun `submit button should be enabled if time value is valid`() {
            interact {
                RescheduleStoryEventDialogView(props, rescheduleStoryEventController)
                val timeInput = timeInput(view()!!)!!
                timeInput.editor.text = "7"
                timeInput.commitValue()
            }

            val saveButton =  saveButton(view()!!)!!
            assertThat(saveButton).isEnabled
        }

        @Test
        fun `submit button should be disabled if time input is empty`() {
            interact {
                RescheduleStoryEventDialogView(props, rescheduleStoryEventController)
                val timeInput = timeInput(view()!!)!!
                timeInput.editor.text = "7"
                timeInput.commitValue()
                timeInput.editor.text = ""
                timeInput.commitValue()
            }

            val saveButton =  saveButton(view()!!)!!
            assertThat(saveButton).isDisabled
        }

    }

    @Nested
    inner class `When Submitted` {

        @Test
        fun `inputs should be disabled until completion or failure`() {
            interact {
                RescheduleStoryEventDialogView(props, rescheduleStoryEventController)
                val view = view()!!
                val timeInput = timeInput(view)!!
                timeInput.editor.text = "7"
                timeInput.commitValue()
                saveButton(view)!!.fire()
            }

            val view = view()!!
            assertThat(timeInput(view)).isDisabled
            assertThat(saveButton(view)).isDisabled
            assertThat(cancelButton(view())).isDisabled
        }

        @Test
        fun `should send story event id and time value to reschedule controller`() {
            interact {
                RescheduleStoryEventDialogView(props, rescheduleStoryEventController)
                val view = view()!!
                val timeInput = timeInput(view)!!
                timeInput.editor.text = "7"
                timeInput.commitValue()
                saveButton(view)!!.fire()
            }

            assertThat(requestedStoryEventId).isEqualTo(storyEventId)
            assertThat(requestedStoryEventTime).isEqualTo(7L)
        }

        @Nested
        inner class `When Failed` {

            @Test
            fun `inputs should be enabled`() {
                interact {
                    RescheduleStoryEventDialogView(props, rescheduleStoryEventController)
                    val view = view()!!
                    val timeInput = timeInput(view)!!
                    timeInput.editor.text = "7"
                    timeInput.commitValue()
                    saveButton(view)!!.fire()
                }
                rescheduleStoryEventController.currentJob.completeExceptionally(Error("Expected failure"))
                WaitForAsyncUtils.waitForFxEvents()

                val view = view()!!
                assertThat(timeInput(view)).isEnabled
                assertThat(saveButton(view)).isEnabled
                assertThat(cancelButton(view())).isEnabled
            }

        }

        @Nested
        inner class `When Successful` {

            @Test
            fun `dialog should be closed`() {
                interact {
                    RescheduleStoryEventDialogView(props, rescheduleStoryEventController)
                    val view = view()!!
                    val timeInput = timeInput(view)!!
                    timeInput.editor.text = "7"
                    timeInput.commitValue()
                    saveButton(view)!!.fire()
                }
                rescheduleStoryEventController.currentJob.complete()
                WaitForAsyncUtils.waitForFxEvents()

                assertNull(view())
            }

        }

    }

    @Nested
    inner class `When Cancelled` {

        @Test
        fun `dialog should be closed`() {
            interact {
                RescheduleStoryEventDialogView(props, rescheduleStoryEventController)
                val view = view()!!
                cancelButton(view)!!.fire()
            }

            assertNull(view())
        }

    }

    @AfterEach
    fun `hide dialogs`() {
        FxToolkit.cleanupStages()
    }

    companion object {
        private val primaryStage by lazy { FxToolkit.registerPrimaryStage() }

        @JvmStatic
        @BeforeAll
        fun `setup toolkit`() {
            runHeadless()
            FX.setPrimaryStage(FX.defaultScope, primaryStage)
        }

    }


}