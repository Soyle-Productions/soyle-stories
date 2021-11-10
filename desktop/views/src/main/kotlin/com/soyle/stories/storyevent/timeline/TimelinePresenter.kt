package com.soyle.stories.storyevent.timeline

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens
import com.soyle.stories.storyevent.create.StoryEventCreatedReceiver
import com.soyle.stories.storyevent.list.ListStoryEventsController
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensReceiver
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.storyPointLabelMenu
import com.sun.javafx.scene.control.skin.Utils
import javafx.scene.control.ListView
import javafx.scene.control.ScrollToEvent
import javafx.scene.control.TableColumnBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.asObservable

class TimelinePresenter(
    projectId: Project.Id,
    timeline: Timeline,
    private val gui: TimelineComponent.GUI,
    private val dependencies: TimelineComponent.Dependencies
) : Timeline.Presenter(timeline), TimelineComponent.Actions {

    private val guiScope = CoroutineScope(dependencies.guiContext)

    private var loadedPresenter: LoadedPresenter? = null

    override fun focusOn(storyEventId: StoryEvent.Id) {
        loadedPresenter?.focusOn(storyEventId)
    }

    init {
        dependencies.listStoryEventsController.listStoryEventsInProject(projectId) {
            val initialItems = with (gui) {
                it.storyEventItems.map {
                    StoryPointLabel(it.storyEventId, it.storyEventName, UnitOfTime(it.time))
                }
            }
            val loaded = Timeline.State.Loaded(initialItems.asObservable(), gui)
            initialItems.forEach { it.contextMenu = loaded.storyPointLabelMenu }
            loadedPresenter = LoadedPresenter(loaded)
            guiScope.launch {
                stateProperty.set(loaded)
            }
        }
    }

    private inner class LoadedPresenter(
        loaded: Timeline.State.Loaded
    ) : Timeline.State.Loaded.Presenter(loaded) {
        fun focusOn(storyEventId: StoryEvent.Id) {
            val storyEvent = state.storyEventItems
                .find { it.storyEventId == storyEventId } ?: return
            guiScope.launch {
                Utils.executeOnceWhenPropertyIsNonNull(timeline.skinProperty()) {
                    timeline.fireEvent(ScrollToEvent(timeline, timeline, Timeline.SCROLL_TO_LABEl, storyEvent))
                }
            }
        }

        private val eventHandlers = object : StoryEventCreatedReceiver, StoryEventNoLongerHappensReceiver {
            override suspend fun receiveStoryEventCreated(event: StoryEventCreated) {
                withContext(guiScope.coroutineContext) {
                    with(gui) {
                        val newLabel = StoryPointLabel(event.storyEventId, event.name, UnitOfTime(event.time.toLong()))
                        newLabel.contextMenu = loaded.storyPointLabelMenu
                        storyEventItemsProperty.add(newLabel)
                    }
                }
            }

            override suspend fun receiveStoryEventNoLongerHappens(event: StoryEventNoLongerHappens) {
                withContext(guiScope.coroutineContext) {
                    loaded.storyEventItems().removeIf { it.storyEventId == event.storyEventId }
                }
            }
        }

        init {
            dependencies.storyEventCreated.addListener(eventHandlers)
            dependencies.storyEventNoLongerHappens.addListener(eventHandlers)
        }
    }

}