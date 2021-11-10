package com.soyle.stories.desktop.view.storyevent.time

import com.soyle.stories.storyevent.time.adjust.AdjustTimePromptViewModel
import com.soyle.stories.storyevent.time.reschedule.ReschedulePromptViewModel
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.testfx.assertions.api.Assertions.assertThat

class `Time Adjustment Prompt ViewModel Test` {

    @Nested
    inner class `Given Created to Adjust Time` {

        val viewModel = AdjustTimePromptViewModel()

        @Test
        fun `time adjustment should equal zero`() {
            assertThat(viewModel.timeText().value).isEqualTo("")
            assertThat(viewModel.time).isEqualTo(0L)
        }

        @Test
        fun `when initial adjustment is provided, should display initial value`() {
            val viewModel = AdjustTimePromptViewModel()
            viewModel.time = 9L
            assertThat(viewModel.timeText().value).isEqualTo("9")
        }

        @Nested
        inner class `Rule - Should only be Valid when Time is a Number Besides Zero` {

            @ParameterizedTest
            @ValueSource(strings = ["   ", "banana", "0"])
            fun `should not be able to submit change`(timeValue: String) {
                viewModel.timeText().set(timeValue)

                assertThat(viewModel.canSubmit).isFalse
            }

            @Test
            fun `when a valid number - should be able to submit change`() {
                viewModel.timeText().set("14")

                assertThat(viewModel.canSubmit).isTrue
            }

            @Test
            fun `when created with an initial value - should be able to submit change`() {
                val viewModel = AdjustTimePromptViewModel()
                viewModel.time = 9L

                assertThat(viewModel.canSubmit).isTrue
            }

        }

        @Nested
        inner class `Given Change Can be Submitted` {

            init {
                viewModel.timeText().set("14")
            }

            @Test
            fun `when change is submitted - should not be able to submit change`() {
                viewModel.submit()

                assertThat(viewModel.isSubmitting).isTrue
                assertThat(viewModel.canSubmit).isFalse
            }

        }

        @Nested
        inner class `Given Change has been Submitted` {

            init {
                viewModel.timeText().set("14")
                viewModel.submit()
            }

            @Test
            fun `when change failed - should be able to submit change`() {
                viewModel.endSubmission()

                assertThat(viewModel.canSubmit).isTrue
            }

        }

    }

    @Nested
    inner class `Given Created to Reschedule` {

        private val currentTime = 9L
        val viewModel = ReschedulePromptViewModel(currentTime)

        @Test
        fun `time should be equal to the current time`() {
            assertThat(viewModel.timeText().value).isEqualTo("9")
        }

        @Nested
        inner class `Rule - Should only be Valid when Time is a Different Number` {

            @ParameterizedTest
            @ValueSource(strings = ["   ", "banana", "9"])
            fun `should not be able to submit change`(timeValue: String) {
                viewModel.timeText().set(timeValue)

                assertThat(viewModel.canSubmit).isFalse
            }

            @Test
            fun `when a different number - should be able to submit change`() {
                viewModel.timeText().set("14")

                assertThat(viewModel.canSubmit).isTrue
            }

        }

        @Nested
        inner class `Given Change Can be Submitted` {

            init {
                viewModel.timeText().set("14")
            }

            @Test
            fun `when change is submitted - should not be able to submit change`() {
                viewModel.submit()

                assertThat(viewModel.canSubmit).isFalse
            }

        }

        @Nested
        inner class `Given Change has been Submitted` {

            init {
                viewModel.timeText().set("14")
                viewModel.submit()
            }

            @Test
            fun `when change failed - should be able to submit change`() {
                viewModel.endSubmission()

                assertThat(viewModel.canSubmit).isTrue
            }

        }

    }

}