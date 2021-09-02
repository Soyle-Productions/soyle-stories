package com.soyle.stories.desktop.view.storyevent.remove

import com.soyle.stories.desktop.view.runHeadless
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.remove.RemoveStoryEventConfirmationDialogView
import com.soyle.stories.storyevent.remove.RemoveStoryEventController
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import javafx.scene.control.CheckBox
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import org.testfx.util.WaitForAsyncUtils
import tornadofx.FX
import tornadofx.confirm
import tornadofx.uiComponent

class `Remove Story Event Confirmation Dialog Test` : FxRobot() {

    private val removeStoryEventController = object : RemoveStoryEventController {
        var requestedStoryEvents: Set<StoryEvent.Id>? = null
        lateinit var completableJob: CompletableJob
        override fun removeStoryEvent(storyEventIds: Set<StoryEvent.Id>) =
            error("Should not be called from dialog")

        override fun confirmRemoveStoryEvent(storyEventIds: Set<StoryEvent.Id>): Job {
            requestedStoryEvents = storyEventIds
            completableJob = Job()
            return completableJob
        }
    }

    private val setDialogPreferencesController = object : SetDialogPreferencesController {
        var requestedDialog: String? = null
        var shouldShow: Boolean? = null
        override fun setDialogPreferences(dialog: String, shouldShow: Boolean) {
            requestedDialog = dialog
            this.shouldShow = shouldShow
        }
    }

    // expected ui
    private fun view(): RemoveStoryEventConfirmationDialogView? = listWindows().asSequence()
        .filter { it.isShowing }
        .filter { it != primaryStage }
        .mapNotNull { it.scene.root.uiComponent<RemoveStoryEventConfirmationDialogView>() }
        .firstOrNull()

    // fixtures
    fun removeStoryEventConfirmationDialogView(storyEventIds: Set<StoryEvent.Id> = setOf()) {
        interact {
            RemoveStoryEventConfirmationDialogView(storyEventIds, removeStoryEventController, setDialogPreferencesController)
        }
    }

    @Test
    fun `should open new dialog`() {
        removeStoryEventConfirmationDialogView()

        view()!!
    }

    @Test
    fun `should display checkbox to not show dialog again`() {
        removeStoryEventConfirmationDialogView()

        val checkbox = from(view()!!.root).lookup("#show-again").query<CheckBox>()
        assertThat(checkbox.isSelected).isTrue()
    }

    @Test
    fun `should display confirmation button`() {
        removeStoryEventConfirmationDialogView()

        from(view()!!.root).lookup("#confirm").queryButton()
    }

    @Test
    fun `should display cancel button`() {
        removeStoryEventConfirmationDialogView()

        from(view()!!.root).lookup("#cancel").queryButton()
    }

    @Nested
    inner class `When Cancelled` {

        init {
            removeStoryEventConfirmationDialogView()
        }

        val cancelButton = from(view()!!.root).lookup("#cancel").queryButton()

        @Test
        fun `should close dialog`() {
            interact { cancelButton.fire() }

            assertNull(view())
        }

    }

    @Nested
    inner class `When Confirmed` {

        private val expectedStoryEventIds = List(5) { StoryEvent.Id() }.toSet()

        init {
            removeStoryEventConfirmationDialogView(expectedStoryEventIds)
        }

        val confirmButton = from(view()!!.root).lookup("#confirm").queryButton()
        val showAgainCheckBox = from(view()!!.root).lookup("#show-again").query<CheckBox>()

        @Test
        fun `should send story event ids to controller`() {
            interact { confirmButton.fire() }

            assertThat(removeStoryEventController.requestedStoryEvents).isEqualTo(expectedStoryEventIds)
        }

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun `should send show selection to controller`(shouldShow: Boolean) {
            interact {
                showAgainCheckBox.isSelected = shouldShow
                confirmButton.fire()
            }

            assertThat(setDialogPreferencesController.requestedDialog).isEqualTo(DialogType.DeleteStoryEvent.toString())
            assertThat(setDialogPreferencesController.shouldShow).isEqualTo(shouldShow)
        }

        @Nested
        inner class `When Successful` {

            @Test
            fun `should close dialog`() {
                interact { confirmButton.fire() }
                removeStoryEventController.completableJob.complete()
                WaitForAsyncUtils.waitForFxEvents()

                assertNull(view())
            }

        }

        @Nested
        inner class `When Failed` {

            @Test
            fun `should not close dialog`() {
                interact { confirmButton.fire() }
                removeStoryEventController.completableJob.completeExceptionally(Exception("Expected Failure"))
                WaitForAsyncUtils.waitForFxEvents()

                assertNotNull(view())
            }

        }

    }

    @AfterEach
    fun `close open dialogs`() {
        interact {
            view()?.currentStage?.close()
        }
    }

    companion object {
        private val primaryStage by lazy { FxToolkit.registerPrimaryStage() }

        @JvmStatic
        @BeforeAll
        fun `setup toolkit`() {
            com.soyle.stories.desktop.view.common.runHeadless()
            FX.setPrimaryStage(FX.defaultScope, primaryStage)
        }
    }

}