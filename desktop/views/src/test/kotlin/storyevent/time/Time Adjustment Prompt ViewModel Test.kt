package com.soyle.stories.desktop.view.storyevent.time

import com.soyle.stories.storyevent.time.TimeAdjustmentPromptViewModel
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.testfx.assertions.api.Assertions.assertThat

class `Time Adjustment Prompt ViewModel Test` {

    @Nested
    inner class `Given Created to Adjust Time` {

        val viewModel = TimeAdjustmentPromptViewModel()

        @Test
        fun `time adjustment should be empty`() {
            assertThat(viewModel.time.value).isBlank
        }

        @Nested
        inner class `Rule - Should only be Valid when Time is a Number Besides Zero` {

            @ParameterizedTest
            @ValueSource(strings = ["   ", "banana", "0"])
            fun `should not be able to submit change`(timeValue: String) {
                viewModel.time.set(timeValue)

                assertThat(viewModel.canSubmit.value).isFalse
            }

            @Test
            fun `when a valid number - should be able to submit change`() {
                viewModel.time.set("14")

                assertThat(viewModel.canSubmit.value).isTrue
            }

        }

        @Nested
        inner class `Given Change Can be Submitted` {

            init {
                viewModel.time.set("14")
            }

            @Test
            fun `when change is submitted - should not be able to submit change`() {
                viewModel.submitting()

                assertThat(viewModel.submitting.value).isTrue
                assertThat(viewModel.canSubmit.value).isFalse
            }

        }

        @Nested
        inner class `Given Change has been Submitted` {

            init {
                viewModel.time.set("14")
                viewModel.submitting()
            }

            @Test
            fun `when change failed - should be able to submit change`() {
                viewModel.failed()

                assertThat(viewModel.canSubmit.value).isTrue
                assertThat(viewModel.isCompleted.value).isFalse
            }

            @Test
            fun `when change succeeded - should be completed`() {
                viewModel.success()

                assertThat(viewModel.canSubmit.value).isTrue
                assertThat(viewModel.isCompleted.value).isTrue
            }

        }

    }

    @Nested
    inner class `Given Created to Reschedule` {

        private val currentTime = 9L
        val viewModel = TimeAdjustmentPromptViewModel(currentTime)

        @Test
        fun `time should be equal to the current time`() {
            assertThat(viewModel.time.value).isEqualTo("9")
        }

        @Nested
        inner class `Rule - Should only be Valid when Time is a Different Number` {

            @ParameterizedTest
            @ValueSource(strings = ["   ", "banana", "9"])
            fun `should not be able to submit change`(timeValue: String) {
                viewModel.time.set(timeValue)

                assertThat(viewModel.canSubmit.value).isFalse
            }

            @Test
            fun `when a different number - should be able to submit change`() {
                viewModel.time.set("14")

                assertThat(viewModel.canSubmit.value).isTrue
            }

        }

        @Nested
        inner class `Given Time is Valid` {

            init {
                viewModel.time.set("14")
            }

        }

        @Nested
        inner class `Given Change Can be Submitted` {

            init {
                viewModel.time.set("14")
            }

            @Test
            fun `when change is submitted - should not be able to submit change`() {
                viewModel.submitting()

                assertThat(viewModel.canSubmit.value).isFalse
            }

        }

        @Nested
        inner class `Given Change has been Submitted` {

            init {
                viewModel.time.set("14")
                viewModel.submitting()
            }

            @Test
            fun `when change failed - should be able to submit change`() {
                viewModel.failed()

                assertThat(viewModel.canSubmit.value).isTrue
            }

            @Test
            fun `when change succeeded - should be completed`() {
                viewModel.success()

                assertThat(viewModel.isCompleted.value).isTrue
            }

        }

    }

}