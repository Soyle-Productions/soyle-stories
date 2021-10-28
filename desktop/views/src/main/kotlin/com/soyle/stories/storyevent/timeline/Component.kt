package com.soyle.stories.storyevent.timeline

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.create.StoryEventCreatedReceiver
import com.soyle.stories.storyevent.item.StoryEventItemMenuComponent
import com.soyle.stories.storyevent.list.ListStoryEventsController
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensReceiver
import com.soyle.stories.storyevent.timeline.header.TimelineHeaderComponent
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPortComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import kotlin.coroutines.CoroutineContext

interface TimelineComponent {

    fun Timeline(): Timeline

    interface GUI : TimelineViewPortComponent, TimelineHeaderComponent, StoryPointLabelComponent, StoryEventItemMenuComponent

    interface Dependencies {
        val listStoryEventsController: ListStoryEventsController
        val storyEventCreated: Notifier<StoryEventCreatedReceiver>
        val storyEventNoLongerHappens: Notifier<StoryEventNoLongerHappensReceiver>
        val guiContext: CoroutineContext
    }

    interface Actions {
        fun focusOn(storyEventId: StoryEvent.Id)
    }

    companion object {
        fun Implementation(
            projectId: Project.Id,
            gui: GUI,
            dependencies: Dependencies
        ): TimelineComponent {
            return object : TimelineComponent {
                private fun actions(timeline: Timeline): Actions {
                    return TimelinePresenter(projectId, timeline, gui, dependencies)
                }

                private fun skin(timeline: Timeline): TimelineSkin {
                    return TimelineSkin(timeline, gui)
                }

                override fun Timeline(): Timeline {
                    return Timeline(::actions, ::skin)
                }
            }
        }
    }

}