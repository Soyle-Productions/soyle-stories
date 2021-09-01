package com.soyle.stories.desktop.view.storyevent.time

import com.soyle.stories.desktop.view.common.runHeadless
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.time.RescheduleStoryEventDialog
import com.soyle.stories.storyevent.time.RescheduleStoryEventDialogView
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
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

    // == DEPENDENCIES ==
    // reschedule story event
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

    // adjust story events time
    private val adjustStoryEventsTimeController = object : AdjustStoryEventsTimeController {
        var requestedStoryEventIds: Set<StoryEvent.Id>? = null
        var requestedAdjustmentTime: Long? = null

        lateinit var currentJob: CompletableJob
        override fun adjustStoryEventsTime(storyEventIds: Set<StoryEvent.Id>, amount: Long): Job {
            requestedStoryEventIds = storyEventIds
            requestedAdjustmentTime = amount
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

    // fixture
    fun rescheduleStoryEventDialogView(
        props: RescheduleStoryEventDialog.Props = RescheduleStoryEventDialog.Props(
            storyEventId,
            storyEventTime
        )
    ) {
        interact {
            RescheduleStoryEventDialogView(props, rescheduleStoryEventController, adjustStoryEventsTimeController)
        }
    }

    @Test
    fun `should open dialog`() {
        rescheduleStoryEventDialogView()

        assertNotNull(view())
    }

    @Test
    fun `should show enabled cancel button`() {
        rescheduleStoryEventDialogView()

        assertThat(cancelButton(view()!!)!!).isEnabled
    }

    @Nested
    inner class `When Created to Reschedule` {

        @Test
        fun `should show current time value in time input`() {
            rescheduleStoryEventDialogView()

            val timeInput = timeInput(view()!!)!!
            assertThat(timeInput.editor.text).isEqualTo("$storyEventTime")
            assertThat(timeInput.value).isEqualTo(storyEventTime)
        }

    }

    @Nested
    inner class `When Created to Adjust Times` {

        @Test
        fun `should show time input with no value`() {
            rescheduleStoryEventDialogView(RescheduleStoryEventDialog.AdjustTimes(setOf()))

            val timeInput = timeInput(view()!!)!!
            assertThat(timeInput.editor.text).isEqualTo("")
            assertThat(timeInput.value).isEqualTo(null)
        }

    }

    @Nested
    inner class `Cannot submit adjustment until time input is valid` {

        @Test
        fun `submit button should be disabled if time input has same value as current time`() {
            rescheduleStoryEventDialogView()

            val saveButton = saveButton(view()!!)!!
            assertThat(saveButton).isDisabled
        }

        @Test
        fun `submit button should be disabled if adjustment is zero`() {
            rescheduleStoryEventDialogView(RescheduleStoryEventDialog.AdjustTimes(setOf()))
            interact {
                val timeInput = timeInput(view()!!)!!
                timeInput.editor.text = "0"
                timeInput.commitValue()
            }

            val saveButton = saveButton(view()!!)!!
            assertThat(saveButton).isDisabled
        }

        @Test
        fun `submit button should be enabled if time value is valid`() {
            rescheduleStoryEventDialogView()
            interact {
                val timeInput = timeInput(view()!!)!!
                timeInput.editor.text = "7"
                timeInput.commitValue()
            }

            val saveButton = saveButton(view()!!)!!
            assertThat(saveButton).isEnabled
        }

        @Test
        fun `submit button should be disabled if time input is empty`() {
            rescheduleStoryEventDialogView()
            interact {
                val timeInput = timeInput(view()!!)!!
                timeInput.editor.text = "7"
                timeInput.commitValue()
                timeInput.editor.text = ""
                timeInput.commitValue()
            }

            val saveButton = saveButton(view()!!)!!
            assertThat(saveButton).isDisabled
        }

    }

    @Nested
    inner class `When Submitted` {

        @Test
        fun `inputs should be disabled until completion or failure`() {
            rescheduleStoryEventDialogView()
            interact {
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
            rescheduleStoryEventDialogView()
            interact {
                val view = view()!!
                val timeInput = timeInput(view)!!
                timeInput.editor.text = "7"
                timeInput.commitValue()
                saveButton(view)!!.fire()
            }

            assertThat(requestedStoryEventId).isEqualTo(storyEventId)
            assertThat(requestedStoryEventTime).isEqualTo(7L)
        }

        @Test
        fun `should send all story event ids and adjustment value to adjustment controller`() {
            val expectedStoryEventIds = List(8) { StoryEvent.Id() }.toSet()
            rescheduleStoryEventDialogView(RescheduleStoryEventDialog.AdjustTimes(expectedStoryEventIds))
            interact {
                val view = view()!!
                val timeInput = timeInput(view)!!
                timeInput.editor.text = "16"
                timeInput.commitValue()
                saveButton(view)!!.fire()
            }

            assertThat(adjustStoryEventsTimeController.requestedStoryEventIds).isEqualTo(expectedStoryEventIds)
            assertThat(adjustStoryEventsTimeController.requestedAdjustmentTime).isEqualTo(16L)
        }

        @Nested
        inner class `When Failed` {

            @Test
            fun `inputs should be enabled`() {
                rescheduleStoryEventDialogView()
                interact {
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
                rescheduleStoryEventDialogView()
                interact {
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
            rescheduleStoryEventDialogView()
            interact {
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