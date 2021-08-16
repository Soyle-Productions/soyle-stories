package com.soyle.stories.desktop.view.storyevent.create

import com.soyle.stories.desktop.view.runHeadless
import com.soyle.stories.desktop.view.storyevent.create.`Create Story Event Dialog Access`.Companion.access
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.create.CreateStoryEventForm
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import java.util.*

class `Create Story Event Form Test` : FxRobot() {

    init {
        runHeadless()
    }

    private val primaryStage = FxToolkit.registerPrimaryStage()

    private var createStoryEventBehavior: (name: NonBlankString) -> Job = { Job() }
    private val createStoryEventController = object : CreateStoryEventController {
        var requestedName: NonBlankString? = null

        override fun createStoryEvent(name: NonBlankString): Job {
            requestedName = name
            return createStoryEventBehavior(name)
        }
        override fun createStoryEventBefore(name: NonBlankString, relativeStoryEventId: String) = error("Should not be called")
        override fun createStoryEventAfter(name: NonBlankString, relativeStoryEventId: String) = error("Should not be called")
    }
    val view = CreateStoryEventForm(createStoryEventController)

    @Nested
    inner class `Cannot Submit with Empty Inputs` {

        @Test
        fun `submit button should be disabled initially`() {
            assertThat(view.access().submitButton).isDisabled
        }

        @Test
        fun `should enable submit button when name and time inputs are populated`() {
            interact {
                view.access().nameInput.text = "Banana"
                view.access().timeInput.editor.text = "0"
                view.access().timeInput.commitValue()
            }

            assertThat(view.access().submitButton).isEnabled
        }

        @Test
        fun `should not enable submit button when only name input has value`() {
            interact {
                view.access().nameInput.text = "Banana"
            }

            assertThat(view.access().submitButton).isDisabled
        }

        @Test
        fun `should not enable submit button when only time input has value`() {
            interact {
                view.access().timeInput.editor.text = "0"
                view.access().timeInput.commitValue()
            }

            assertThat(view.access().submitButton).isDisabled
        }

        @Test
        fun `should not enable submit button when name input has blank value`() {
            interact {
                view.access().nameInput.text = "    \r"
                view.access().timeInput.editor.text = "0"
                view.access().timeInput.commitValue()
            }

            assertThat(view.access().submitButton).isDisabled
        }

    }

    @Nested
    inner class `Cancelling` {

        @Test
        fun `should clear the entered values`() {
            interact {
                view.access().nameInput.text = "Banana"
                view.access().timeInput.editor.text = "0"
                view.access().timeInput.commitValue()
                view.access().cancelButton.fire()
            }

            assertThat(view.access().nameInput.text).isEmpty()
            assertThat(view.access().timeInput.valueFactory.value).isNull()
        }

        @Test
        fun `should notify subscribers of cancellation`() {
            var notified = false
            view.onCancel { notified = true }
            interact { view.access().cancelButton.fire() }

            assertTrue(notified)
        }

    }

    @Nested
    inner class `Submitting` {

        @Test
        fun `should disable all inputs`() {
            interact {
                view.access().nameInput.text = "Banana"
                view.access().timeInput.editor.text = "0"
                view.access().timeInput.commitValue()
                view.access().submitButton.fire()
            }

            assertThat(view.access().nameInput).isDisabled
            assertThat(view.access().timeInput).isDisabled
        }

        @Test
        fun `should not disable inputs if values have not been entered`() {
            interact {
                view.access().submitButton.fire()
            }

            assertThat(view.access().nameInput).isEnabled
            assertThat(view.access().timeInput).isEnabled
        }

        @Test
        fun `should send input values to controller`() {

            val randomNonBlankName = UUID.randomUUID().toString()

            interact {
                view.access().nameInput.text = randomNonBlankName
                view.access().timeInput.editor.text = "0"
                view.access().timeInput.commitValue()
                view.access().submitButton.fire()
            }

            assertThat(createStoryEventController.requestedName).isEqualTo(randomNonBlankName)
        }

        @Nested
        inner class `When Failed` {

            val handler = CoroutineExceptionHandler { a, b ->

            }

            val job by lazy {
                CoroutineScope(Dispatchers.Default).launch(handler) {
                    error("Intended async error")
                }
            }

            init {
                createStoryEventBehavior = { job }

                interact {
                    view.access().nameInput.text = "Banana"
                    view.access().timeInput.editor.text = "0"
                    view.access().timeInput.commitValue()
                }
            }

            @Test
            fun `should enable inputs`() {
                interact { view.access().submitButton.fire() }
                runBlocking { job.join() }

                assertThat(view.access().nameInput).isEnabled
                assertThat(view.access().timeInput).isEnabled
            }

            @Test
            fun `should not clear input values`() {
                interact { view.access().submitButton.fire() }
                runBlocking { job.join() }

                assertThat(view.access().nameInput.text).isEqualTo("Banana")
                assertThat(view.access().timeInput.valueFactory.value).isEqualTo(0L)
            }


            @Test
            fun `should not notify subscribers of completion`() {
                var notified = false
                view.onCreate { notified = true }
                interact { view.access().submitButton.fire() }
                runBlocking { job.join() }

                assertFalse(notified)
            }

        }

        @Nested
        inner class `When Successful` {

            val job by lazy {
                CoroutineScope(Dispatchers.Default).launch {
                }
            }

            init {
                createStoryEventBehavior = { job }

                interact {
                    view.access().nameInput.text = "Banana"
                    view.access().timeInput.editor.text = "0"
                    view.access().timeInput.commitValue()
                }

            }

            @Test
            fun `should enable inputs`() {
                interact { view.access().submitButton.fire() }
                runBlocking { job.join() }

                assertThat(view.access().nameInput).isEnabled
                assertThat(view.access().timeInput).isEnabled
            }

            @Test
            fun `should clear input values`() {
                interact { view.access().submitButton.fire() }
                runBlocking { job.join() }

                assertThat(view.access().nameInput.text).isEmpty()
                assertThat(view.access().timeInput.valueFactory.value).isNull()
            }

            @Test
            fun `should notify subscribers of completion`() {
                var notified = false
                view.onCreate { notified = true }
                interact { view.access().submitButton.fire() }
                runBlocking { job.join() }

                assertTrue(notified)
            }

        }

    }


}