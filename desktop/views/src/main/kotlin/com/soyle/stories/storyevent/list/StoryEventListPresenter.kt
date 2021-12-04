package com.soyle.stories.storyevent.list

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.rootMenu
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.storyevent.coverage.StoryEventCoverageController
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.create.StoryEventCreatedReceiver
import com.soyle.stories.storyevent.remove.RemoveStoryEventController
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensReceiver
import com.soyle.stories.storyevent.rename.RenameStoryEventController
import com.soyle.stories.storyevent.rename.StoryEventRenamedReceiver
import com.soyle.stories.storyevent.time.StoryEventRescheduledReceiver
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import com.soyle.stories.usecase.storyevent.StoryEventItem
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.RadioMenuItem
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import tornadofx.*
import kotlin.collections.sortWith

class StoryEventListPresenter(
    private val projectId: Project.Id,

    private val createStoryEventController: CreateStoryEventController,
    private val listStoryEventsController: ListStoryEventsController,
    private val renameStoryEventController: RenameStoryEventController,
    private val rescheduleStoryEventController: RescheduleStoryEventController,
    private val adjustStoryEventsTimeController: AdjustStoryEventsTimeController,
    private val removeStoryEventController: RemoveStoryEventController,
    private val storyEventCoverageController: StoryEventCoverageController,
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

                val newVM = if (it.isEmpty()) EmptyStoryEventListViewModel
                else {
                    populatedViewModel(it
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
    private fun StoryEventCreated.toViewModel() = createStoryEventViewModel(storyEventId, name, time.toLong())

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
        createStoryEventController.create()
    }

    override fun insertStoryEventBeforeSelectedItem() = insertStoryEvent(createStoryEventController::before)
    override fun insertStoryEventAtSameTimeAsSelectedItem() = insertStoryEvent(createStoryEventController::inPlaceWith)
    override fun insertStoryEventAfterSelectedItem() = insertStoryEvent(createStoryEventController::after)

    private fun insertStoryEvent(ifSelected: (StoryEvent.Id) -> Unit) {
        selectedItem {
            ifSelected(it.id)
        }
    }

    override fun renameSelectedItem() {
        selectedItem {
            renameStoryEventController.requestToRenameStoryEvent(it.id, it.nameProperty.value)
        }
    }

    override fun rescheduleSelectedItem() {
        selectedItem {
            rescheduleStoryEventController.rescheduleStoryEvent(
                it.id
            )
        }
    }

    private inline fun selectedItem(op: (StoryEventListItemViewModel) -> Unit) {
        val selectedItem = (viewModel.value as PopulatedStoryEventListViewModel).selectedItems.singleOrNull() ?: return
        op(selectedItem)
    }

    override fun adjustTimesOfSelectedItems() {
        val selectedItems = (viewModel.value as PopulatedStoryEventListViewModel).selectedItems
        adjustStoryEventsTimeController.adjustTimes(selectedItems.map { it.id }.toSet())
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

    private fun populatedViewModel(items: ObservableList<StoryEventListItemViewModel>): PopulatedStoryEventListViewModel
    {
        return PopulatedStoryEventListViewModel(items).apply {
            requestingScenesToCover().onChange { _ ->
                scenesToCover = null
                if (! isRequestingScenesToCover) return@onChange
                val selectedItem = selectedItems.singleOrNull() ?: return@onChange
                val deferred = CompletableDeferred<Scene.Id?>()
                storyEventCoverageController.modifyStoryEventCoverage(selectedItem.id) { currentScene, sceneItems ->
                    if (! isRequestingScenesToCover) return@modifyStoryEventCoverage null
                    scenesToCover = sceneItems.map {
                        RadioMenuItem(it.sceneName).apply {
                            id = Scene.Id(it.id).toString()
                            isSelected = it.id == currentScene?.uuid
                            action {
                                rootMenu?.hide()
                                if (! deferred.isCompleted) deferred.complete(Scene.Id(it.id)) }
                        }
                    }
                    deferred.await()
                }
            }
        }
    }

    private inner class EventReceiver : StoryEventCreatedReceiver, StoryEventRenamedReceiver,
        StoryEventRescheduledReceiver, StoryEventNoLongerHappensReceiver {

        override suspend fun receiveStoryEventCreated(event: StoryEventCreated) {
            withContext(Dispatchers.JavaFx) {
                val vm = viewModel.value as? LoadedStoryEventListViewModel ?: return@withContext
                val newItem = event.toViewModel()
                when (vm) {
                    EmptyStoryEventListViewModel -> viewModel.set(populatedViewModel(observableListOf(
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

        override suspend fun receiveStoryEventsRescheduled(events: Map<StoryEvent.Id, StoryEventRescheduled>) {
            withContext(Dispatchers.JavaFx) {
                val vm = viewModel.value as? PopulatedStoryEventListViewModel ?: return@withContext

                vm.items.onEach {
                    val rescheduled = events[it.id] ?: return@onEach
                    it.timeProperty.set(rescheduled.newTime.toLong())
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