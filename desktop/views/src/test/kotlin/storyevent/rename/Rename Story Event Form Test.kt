package com.soyle.stories.desktop.view.storyevent.rename

import com.soyle.stories.desktop.view.runHeadless
import com.soyle.stories.desktop.view.storyevent.rename.`Rename Story Event Dialog Access`.Companion.access
import com.soyle.stories.desktop.view.storyevent.rename.`Rename Story Event Dialog Access`.Companion.drive
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.rename.RenameStoryEventController
import com.soyle.stories.storyevent.rename.RenameStoryEventForm
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat

class `Rename Story Event Form Test` : FxRobot() {

    init {
        runHeadless()
    }

    private val primaryStage = FxToolkit.registerPrimaryStage()

    private val storyEventId = StoryEvent.Id()
    private val currentName = "Current Story Event Name"

    private var requestedStoryEvent: StoryEvent.Id? = null
    private var requestedName: String? = null
    private val renameStoryEventController = object : RenameStoryEventController {
        var failWith: Throwable? = null
        val completion = Job()

        override fun renameStoryEvent(storyEventId: StoryEvent.Id, newName: NonBlankString): Job {
            requestedStoryEvent = storyEventId
            requestedName = newName.value
            return CoroutineScope(Dispatchers.Main).launch(
                CoroutineExceptionHandler { coroutineContext, throwable ->
                    coroutineContext.cancel(CancellationException("", throwable))
                }
            ) {
                failWith?.let { throw it }
                completion.join()
            }
        }
    }

    private val form = RenameStoryEventForm(storyEventId, currentName, renameStoryEventController)

    @Test
    fun `name input should contain current name`() {
        assertThat(form.access().nameInput).hasText(currentName)
    }

    @Test
    fun `submit button should be disabled`() {
        assertThat(form.access().submitButton).isDisabled
    }

    @Test
    fun `cancel should always be available`() {
        assertThat(form.access().cancelButton).isEnabled
    }

    @Nested
    inner class `When Cancelled` {

        private var cancelled = false

        init {
            form.onCancelled { cancelled = true }
        }

        @Test
        fun `should notify listener of cancellation`() {
            form.drive { cancelButton.fire() }
            assertTrue(cancelled)
        }

    }

    @Nested
    inner class `When Name is Modified` {

        @Test
        fun `submit button should be enabled if not empty`() {
            form.drive { nameInput.text = "New Name" }
            assertThat(form.access().submitButton).isEnabled
        }

        @Test
        fun `submit button should be disabled if empty`() {
            form.drive { nameInput.text = "" }
            assertThat(form.access().submitButton).isDisabled
        }

        @Test
        fun `submit button should be disabled if name returns to current name`() {
            form.drive { nameInput.text = "New Name" }
            form.drive { nameInput.text = currentName }
            assertThat(form.access().submitButton).isDisabled
        }

    }

    @Nested
    inner class `When Submitted` {

        init {
            form.drive { nameInput.text = "New Name" }
        }

        @Test
        fun `all inputs and buttons should be disabled`() {
            form.drive { submitButton.fire() }
            assertThat(form.access().nameInput).isDisabled
            assertThat(form.access().cancelButton).isDisabled
            assertThat(form.access().submitButton).isDisabled
        }

        @Test
        fun `should request rename with input values`() {
            form.drive { submitButton.fire() }
            assertThat(requestedStoryEvent).isEqualTo(storyEventId)
            assertThat(requestedName).isEqualTo("New Name")
        }

    }

    @Nested
    inner class `When Submission Fails` {

        var completed = false

        init {
            form.onCompleted { completed = true }
            renameStoryEventController.failWith = Exception("Intended Exception")
            form.drive { nameInput.text = "New Name" }
        }

        @Test
        fun `all inputs and buttons should be enabled again`() {
            form.drive { submitButton.fire() }
            assertThat(form.access().nameInput).isEnabled
            assertThat(form.access().cancelButton).isEnabled
            assertThat(form.access().submitButton).isEnabled
        }

        @Test
        fun `should not notify about completion`() {
            form.drive { submitButton.fire() }
            assertFalse(completed)
        }

    }

    @Nested
    inner class `When Submission Succeeds` {

        var completed = false

        init {
            form.onCompleted { completed = true }
            form.drive {
                nameInput.text = "New Name"
                submitButton.fire()
            }
        }

        @Test
        fun `should notify listener of completion`() {
            renameStoryEventController.completion.complete()
            runBlocking(Dispatchers.Main) {
                assertTrue(completed)
            }
        }

    }

}