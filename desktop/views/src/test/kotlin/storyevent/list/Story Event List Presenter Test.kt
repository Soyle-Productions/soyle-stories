package com.soyle.stories.desktop.view.storyevent.list

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.adapter.storyevent.list.ListStoryEventsControllerDouble
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.drive
import com.soyle.stories.desktop.view.storyevent.list.`Story Event List Tool Assertions`.Companion.assertThis
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.create.StoryEventCreatedNotifier
import com.soyle.stories.storyevent.list.*
import com.soyle.stories.storyevent.remove.RemoveStoryEventController
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensNotifier
import com.soyle.stories.storyevent.rename.RenameStoryEventController
import com.soyle.stories.storyevent.rename.RenameStoryEventDialog
import com.soyle.stories.storyevent.rename.StoryEventRenamedNotifier
import com.soyle.stories.storyevent.time.StoryEventRescheduledNotifier
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import com.soyle.stories.theme.themeOppositionWebs.Styles.Companion.selectedItem
import com.soyle.stories.usecase.storyevent.StoryEventItem
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import javafx.beans.binding.When
import javafx.scene.Parent
import javafx.scene.layout.Pane
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.testfx.assertions.api.Assertions
import org.testfx.assertions.api.Assertions.assertThat
import org.testfx.util.WaitForAsyncUtils
import tornadofx.UIComponent
import tornadofx.objectProperty
import tornadofx.onChange
import java.util.concurrent.atomic.AtomicInteger

class `Story Event List Presenter Test` {

    companion object {

        private val expectedFailure = Exception("Intentional Failure")

    }

    private val projectId = Project.Id()

    private val listStoryEventsController = object : ListStoryEventsController {
        var requestedProjectId: Project.Id? = null
        var requestedOutput: ListAllStoryEvents.OutputPort? = null
        var job: CompletableJob = Job()

        override fun listStoryEventsInProject(projectId: Project.Id, output: ListAllStoryEvents.OutputPort): Job {
            requestedProjectId = projectId
            requestedOutput = output
            job = Job()
            return job
        }
    }

    private val createStoryEventController = object : CreateStoryEventController {
        var requestWasMade = false
        var requestedRelative: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative? = null

        override fun requestToCreateStoryEvent() {
            requestWasMade = true
        }

        override fun requestToCreateStoryEvent(relativeTo: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative) {
            requestWasMade = true
            requestedRelative = relativeTo
        }

        override fun createStoryEvent(name: NonBlankString): Job {
            fail("Should not be called by story event list")
        }

        override fun createStoryEvent(name: NonBlankString, timeUnit: Long): Job {
            fail("Should not be called by story event list")
        }

        override fun createStoryEvent(
            name: NonBlankString,
            relativeTo: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative
        ): Job {
            fail("Should not be called by story event list")
        }
    }

    private val storyEventCreatedNotifier = StoryEventCreatedNotifier()

    private val renameStoryEventController = object : RenameStoryEventController {
        var requestedId: StoryEvent.Id? = null
        var requestedName: String? = null
        override fun requestToRenameStoryEvent(storyEventId: StoryEvent.Id, currentName: String) {
            requestedId = storyEventId
            requestedName = currentName
        }

        override fun renameStoryEvent(storyEventId: StoryEvent.Id, newName: NonBlankString): Job {
            fail("Should not be called by story event list")
        }
    }

    private val storyEventRenamedNotifier = StoryEventRenamedNotifier()

    private val rescheduleStoryEventController = object : RescheduleStoryEventController {
        var requestedId: StoryEvent.Id? = null
        var requestedTime: Long? = null
        override fun requestToRescheduleStoryEvent(storyEventId: StoryEvent.Id, currentTime: Long) {
            requestedId = storyEventId
            requestedTime = currentTime
        }

        override fun rescheduleStoryEvent(storyEventId: StoryEvent.Id, time: Long): Job {
            fail("Should not be called by story event list")
        }
    }

    private val adjustStoryEventsTimeController = object : AdjustStoryEventsTimeController {
        var requestedIds: Set<StoryEvent.Id>? = null
        override fun requestToAdjustStoryEventsTimes(storyEventIds: Set<StoryEvent.Id>) {
            requestedIds = storyEventIds
        }

        override fun adjustStoryEventsTime(storyEventIds: Set<StoryEvent.Id>, amount: Long): Job {
            fail("Should not be called by story event list")
        }
    }

    private val storyEventRescheduledNotifier = StoryEventRescheduledNotifier()

    private val removeStoryEventController = object : RemoveStoryEventController {
        var requestedStoryEventIds: Set<StoryEvent.Id>? = null
        override fun removeStoryEvent(storyEventIds: Set<StoryEvent.Id>) {
            requestedStoryEventIds = storyEventIds
        }

        override fun confirmRemoveStoryEvent(storyEventIds: Set<StoryEvent.Id>): Job =
            error("Should never be called from story event list")
    }

    private val storyEventRemovedNotifier = StoryEventNoLongerHappensNotifier()

    private val presenter =
        StoryEventListPresenter(
            projectId,
            createStoryEventController,
            listStoryEventsController,
            renameStoryEventController,
            rescheduleStoryEventController,
            adjustStoryEventsTimeController,
            removeStoryEventController,
            storyEventCreatedNotifier,
            storyEventRenamedNotifier,
            storyEventRescheduledNotifier,
            storyEventRemovedNotifier,
            object : ThreadTransformer {
                override fun async(task: suspend CoroutineScope.() -> Unit): Job {
                    TODO("Not yet implemented")
                }
                override fun gui(update: suspend CoroutineScope.() -> Unit) {
                    CoroutineScope(Dispatchers.JavaFx).launch { update() }
                }
            }
        )

    private val viewModel
        get() = presenter.viewModel.value

    @Test
    fun `should be loading story events when first created`() {
        Assertions.assertThat(listStoryEventsController.requestedProjectId).isEqualTo(projectId)
        assertThat(viewModel).isEqualTo(LoadingStoryEventListViewModel)
    }

    @Nested
    inner class `When Loading Fails` {

        init {
            listStoryEventsController.job.completeExceptionally(expectedFailure)
        }

        @Test
        fun `should be in failed state`() {
            listStoryEventsController.job.completeExceptionally(expectedFailure)
            runBlocking { Dispatchers.JavaFx.get(Job)?.join() }

            assertThat(viewModel).isEqualTo(FailedStoryEventListViewModel)
        }

    }

    @Nested
    inner class `When Loading Succeeds` {

        fun `given controller responded with`(items: List<StoryEventItem>) {
            runBlocking {
                listStoryEventsController.requestedOutput!!.receiveListAllStoryEventsResponse(
                    ListAllStoryEvents.ResponseModel(items)
                )
            }
        }

        @Test
        fun `should not be in failed state if story events were received before failure`() {
            `given controller responded with`(listOf())
            `When Loading Fails`()

            assertThat(viewModel).isNotEqualTo(FailedStoryEventListViewModel)
        }

        @Nested
        inner class `And Response is Empty` {

            init {
                `given controller responded with`(emptyList())
                listStoryEventsController.job.complete()
            }

            @Test
            fun `should be in empty state`() {
                assertThat(viewModel).isEqualTo(EmptyStoryEventListViewModel)
            }

        }

        @Nested
        inner class `And Not Empty` {

            init {
                `given controller responded with`(
                    listOf(
                        StoryEventItem(StoryEvent.Id(), "Story Event 3", 3),
                        StoryEventItem(StoryEvent.Id(), "Story Event 2", 2),
                        StoryEventItem(StoryEvent.Id(), "Story Event 4", 4),
                        StoryEventItem(StoryEvent.Id(), "Story Event 3.1", 3),
                        StoryEventItem(StoryEvent.Id(), "Story Event 1", 1),
                    )
                )
                listStoryEventsController.job.complete()
            }

            @Test
            fun `should be populated`() {
                assertThat(viewModel).isInstanceOf(PopulatedStoryEventListViewModel::class.java)
            }

            @Test
            fun `should contain all returned items in time order`() {
                with(viewModel as PopulatedStoryEventListViewModel) {
                    assertThat(items.size).isEqualTo(5)
                    assertThat(items.map { it.nameProperty.value }).isEqualTo(
                        listOf(
                            "Story Event 1",
                            "Story Event 2",
                            "Story Event 3",
                            "Story Event 3.1",
                            "Story Event 4"
                        )
                    )
                    assertThat(items.map { it.timeProperty.value }).isEqualTo(
                        listOf(
                            1L, 2L, 3L, 3L, 4L
                        )
                    )
                }
            }

            @Test
            fun `items with the same time should be marked as such`() {
                with(viewModel as PopulatedStoryEventListViewModel) {
                    assertThat(items.map { it.prevItemHasSameTime.value }).isEqualTo(
                        listOf(
                            true, false, false, true, false
                        )
                    )
                }
            }

        }

    }

    @Nested
    inner class `When Creating a New Story Event` {

        init {
            `When Loading Succeeds`().`given controller responded with`(listOf())
        }

        @Test
        fun `should request create story event dialog without relative placement`() {
            presenter.createStoryEvent()

            assertThat(createStoryEventController.requestWasMade).isTrue()
            assertNull(createStoryEventController.requestedRelative)
        }

    }

    @Nested
    inner class `Given Story Event Selected` {

        val selectedItem: StoryEventListItemViewModel

        init {
            `When Loading Succeeds`().`given controller responded with`(
                List(5) {
                    StoryEventItem(StoryEvent.Id(), "name $it", it.toLong())
                }
            )
            selectedItem = with(viewModel as PopulatedStoryEventListViewModel) {
                items.random().also(selectedItems::add)
            }
        }

        @Nested
        inner class `When Inserting a New Story Event` {

            @Test
            fun `before - should request create story event dialog relative to selected item`() {
                presenter.insertStoryEventBeforeSelectedItem()

                assertThat(createStoryEventController.requestWasMade).isTrue()
                with(createStoryEventController.requestedRelative!!) {
                    assertThat(relativeStoryEventId).isEqualTo(selectedItem.id)
                    assertThat(delta).isEqualTo(-1L)
                }
            }

            @Test
            fun `after - should request create story event dialog relative to selected item`() {
                presenter.insertStoryEventAfterSelectedItem()

                assertThat(createStoryEventController.requestWasMade).isTrue()
                with(createStoryEventController.requestedRelative!!) {
                    assertThat(relativeStoryEventId).isEqualTo(selectedItem.id)
                    assertThat(delta).isEqualTo(1L)
                }
            }

            @Test
            fun `at the same time - should request create story event dialog relative to selected item`() {
                presenter.insertStoryEventAtSameTimeAsSelectedItem()

                assertThat(createStoryEventController.requestWasMade).isTrue()
                with(createStoryEventController.requestedRelative!!) {
                    assertThat(relativeStoryEventId).isEqualTo(selectedItem.id)
                    assertThat(delta).isEqualTo(0L)
                }
            }

        }

        @Nested
        inner class `When Renaming a Story Event` {

            @Test
            fun `should request to rename story event`() {
                presenter.renameSelectedItem()

                assertThat(renameStoryEventController.requestedId).isEqualTo(selectedItem.id)
                assertThat(renameStoryEventController.requestedName).isEqualTo(selectedItem.nameProperty.value)
            }

        }

        @Nested
        inner class `When Rescheduling a Story Event` {

            @Test
            fun `should request to reschedule the selected story event`() {
                presenter.rescheduleSelectedItem()

                assertThat(rescheduleStoryEventController.requestedId).isEqualTo(selectedItem.id)
                assertThat(rescheduleStoryEventController.requestedTime).isEqualTo(selectedItem.timeProperty.value)
            }

        }

    }

    @Nested
    inner class `Given Multiple Story Events have been Selected` {

        val selectedItems: List<StoryEventListItemViewModel>

        init {
            `When Loading Succeeds`().`given controller responded with`(
                List(5) {
                    StoryEventItem(StoryEvent.Id(), "name $it", it.toLong())
                }
            )
            selectedItems = with(viewModel as PopulatedStoryEventListViewModel) {
                items.shuffled().take(3).onEach(selectedItems::add)
            }
        }

        @Test
        fun `cannot rename multiple story events`() {
            presenter.renameSelectedItem()

            assertThat(renameStoryEventController.requestedId).isNull()
        }

        @Test
        fun `cannot reschedule multiple story events`() {
            presenter.rescheduleSelectedItem()

            assertThat(rescheduleStoryEventController.requestedId).isNull()
        }

        @Test
        fun `cannot insert new story event relative to multiple story events`() {
            presenter.insertStoryEventBeforeSelectedItem()
            assertThat(createStoryEventController.requestWasMade).isFalse()

            presenter.insertStoryEventAtSameTimeAsSelectedItem()
            assertThat(createStoryEventController.requestWasMade).isFalse()

            presenter.insertStoryEventAfterSelectedItem()
            assertThat(createStoryEventController.requestWasMade).isFalse()
        }

        @Nested
        inner class `When Adjusting Times` {

            @Test
            fun `should request to adjust the times of all selected items`() {
                presenter.adjustTimesOfSelectedItems()

                assertThat(adjustStoryEventsTimeController.requestedIds)
                    .containsExactlyInAnyOrderElementsOf(selectedItems.map { it.id })
            }

        }

        @Nested
        inner class `When Deleting Story Events` {

            @Test
            fun `should request to delete all of the selected items`() {
                presenter.deleteSelectedItems()

                assertThat(removeStoryEventController.requestedStoryEventIds)
                    .containsExactlyInAnyOrderElementsOf(selectedItems.map { it.id })
            }

        }

    }

    @Nested
    inner class `When New Story Event is created` {

        private val newStoryEvent: StoryEvent
        private val storyEventCreated: StoryEventCreated

        init {
            StoryEvent.create(NonBlankString.create(("Story Event 3.1"))!!, 3L, projectId).also {
                newStoryEvent = it.storyEvent
                storyEventCreated = (it as Successful).change
            }
        }

        fun storyEventCreated() {
            runBlocking {
                storyEventCreatedNotifier.receiveStoryEventCreated(storyEventCreated)
            }
        }

        @Nested
        inner class `While Empty` {

            init {
                `When Loading Succeeds`().`given controller responded with`(listOf())
            }

            @Test
            fun `should show new story event`() {
                storyEventCreated()

                with(viewModel as PopulatedStoryEventListViewModel) {
                    val item = items.single()
                    assertThat(item.id).isEqualTo(newStoryEvent.id)
                    assertThat(item.nameProperty.value).isEqualTo(newStoryEvent.name.value)
                    assertThat(item.timeProperty.value).isEqualTo(newStoryEvent.time)
                    assertThat(item.prevItemHasSameTime.value).isTrue()
                }
            }

        }

        @Nested
        inner class `While Populated` {

            init {
                `When Loading Succeeds`().`given controller responded with`(
                    listOf(
                        StoryEventItem(StoryEvent.Id(), "Story Event 3", 3),
                        StoryEventItem(StoryEvent.Id(), "Story Event 2", 2),
                        StoryEventItem(StoryEvent.Id(), "Story Event 4", 4),
                        StoryEventItem(StoryEvent.Id(), "Story Event 1", 1),
                    )
                )
            }

            @Test
            fun `should display new story event`() {
                storyEventCreated()

                with(viewModel as PopulatedStoryEventListViewModel) {
                    assertThat(items.size).isEqualTo(5)
                    assertThat(items.map { it.nameProperty.value }).isEqualTo(
                        listOf(
                            "Story Event 1",
                            "Story Event 2",
                            "Story Event 3",
                            "Story Event 3.1",
                            "Story Event 4"
                        )
                    )
                    assertThat(items.map { it.timeProperty.value }).isEqualTo(
                        listOf(
                            1L, 2L, 3L, 3L, 4L
                        )
                    )
                    assertThat(items.map { it.prevItemHasSameTime.value }).isEqualTo(
                        listOf(
                            true, false, false, true, false
                        )
                    )
                }
            }

            @Test
            fun `should only update items once`() {
                var itemModCount = 0
                (viewModel as PopulatedStoryEventListViewModel).items.onChange {
                    itemModCount++
                }

                storyEventCreated()

                assertThat(itemModCount).isEqualTo(1)
            }

        }


    }


    @Nested
    inner class `When Story Event is Renamed` {

        private val renamedStoryEventId = StoryEvent.Id()
        private val newName = "Some new story event name"

        private val items = List(5) {
            StoryEventItem(StoryEvent.Id(), "name $it", it.toLong())
        } + StoryEventItem(renamedStoryEventId, "Some name", 0)

        init {
            `When Loading Succeeds`().`given controller responded with`(items)
        }

        @Test
        fun `should rename corresponding story event item`() {
            runBlocking {
                storyEventRenamedNotifier.receiveStoryEventRenamed(StoryEventRenamed(renamedStoryEventId, newName))
            }

            with(viewModel as PopulatedStoryEventListViewModel) {
                assertThat(items.single { it.id == renamedStoryEventId }.nameProperty.value).isEqualTo(newName)
            }
        }

    }

    @Nested
    inner class `When Story Events are Rescheduled` {

        private val rescheduledStoryEventIds = List(7) { StoryEvent.Id() }
        private val newTimes = rescheduledStoryEventIds.associate { it to (0L..5L).random() }

        private val items = (List(5) {
            StoryEventItem(StoryEvent.Id(), "name $it", it.toLong())
        } + rescheduledStoryEventIds.mapIndexed { index, id ->
            StoryEventItem(id, "Some name $index", 10L + index)
        }).shuffled()

        init {
            `When Loading Succeeds`().`given controller responded with`(items)
        }

        @Test
        fun `should update the times of corresponding story event items`() {
            runBlocking {
                storyEventRescheduledNotifier.receiveStoryEventsRescheduled(newTimes.map { StoryEventRescheduled(it.key, it.value, 0L) })
            }

            with(viewModel as PopulatedStoryEventListViewModel) {
                newTimes.forEach { (id, newTime) ->
                    val item = items.single { it.id == id }
                    assertThat(item.timeProperty.value).isEqualTo(newTime)
                }
            }
        }

        @Test
        fun `should update the order of items`() {
            runBlocking {
                storyEventRescheduledNotifier.receiveStoryEventsRescheduled(newTimes.map { StoryEventRescheduled(it.key, it.value, 0L) })
            }

            with(viewModel as PopulatedStoryEventListViewModel) {
                assertThat(items.toList()).containsExactly(*items.sortedBy { it.timeProperty.value }.toTypedArray())
            }
        }

    }

    @Nested
    inner class `When Story Events are Removed` {

        private val removedStoryEvents = listOf(
            StoryEventItem(StoryEvent.Id(), "Some name 0", 4L),
            StoryEventItem(StoryEvent.Id(), "Some name 1", 4L),
            StoryEventItem(StoryEvent.Id(), "Some name 2", 4L),
            StoryEventItem(StoryEvent.Id(), "Some name 3", 2L),
            StoryEventItem(StoryEvent.Id(), "Some name 4", 1L),
            StoryEventItem(StoryEvent.Id(), "Some name 5", 5L),
            StoryEventItem(StoryEvent.Id(), "Some name 6", 2L),
        )

        private val items = removedStoryEvents + listOf(
            StoryEventItem(StoryEvent.Id(), "name 0", 0L),
            StoryEventItem(StoryEvent.Id(), "name 1", 1L),
            StoryEventItem(StoryEvent.Id(), "name 2", 2L),
            StoryEventItem(StoryEvent.Id(), "name 3", 3L),
            StoryEventItem(StoryEvent.Id(), "name 4", 4L),
        )

        /*
        Ordered Events:
            StoryEventItem(StoryEvent.Id(), "name 0", 0L),
            StoryEventItem(StoryEvent.Id(), "Some name 4", 1L),
            StoryEventItem(StoryEvent.Id(), "name 1", 1L),
            StoryEventItem(StoryEvent.Id(), "Some name 3", 2L),
            StoryEventItem(StoryEvent.Id(), "Some name 6", 2L),
            StoryEventItem(StoryEvent.Id(), "name 2", 2L),
            StoryEventItem(StoryEvent.Id(), "name 3", 3L),
            StoryEventItem(StoryEvent.Id(), "Some name 0", 4L),
            StoryEventItem(StoryEvent.Id(), "Some name 1", 4L),
            StoryEventItem(StoryEvent.Id(), "Some name 2", 4L),
            StoryEventItem(StoryEvent.Id(), "Some name 5", 5L),
            StoryEventItem(StoryEvent.Id(), "name 4", 4L),

        After Deletion:
            StoryEventItem(StoryEvent.Id(), "name 0", 0L),
            StoryEventItem(StoryEvent.Id(), "name 1", 1L),
            StoryEventItem(StoryEvent.Id(), "name 2", 2L),
            StoryEventItem(StoryEvent.Id(), "name 3", 3L),
            StoryEventItem(StoryEvent.Id(), "name 4", 4L),

         */

        init {
            items.forEach {
                println("StoryEventItem(StoryEvent.Id(), \"${it.storyEventName}\", ${it.time}L),")
            }

            `When Loading Succeeds`().`given controller responded with`(items)
        }

        @Test
        fun `should remove corresponding story event items`() {
            runBlocking {
                removedStoryEvents.forEach {
                    storyEventRemovedNotifier.receiveStoryEventNoLongerHappens(StoryEventNoLongerHappens(it.storyEventId))
                }
            }

            with(viewModel as PopulatedStoryEventListViewModel) {
                removedStoryEvents.forEach { storyEvent ->
                    assertThat(items.find { it.id == storyEvent.storyEventId }).isNull()
                }
            }
        }

        @Test
        fun `should update prev time match for each item`() {
            runBlocking {
                removedStoryEvents.forEach {
                    storyEventRemovedNotifier.receiveStoryEventNoLongerHappens(StoryEventNoLongerHappens(it.storyEventId))
                }
            }

            with(viewModel as PopulatedStoryEventListViewModel) {
                assertThat(items.map { it.prevItemHasSameTime.value }).isEqualTo(listOf(
                    true, false, false, false, false
                ))
            }
        }

        @Test
        fun `should only update items once`() {
            val updateCount = AtomicInteger(0)
            (viewModel as PopulatedStoryEventListViewModel).items.onChange {
                updateCount.getAndIncrement()
            }
            val processesCompletedCount = AtomicInteger(0)
            val send = CoroutineScope(Dispatchers.Main).launch {
                removedStoryEvents.map {
                    launch {
                        storyEventRemovedNotifier.receiveStoryEventNoLongerHappens(StoryEventNoLongerHappens(it.storyEventId))
                        processesCompletedCount.getAndIncrement()
                    }
                }.joinAll()
            }
            runBlocking {
                send.join()
            }

            assertThat(processesCompletedCount.get()).isEqualTo(7)
            assertThat(updateCount.get()).isEqualTo(1)
        }

        @Test
        fun `when all items are removed - should be empty`() {
            runBlocking {
                items.forEach {
                    storyEventRemovedNotifier.receiveStoryEventNoLongerHappens(StoryEventNoLongerHappens(it.storyEventId))
                }
            }

            assertThat(viewModel).isEqualTo(EmptyStoryEventListViewModel)
        }

    }

}