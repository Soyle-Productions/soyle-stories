package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.common.Notifier
import com.soyle.stories.desktop.adapter.storyevent.list.ListStoryEventsControllerDouble
import com.soyle.stories.desktop.view.storyevent.item.StoryEventItemMenuComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.header.TimelineHeaderComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.header.TimelineHeaderCreateButtonComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.TimelineViewPortComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelComponentDouble
import com.soyle.stories.domain.project.Project
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.create.StoryEventCreatedNotifier
import com.soyle.stories.storyevent.create.StoryEventCreatedReceiver
import com.soyle.stories.storyevent.item.StoryEventItemMenuComponent
import com.soyle.stories.storyevent.list.ListStoryEventsController
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensNotifier
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensReceiver
import com.soyle.stories.storyevent.timeline.Timeline
import com.soyle.stories.storyevent.timeline.TimelineComponent
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderComponent
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPortComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlin.coroutines.CoroutineContext

class TimelineComponentDouble(
    private val projectId: Project.Id = Project.Id(),
    val dependencies: TimelineComponent.Dependencies = Dependencies(),
    val viewPortDependencies: TimelineViewPortComponent.Dependencies = TimelineViewPortComponentDouble.Dependencies(),
    val createButtonDependencies: CreateStoryEventController = TimelineHeaderCreateButtonComponentDouble.Dependencies(),
    val storyEventItemMenuDependencies: StoryEventItemMenuComponent.Dependencies = StoryEventItemMenuComponentDouble.Dependencies()
) : TimelineComponent {

    private val gui = object : TimelineComponent.GUI,
        TimelineViewPortComponent by TimelineViewPortComponentDouble(
            viewPortDependencies
        ),
        TimelineHeaderComponent by TimelineHeaderComponentDouble(
            createButtonDependencies,
            storyEventItemMenuDependencies
        ),
        StoryPointLabelComponent by StoryPointLabelComponentDouble(),
        StoryEventItemMenuComponent by StoryEventItemMenuComponentDouble(
            storyEventItemMenuDependencies
        ) {}

    class Dependencies(
        override val storyEventCreated: Notifier<StoryEventCreatedReceiver> = StoryEventCreatedNotifier(),
        override val listStoryEventsController: ListStoryEventsController = ListStoryEventsControllerDouble(),
        override val storyEventNoLongerHappens: Notifier<StoryEventNoLongerHappensReceiver> = StoryEventNoLongerHappensNotifier(),
        override val guiContext: CoroutineContext = Dispatchers.JavaFx
    ) : TimelineComponent.Dependencies

    val timeline by lazy {
        TimelineComponent.Implementation(projectId, gui, dependencies).Timeline()
    }

    override fun Timeline(): Timeline {
        return timeline
    }

}