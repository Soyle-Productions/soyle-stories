package com.soyle.stories.storyevent.list

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.create.StoryEventCreatedReceiver
import com.soyle.stories.storyevent.remove.RemoveStoryEventController
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensReceiver
import com.soyle.stories.storyevent.rename.RenameStoryEventController
import com.soyle.stories.storyevent.rename.StoryEventRenamedReceiver
import com.soyle.stories.storyevent.time.StoryEventRescheduledReceiver
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import com.soyle.stories.theme.themeOppositionWebs.Styles.Companion.selectedItem
import com.soyle.stories.usecase.storyevent.StoryEventItem
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import javafx.beans.property.ObjectProperty
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import tornadofx.objectProperty
import tornadofx.observableListOf
import tornadofx.toObservable
import java.util.*
import java.util.concurrent.atomic.AtomicReferenceArray
import kotlin.coroutines.coroutineContext

class StoryEventListPresenter(
    private val projectId: Project.Id,

    private val createStoryEventController: CreateStoryEventController,
    private val listStoryEventsController: ListStoryEventsController,
    private val renameStoryEventController: RenameStoryEventController,
    private val rescheduleStoryEventController: RescheduleStoryEventController,
    private val adjustStoryEventsTimeController: AdjustStoryEventsTimeController,
    private val removeStoryEventController: RemoveStoryEventController,
    private val requestToViewStoryEventInTimeline: (StoryEvent.Id) -> Unit,

    storyEventCreated: Notifier<StoryEventCreatedReceiver>,
    storyEventRenamed: Notifier<StoryEventRenamedReceiver>,
    storyEventRescheduled: Notifier<StoryEventRescheduledReceiver>,
    storyEventNoLongerHappens: Notifier<StoryEventNoLongerHappensReceiver>,

    private val threadTransformer: ThreadTransformer
) : StoryEventListViewActions {

    val viewModel: ObjectProperty<StoryEventListViewModel> = objectProperty(null)

    override fun loadStoryEvents() {
        viewModel.set(LoadingStoryEventListViewModel)
        var outputReceived = false
        listStoryEventsController.listStoryEventsInProject(projectId) {
            withContext(Dispatchers.JavaFx) {
                outputReceived = true

                val newVM = if (it.storyEventItems.isEmpty()) EmptyStoryEventListViewModel
                else {
                    PopulatedStoryEventListViewModel(it.storyEventItems
                        .asSequence()
                        .map { it.toViewModel() }
                        .orderAndGroupByTime()
                        .toObservable()
                    )
                }
                viewModel.set(newVM)
            }
        }.invokeOnCompletion {
            threadTransformer.gui {
                if (it != null && !outputReceived) viewModel.set(
                    FailedStoryEventListViewModel
                )
            }
        }
    }

    private fun createStoryEventViewModel(
        id: StoryEvent.Id,
        name: String,
        time: Long
    ): StoryEventListItemViewModel {
        return StoryEventListItemViewModel(id).apply {
            nameProperty.set(name)
            timeProperty.set(time)
        }
    }

    private fun StoryEventItem.toViewModel() = createStoryEventViewModel(storyEventId, storyEventName, time)
    private fun StoryEventCreated.toViewModel() = createStoryEventViewModel(storyEventId, name, time)

    private fun Sequence<StoryEventListItemViewModel>.orderAndGroupByTime(): List<StoryEventListItemViewModel> {
        val seenIds = mutableSetOf<StoryEvent.Id>()
        return sortedBy { it.timeProperty.value }
            .windowed(2, 1, true) {
                val first = it[0]
                if (first.id !in seenIds) {
                    first.prevItemHasSameTime.set(true)
                    seenIds.add(first.id)
                }
                val second = it.getOrNull(1)
                if (second != null) {
                    second.prevItemHasSameTime.set(first.timeProperty.value == second.timeProperty.value)
                    seenIds.add(second.id)
                }
                first
            }
            .toList()
    }

    override fun createStoryEvent() {
        createStoryEventController.requestToCreateStoryEvent()
    }

    override fun insertStoryEventBeforeSelectedItem() = insertStoryEvent(-1)
    override fun insertStoryEventAtSameTimeAsSelectedItem() = insertStoryEvent(0)
    override fun insertStoryEventAfterSelectedItem() = insertStoryEvent(1)

    private fun insertStoryEvent(delta: Long) {
        selectedItem {
            createStoryEventController.requestToCreateStoryEvent(
                CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative(it.id, delta)
            )
        }
    }

    override fun renameSelectedItem() {
        selectedItem {
            renameStoryEventController.requestToRenameStoryEvent(it.id, it.nameProperty.value)
        }
    }

    override fun rescheduleSelectedItem() {
        selectedItem {
            rescheduleStoryEventController.requestToRescheduleStoryEvent(
                it.id,
                it.timeProperty.value
            )
        }
    }

    private inline fun selectedItem(op: (StoryEventListItemViewModel) -> Unit) {
        val selectedItem = (viewModel.value as PopulatedStoryEventListViewModel).selectedItems.singleOrNull() ?: return
        op(selectedItem)
    }

    override fun adjustTimesOfSelectedItems() {
        val selectedItems = (viewModel.value as PopulatedStoryEventListViewModel).selectedItems
        adjustStoryEventsTimeController.requestToAdjustStoryEventsTimes(selectedItems.map { it.id }.toSet())
    }

    override fun deleteSelectedItems() {
        val selectedItems = (viewModel.value as PopulatedStoryEventListViewModel).selectedItems
        removeStoryEventController.removeStoryEvent(selectedItems.map { it.id }.toSet())
    }

    override fun viewSelectedItemInTimeline() {
        val selectedItem = (viewModel.value as PopulatedStoryEventListViewModel).selectedItems.singleOrNull() ?: return
        requestToViewStoryEventInTimeline(selectedItem.id)
    }

    private val eventReceiver = EventReceiver()

    init {
        storyEventCreated.addListener(eventReceiver)
        storyEventRenamed.addListener(eventReceiver)
        storyEventRescheduled.addListener(eventReceiver)
        storyEventNoLongerHappens.addListener(eventReceiver)

        loadStoryEvents()
    }

    private inner class EventReceiver : StoryEventCreatedReceiver, StoryEventRenamedReceiver,
        StoryEventRescheduledReceiver, StoryEventNoLongerHappensReceiver {

        override suspend fun receiveStoryEventCreated(event: StoryEventCreated) {
            withContext(Dispatchers.JavaFx) {
                val vm = viewModel.value as? LoadedStoryEventListViewModel ?: return@withContext
                val newItem = event.toViewModel()
                when (vm) {
                    EmptyStoryEventListViewModel -> viewModel.set(PopulatedStoryEventListViewModel(observableListOf(
                        newItem.apply { prevItemHasSameTime.set(true) }
                    )))
                    is PopulatedStoryEventListViewModel -> {
                        vm.items.setAll(
                            vm.items.asSequence().plus(newItem).orderAndGroupByTime()
                        )
                    }
                }
            }
        }

        override suspend fun receiveStoryEventRenamed(event: StoryEventRenamed) {
            withContext(Dispatchers.JavaFx) {
                val vm = viewModel.value as? PopulatedStoryEventListViewModel ?: return@withContext
                vm.items.find { it.id == event.storyEventId }?.nameProperty?.set(event.newName)
            }
        }

        override suspend fun receiveStoryEventsRescheduled(events: List<StoryEventRescheduled>) {
            withContext(Dispatchers.JavaFx) {
                val vm = viewModel.value as? PopulatedStoryEventListViewModel ?: return@withContext
                val eventsById = events.associateBy { it.storyEventId }

                vm.items.onEach {
                    val rescheduled = eventsById[it.id] ?: return@onEach
                    it.timeProperty.set(rescheduled.newTime)
                }
                vm.items.sortWith { item1, item2 ->
                    (item1.timeProperty.value - item2.timeProperty.value).toInt()
                }
                val seenIds = mutableSetOf<StoryEvent.Id>()
                vm.items.windowed(2) {
                    val first = it[0]
                    if (first.id !in seenIds) {
                        first.prevItemHasSameTime.set(true)
                        seenIds.add(first.id)
                    }
                    val second = it.getOrNull(1)
                    if (second != null) {
                        second.prevItemHasSameTime.set(first.timeProperty.value == second.timeProperty.value)
                        seenIds.add(second.id)
                    }
                }

            }
        }

        private val eventQueue = mutableSetOf<StoryEvent.Id>()
        private val mutex = Mutex()
        override suspend fun receiveStoryEventNoLongerHappens(event: StoryEventNoLongerHappens) {
            mutex.withLock {
                eventQueue.add(event.storyEventId)
            }
            withContext(Dispatchers.JavaFx) {
                val ids = mutex.withLock {
                    eventQueue.toSet().also {
                        eventQueue.clear()
                    }
                }
                if (ids.isEmpty()) return@withContext
                val vm = viewModel.value as? PopulatedStoryEventListViewModel ?: return@withContext
                val newItems = vm.items.asSequence().filterNot { it.id in ids }.orderAndGroupByTime()
                if (newItems.isEmpty()) viewModel.set(EmptyStoryEventListViewModel)
                else vm.items.setAll(newItems)
            }
        }
    }

}