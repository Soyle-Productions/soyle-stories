package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.makeStoryPointLabel
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponentDouble
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.timeline.TimeRange
import com.soyle.stories.storyevent.timeline.TimelineSelectionModel
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.unit
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.usecase.storyevent.StoryEventItem
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat

class `Timeline Ruler Label Menu Unit Test` {

    private val dependencies = TimelineRulerLabelMenuComponentDouble.Dependencies(spyk())
    private val component = TimelineRulerLabelMenuComponentDouble(dependencies)
    private val selection = TimelineSelectionModel()
    private val storyPointLabels = mutableListOf<StoryPointLabel>()
    private val menu = component.TimelineRulerLabelMenu(selection, storyPointLabels)

    @Nested
    inner class `Length of selection determines available items` {

        init {
            selection.timeRange.restart(UnitOfTime(4))
        }

        val itemsById = menu.items.associateBy { it.id }

        @Test
        fun `should be able to insert time`() {
            assertThat(itemsById.getValue("insert-before").text).isEqualTo("Insert 1 unit of time before")
            assertThat(itemsById.getValue("insert-after").text).isEqualTo("Insert 1 unit of time after")
        }

        @Test
        fun `should be able to remove time`() {
            assertThat(itemsById.getValue("delete").text).isEqualTo("Remove 1 unit of time")
        }

    }

    @Nested
    inner class `Insert time before` {

        private val storyEventItems = listOf(
            StoryEventItem(StoryEvent.Id(), "", 2), // <- should not be included
            StoryEventItem(StoryEvent.Id(), "", 3),
            StoryEventItem(StoryEvent.Id(), "", 6),
            StoryEventItem(StoryEvent.Id(), "", 8),
            StoryEventItem(StoryEvent.Id(), "", 12),
        )

        init {
            selection.restart(TimeRange(3 .. 8L))

            storyPointLabels.addAll(storyEventItems.map {
                storyPointLabelComponent.makeStoryPointLabel(it.storyEventId, it.storyEventName, it.time.unit)
            })
        }

        @Test
        fun `should request to adjust time of all story events after start`() {
            menu.access().insertBeforeOption!!.fire()

            verify {
                dependencies.adjustStoryEventsTimeController.requestToAdjustStoryEventsTimes(
                    storyEventItems.drop(1).map { it.storyEventId }.toSet(),
                    5L
                )
            }
        }

    }

    @Nested
    inner class `Insert time after` {

        private val storyEventItems = listOf(
            StoryEventItem(StoryEvent.Id(), "", 2), // <- should not be included
            StoryEventItem(StoryEvent.Id(), "", 3), // <- should not be included
            StoryEventItem(StoryEvent.Id(), "", 6), // <- should not be included
            StoryEventItem(StoryEvent.Id(), "", 8),
            StoryEventItem(StoryEvent.Id(), "", 12),
        )

        init {
            selection.restart(TimeRange(3 .. 8L))

            storyPointLabels.addAll(storyEventItems.map {
                storyPointLabelComponent.makeStoryPointLabel(it.storyEventId, it.storyEventName, it.time.unit)
            })
        }

        @Test
        fun `should request to adjust time of all story events after end`() {
            menu.access().insertAfterOption!!.fire()

            verify {
                dependencies.adjustStoryEventsTimeController.requestToAdjustStoryEventsTimes(
                    storyEventItems.drop(3).map { it.storyEventId }.toSet(),
                    5L
                )
            }
        }

    }

    @Nested
    inner class `Remove time` {

        private val storyEventItems = listOf(
            StoryEventItem(StoryEvent.Id(), "", 2), // <- should not be included
            StoryEventItem(StoryEvent.Id(), "", 3),
            StoryEventItem(StoryEvent.Id(), "", 6),
            StoryEventItem(StoryEvent.Id(), "", 8),
            StoryEventItem(StoryEvent.Id(), "", 12),
        )

        init {
            selection.restart(TimeRange(3 .. 8L))

            storyPointLabels.addAll(storyEventItems.map {
                storyPointLabelComponent.makeStoryPointLabel(it.storyEventId, it.storyEventName, it.time.unit)
            })
        }

        @Test
        fun `should request to adjust time of all story events after start`() {
            menu.access().removeTimeOption!!.fire()

            verify {
                dependencies.adjustStoryEventsTimeController.requestToAdjustStoryEventsTimes(
                    storyEventItems.drop(1).map { it.storyEventId }.toSet(),
                    -5L
                )
            }
        }

    }

    companion object {
        init {
            FxToolkit.registerPrimaryStage()
        }
    }

    private val storyPointLabelComponent = StoryPointLabelComponentDouble()

}