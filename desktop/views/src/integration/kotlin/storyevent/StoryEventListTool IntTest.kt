package storyevent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.components.surfaces.Surface.Companion.surface
import com.soyle.stories.desktop.adapter.storyevent.RenameStoryEventControllerDouble
import com.soyle.stories.desktop.adapter.storyevent.create.CreateStoryEventControllerDouble
import com.soyle.stories.desktop.adapter.storyevent.list.ListStoryEventsControllerDouble
import com.soyle.stories.desktop.view.runHeadless
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.access
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.storyevent.create.StoryEventCreatedNotifier
import com.soyle.stories.storyevent.list.*
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensNotifier
import com.soyle.stories.storyevent.rename.StoryEventRenamedNotifier
import com.soyle.stories.storyevent.time.StoryEventRescheduledNotifier
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventControllerDouble
import com.soyle.stories.usecase.storyevent.StoryEventItem
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ListCell
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat

class `StoryEventListTool IntTest` : FxRobot() {

    @Nested
    inner class `Should Keep Selection when Story Events Renamed` {

        @Test
        fun `Reschedule Single Selected Item`() {
            val storyEventItem = `given a story event has been created`()
            val view = `given story event list has been initialized`()
            view.`given story event has been selected`(storyEventItem.storyEventId)
            view.`given a request to reschedule the story event has been made`()
            `when the story event item is rescheduled`(storyEventItem.storyEventId, storyEventItem.time)
            view.`then the story event item should still be selected`(storyEventItem.storyEventId)
        }

        @Test
        fun `Reschedule the same selected item twice`() {
            val storyEventItem = `given a story event has been created`()
            val view = `given story event list has been initialized`()
            view.`given story event has been selected`(storyEventItem.storyEventId)
            view.`given a request to reschedule the story event has been made`()
            `when the story event item is rescheduled`(storyEventItem.storyEventId, storyEventItem.time)
            view.`given a request to adjust the story event has been made`()
            `when the story event item is adjusted`(storyEventItem.storyEventId, storyEventItem.time+5)
            view.`then the story event item should still be selected`(storyEventItem.storyEventId)
        }

    }

    // Step Definitions

    private fun `given a story event has been created`(): StoryEventItem {
        return if (listStoryEventsController.storyEvents.isEmpty()) {
            StoryEventItem(StoryEvent.Id(), "Some Name", 7).also(listStoryEventsController.storyEvents::add)
        } else listStoryEventsController.storyEvents.first()
    }

    private val presenter by lazy {
        StoryEventListPresenter(
            projectId = Project.Id(),
            createStoryEventController = mockk(relaxed = true),
            listStoryEventsController = listStoryEventsController,
            renameStoryEventController = mockk(relaxed = true),
            rescheduleStoryEventController = mockk(relaxed = true),
            adjustStoryEventsTimeController = mockk(relaxed = true),
            removeStoryEventController = mockk(relaxed = true),
            requestToViewStoryEventInTimeline = mockk(relaxed = true),
            storyEventCreated = StoryEventCreatedNotifier(),
            storyEventRenamed = StoryEventRenamedNotifier(),
            storyEventRescheduled = storyEventRescheduledNotifier,
            storyEventNoLongerHappens = StoryEventNoLongerHappensNotifier(),
            threadTransformer = threadTransformer
        )
    }

    private fun `given story event list has been initialized`(): StoryEventListToolView {
        val view = StoryEventListToolView(presenter, presenter.viewModel, object : StoryEventListCell {
            override fun invoke(): ListCell<StoryEventListItemViewModel> = object : ListCell<StoryEventListItemViewModel>() {
                override fun updateItem(item: StoryEventListItemViewModel?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty || item == null) {
                        graphic = null
                        text = null
                    } else {
                        graphic = StoryEventListItemView(item)
                    }
                }
            }
        })

        interact {
            primaryStage.scene = Scene(view)
        }

        return view
    }

    private fun StoryEventListToolView.`given story event has been selected`(id: StoryEvent.Id) {
        interact {
            access().storyEventList?.selectionModel?.select(access().storyEventItems.indexOfFirst { it.id == id })
        }
    }

    private fun StoryEventListToolView.`given a request to reschedule the story event has been made`() {
        interact {
            access().optionsButton?.items?.find { it.id == "reschedule" }?.fire()
        }
    }

    private fun StoryEventListToolView.`given a request to adjust the story event has been made`() {
        interact {
            access().optionsButton?.items?.find { it.id == "adjust" }?.fire()
        }
    }

    private fun `when the story event item is rescheduled`(id: StoryEvent.Id, currentTime: Long) {
        runBlocking {
            storyEventRescheduledNotifier.receiveStoryEventsRescheduled(
                listOf(StoryEventRescheduled(id, currentTime + 5, currentTime)).associateBy { it.storyEventId }
            )
        }
    }

    private fun `when the story event item is adjusted`(id: StoryEvent.Id, currentTime: Long) {
        runBlocking {
            storyEventRescheduledNotifier.receiveStoryEventsRescheduled(
                listOf(StoryEventRescheduled(id, currentTime - 5, currentTime)).associateBy { it.storyEventId }
            )
        }
    }

    private fun StoryEventListToolView.`then the story event item should still be selected`(id: StoryEvent.Id) {
        assertThat(access().storyEventList?.selectionModel?.selectedItems?.find { it.id == id }).isNotNull
        assertThat((presenter.viewModel.value as PopulatedStoryEventListViewModel).hasSingleSelection.value).isTrue
    }

    init {
        runHeadless()
    }

    private val primaryStage = FxToolkit.registerPrimaryStage()
    private val listStoryEventsController = object : ListStoryEventsController {
        var storyEvents = mutableListOf<StoryEventItem>()
        override fun listStoryEventsInProject(projectId: Project.Id, output: ListAllStoryEvents.OutputPort): Job {
            return CoroutineScope(Dispatchers.JavaFx).launch {
                output.receiveListAllStoryEventsResponse(ListAllStoryEvents.ResponseModel(storyEvents))
            }
        }
    }

    private val rescheduleStoryEventController = RescheduleStoryEventControllerDouble()

    private val storyEventRescheduledNotifier = StoryEventRescheduledNotifier()

    private val threadTransformer: ThreadTransformer = mockk(relaxed = true)

}