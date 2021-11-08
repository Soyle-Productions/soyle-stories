package com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.storyevent.rename.StoryEventRenamedNotifier
import com.soyle.stories.storyevent.rename.StoryEventRenamedReceiver
import com.soyle.stories.storyevent.time.StoryEventRescheduledNotifier
import com.soyle.stories.storyevent.time.StoryEventRescheduledReceiver
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import javafx.scene.Node
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlin.coroutines.CoroutineContext

class StoryPointLabelComponentDouble(
    val dependencies: StoryPointLabelComponent.Dependencies = Dependencies()
) : StoryPointLabelComponent {

    class Dependencies(
        override val storyEventRenamed: Notifier<StoryEventRenamedReceiver> = StoryEventRenamedNotifier(),
        override val storyEventRescheduled: Notifier<StoryEventRescheduledReceiver> = StoryEventRescheduledNotifier(),
        override val guiContext: CoroutineContext = Dispatchers.JavaFx
    ) : StoryPointLabelComponent.Dependencies

    override fun StoryPointLabel(storyEventId: StoryEvent.Id, name: String, time: UnitOfTime): StoryPointLabel {
        val gui = object : StoryPointLabelComponent.GUIComponents {}
        return StoryPointLabelComponent.Implementation(gui, dependencies)
            .StoryPointLabel(storyEventId, name, time)
    }

}

fun StoryPointLabelComponent.makeStoryPointLabel(
    storyEventId: StoryEvent.Id = StoryEvent.Id(),
    name: String = "Label",
    time: UnitOfTime = UnitOfTime(0)
): StoryPointLabel =
    StoryPointLabel(storyEventId, name, time)