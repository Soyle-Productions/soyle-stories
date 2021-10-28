package storyevent.timeline.label

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.makeStoryPointLabel
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.storyevent.rename.StoryEventRenamedNotifier
import com.soyle.stories.storyevent.time.StoryEventRescheduledNotifier
import com.soyle.stories.storyevent.timeline.UnitOfTime
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat

class `Story Point Label Dependency Int Test` {

    private val component = StoryPointLabelComponentDouble()
    private val storyEventRenamedNotifier = component.dependencies.storyEventRenamed as StoryEventRenamedNotifier
    private val storyEventRescheduledNotifier = component.dependencies.storyEventRescheduled as StoryEventRescheduledNotifier

    @Nested
    inner class `Label should watch for name changes` {

        private val label = component.makeStoryPointLabel(name = "original name")
        private val storyEventId = label.storyEventId

        @Test
        fun `should change text when event matches`(): Unit = runBlocking {
            storyEventRenamedNotifier.receiveStoryEventRenamed(StoryEventRenamed(storyEventId, "A new name"))
            assertThat(label.text).isEqualTo("A new name")
        }

        @Test
        fun `should not change text when event does not matches`(): Unit = runBlocking {
            storyEventRenamedNotifier.receiveStoryEventRenamed(StoryEventRenamed(StoryEvent.Id(), "A new name"))
            assertThat(label.text).isEqualTo("original name")
        }

        @Test
        fun `should continue listening for changes as long as label is in memory`() {
            repeat(10) {
                System.gc()
                val nextName = "Another $it"
                runBlocking {
                    storyEventRenamedNotifier.receiveStoryEventRenamed(StoryEventRenamed(storyEventId, nextName))
                    assertThat(label.text).isEqualTo(nextName)
                }
            }
        }

    }

    @Nested
    inner class `Label should watch for time changes` {

        private val label = component.makeStoryPointLabel(time = UnitOfTime(4))
        private val storyEventId = label.storyEventId

        @Test
        fun `should change time when one event matches`(): Unit = runBlocking {
            storyEventRescheduledNotifier.receiveStoryEventsRescheduled(listOf(
                StoryEventRescheduled(StoryEvent.Id(), 5L, 4L),
                StoryEventRescheduled(StoryEvent.Id(), 6L, 4L),
                StoryEventRescheduled(storyEventId, 8L, 4L),
                StoryEventRescheduled(StoryEvent.Id(), 7L, 4L),
                StoryEventRescheduled(StoryEvent.Id(), 3L, 4L)
            ).associateBy { it.storyEventId })
            assertThat(label.time).isEqualTo(8L)
        }

        @Test
        fun `should not change time when events do not match`(): Unit = runBlocking {
            storyEventRescheduledNotifier.receiveStoryEventsRescheduled(listOf(
                StoryEventRescheduled(StoryEvent.Id(), 8L, 4L)
            ).associateBy { it.storyEventId })
            assertThat(label.time).isEqualTo(4L)
        }

    }

    companion object {
        init {
            FxToolkit.registerPrimaryStage()
        }
    }

}