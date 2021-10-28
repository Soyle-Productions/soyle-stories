package com.soyle.stories.storyevent.timeline.viewport.grid.label

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.storyevent.item.icon.StoryEventItemIconComponent
import com.soyle.stories.storyevent.rename.StoryEventRenamedReceiver
import com.soyle.stories.storyevent.time.StoryEventRescheduledReceiver
import com.soyle.stories.storyevent.timeline.UnitOfTime
import javafx.event.EventTarget
import tornadofx.add

@Suppress("FunctionName")
interface StoryPointLabelComponent {

    fun StoryPointLabel(storyEventId: StoryEvent.Id, name: String, time: UnitOfTime): StoryPointLabel

    @ViewBuilder
    fun EventTarget.storyPointLabel(storyEventId: StoryEvent.Id, name: String, time: UnitOfTime) =
        StoryPointLabel(storyEventId, name, time).also { add(it) }

    interface GUIComponents : StoryEventItemIconComponent

    interface Dependencies {
        val storyEventRenamed: Notifier<StoryEventRenamedReceiver>
        val storyEventRescheduled: Notifier<StoryEventRescheduledReceiver>
    }

    companion object {

        fun Implementation(
            guiComponents: GUIComponents,
            dependencies: Dependencies
        ) = object : StoryPointLabelComponent {
            override fun StoryPointLabel(storyEventId: StoryEvent.Id, name: String, time: UnitOfTime): StoryPointLabel {
                val label = StoryPointLabel(storyEventId, guiComponents).apply {
                    text = name
                    this.time = time.value
                }
                label.properties["com.soyle.stories.storyevent.timeline.viewport.grid.label.Presenter"] =
                    StoryPointLabelPresenter(label, dependencies)
                return label
            }
        }

    }

}

