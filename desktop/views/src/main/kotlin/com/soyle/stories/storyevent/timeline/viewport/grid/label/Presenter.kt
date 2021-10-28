package com.soyle.stories.storyevent.timeline.viewport.grid.label

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.storyevent.rename.StoryEventRenamedReceiver
import com.soyle.stories.storyevent.time.StoryEventRescheduledReceiver

class StoryPointLabelPresenter(
    private val label: StoryPointLabel,
    dependencies: StoryPointLabelComponent.Dependencies
) : StoryEventRenamedReceiver, StoryEventRescheduledReceiver {

    override suspend fun receiveStoryEventRenamed(event: StoryEventRenamed) {
        if (event.storyEventId != label.storyEventId) return
        label.text = event.newName
    }

    override suspend fun receiveStoryEventsRescheduled(events: Map<StoryEvent.Id, StoryEventRescheduled>) {
        val relevantEvent = events[label.storyEventId] ?: return
        label.time = relevantEvent.newTime
    }

    init {
        dependencies.storyEventRenamed.addListener(this)
        dependencies.storyEventRescheduled.addListener(this)
    }

}