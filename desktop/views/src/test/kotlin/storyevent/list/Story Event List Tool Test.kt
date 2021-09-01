package com.soyle.stories.desktop.view.storyevent.list

import com.soyle.stories.desktop.adapter.storyevent.RenameStoryEventControllerDouble
import com.soyle.stories.desktop.adapter.storyevent.list.ListStoryEventsControllerDouble
import com.soyle.stories.desktop.view.runHeadless
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.drive
import com.soyle.stories.desktop.view.storyevent.list.`Story Event List Tool Assertions`.Companion.assertThis
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.create.CreateStoryEventDialog
import com.soyle.stories.storyevent.create.StoryEventCreatedNotifier
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import com.soyle.stories.storyevent.list.StoryEventListToolView
import com.soyle.stories.storyevent.rename.RenameStoryEventDialog
import com.soyle.stories.storyevent.rename.RenameStoryEventDialogView
import com.soyle.stories.storyevent.rename.StoryEventRenamedNotifier
import com.soyle.stories.storyevent.time.RescheduleStoryEventDialog
import com.soyle.stories.storyevent.time.StoryEventRescheduledNotifier
import com.soyle.stories.theme.themeOppositionWebs.Styles.Companion.selectedItem
import com.soyle.stories.usecase.storyevent.StoryEventItem
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import org.testfx.util.WaitForAsyncUtils
import tornadofx.UIComponent
import java.util.*

class `Story Event List Tool Test` : FxRobot() {

    companion object {

        private val expectedFailure = Exception("Intentional Failure")

        lateinit var primaryStage: Stage

        @JvmStatic
        @BeforeAll
        fun `setup toolkit`() {
            runHeadless()
            primaryStage = FxToolkit.registerPrimaryStage()
            WaitForAsyncUtils.waitFor(WaitForAsyncUtils.asyncFx {
                val handler = Thread.currentThread().uncaughtExceptionHandler
                Thread.currentThread().uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { thread, throwable ->
                    if (throwable == expectedFailure) { println("Expected error received: $throwable")
                    } else handler.uncaughtException(thread, throwable)
                }
            })
            WaitForAsyncUtils.waitForFxEvents()
        }
    }

    // given
    private val projectId = Project.Id()

    // setup
    private var createStoryEventProps: CreateStoryEventDialog.Props? = null
    private val createStoryEventDialog = object : CreateStoryEventDialog {
        override fun invoke(props: CreateStoryEventDialog.Props): UIComponent {
            createStoryEventProps = props
            return object : UIComponent() {
                override val root: Parent = Pane()
            }
        }
    }

    private var renameStoryEventDialogProps: RenameStoryEventDialog.Props? = null
    private val renameStoryEventForm by lazy {
        RenameStoryEventDialogView(
            renameStoryEventDialogProps!!,
            RenameStoryEventControllerDouble()
        )
    }
    private val renameStoryEventDialog = RenameStoryEventDialog {
        renameStoryEventDialogProps = it
        object : UIComponent() {
            override val root: Parent = Pane()
        }
    }

    private var rescheduleStoryEventDialogProps: RescheduleStoryEventDialog.Props? = null
    private val rescheduleStoryEventDialog = RescheduleStoryEventDialog {
        rescheduleStoryEventDialogProps = it
    }

    private var requestedProjectId: Project.Id? = null
    private var response: ListAllStoryEvents.ResponseModel = ListAllStoryEvents.ResponseModel(emptyList())
    private val listStoryEventsController = ListStoryEventsControllerDouble({ projectId, output ->
        requestedProjectId = projectId
        output.receiveListAllStoryEventsResponse(response)
    })

    private val storyEventCreatedNotifier = StoryEventCreatedNotifier()
    private val storyEventRenamedNotifier = StoryEventRenamedNotifier()
    private val storyEventRescheduledNotifier = StoryEventRescheduledNotifier()

    // tool under test
    private val tool by lazy {
        StoryEventListToolView(
            projectId,
            createStoryEventDialog,
            renameStoryEventDialog,
            rescheduleStoryEventDialog,
            listStoryEventsController,
            storyEventCreatedNotifier,
            storyEventRenamedNotifier,
            storyEventRescheduledNotifier
        )
    }

    init {
        interact { primaryStage.show() }
    }

    private fun awaitToolInitialization() {
        tool
        interact {
            primaryStage.scene = Scene(tool.root)
        }
    }

    @Nested
    inner class `When First Created` {

        @Test
        fun `should request story events in project`() {
            awaitToolInitialization()
            assertThat(requestedProjectId).isEqualTo(projectId)
        }

        @Nested
        inner class `If failed` {

            init {
                listStoryEventsController.failWhenCalled(expectedFailure)
            }

            @Test
            fun `should allow for retry`() {
                awaitToolInitialization()
                assertThat(tool.access().retryButton)
                    .isNotNull
                    .isVisible
                    .isEnabled
            }

            @Test
            fun `should not be able to create story events`() {
                awaitToolInitialization()
                assertThat(tool.access().createStoryEventButton)
                    .isNull()
            }

            @Test
            fun `should not allow for retry if story events were received`() {
                listStoryEventsController.failAfterResult = true
                awaitToolInitialization()
                assertThat(tool.access().retryButton)
                    .isNull()
            }

            @Nested
            inner class `When Retried` {

                init {
                    awaitToolInitialization()
                    listStoryEventsController.shouldFailWith = null
                    requestedProjectId = null
                }

                @Test
                fun `should not allow for retry while loading`() {
                    tool.drive { retryButton!!.fire() }
                    assertThat(tool.access().retryButton)
                        .isNull()
                }

                @Test
                fun `should send another request`() {
                    tool.drive { retryButton!!.fire() }
                    assertThat(requestedProjectId).isEqualTo(projectId)
                }

                @Nested
                inner class `Another failure` {

                    init {
                        listStoryEventsController.failWhenCalled(expectedFailure)
                    }

                    @Test
                    fun `should allow for retry again`() {
                        tool.drive { retryButton!!.fire() }
                        assertThat(tool.access().retryButton)
                            .isNotNull
                            .isVisible
                            .isEnabled
                    }

                    @Test
                    fun `should not allow for retry if story events were received`() {
                        listStoryEventsController.failAfterResult = true
                        tool.drive { retryButton!!.fire() }
                        assertThat(tool.access().retryButton)
                            .isNull()
                    }

                }

            }

        }

        @Nested
        inner class `If succeeded` {

            @Nested
            inner class `And empty` {

                init {
                    response = ListAllStoryEvents.ResponseModel(listOf())
                }

                @Test
                fun `should not be able to retry`() {
                    awaitToolInitialization()
                    assertThat(tool.access().retryButton)
                        .isNull()
                }

                @Test
                fun `should be able to create story event`() {
                    awaitToolInitialization()
                    assertThat(tool.access().createStoryEventButton)
                        .isNotNull()
                        .isVisible
                        .isEnabled
                }

            }

            @Nested
            inner class `And not empty` {

                init {
                    response = ListAllStoryEvents.ResponseModel(
                        listOf(
                            StoryEventItem(
                                StoryEvent.Id(UUID.fromString("33a14d76-dab1-4277-87c8-7d46b13a178d")),
                                "e4448d63-ad47-43ae-9911-12255ad5ace9",
                                0
                            ),
                            StoryEventItem(
                                StoryEvent.Id(UUID.fromString("6c3d08f7-a46f-4d1d-9013-d8c45364d7f5")),
                                "70856ee7-16d0-408c-a2a6-9fec24813924",
                                0
                            ),
                            StoryEventItem(
                                StoryEvent.Id(UUID.fromString("254a75e5-4cd4-4257-a24d-c733101946f6")),
                                "8df48034-9b46-4a1b-9b90-6d466000b491",
                                0
                            ),
                            StoryEventItem(
                                StoryEvent.Id(UUID.fromString("d642b5a2-5bd2-43c2-8cf7-d87c49166d66")),
                                "f6d436cb-5d37-4bf8-9273-29ee75854b1f",
                                0
                            ),
                            StoryEventItem(
                                StoryEvent.Id(UUID.fromString("d7f12ca3-d0ba-4b16-a5e2-b315d6b28a57")),
                                "394b8867-b7b5-4c39-8fe7-7b0fa45bf78c",
                                0
                            )
                        )
                    )
                }

                @Test
                fun `should not be able to retry`() {
                    awaitToolInitialization()
                    assertThat(tool.access().retryButton)
                        .isNull()
                }

                @Test
                fun `should be able to create story event`() {
                    awaitToolInitialization()
                    assertThat(tool.access().createStoryEventButton)
                        .isNotNull()
                        .isVisible
                        .isEnabled
                }

                @Test
                fun `should show all resulting story events`() {
                    awaitToolInitialization()
                    interact {}
                    assertThat(tool.access().storyEventItems.map { it.id.uuid.toString() }).isEqualTo(
                        listOf(
                            "33a14d76-dab1-4277-87c8-7d46b13a178d",
                            "6c3d08f7-a46f-4d1d-9013-d8c45364d7f5",
                            "254a75e5-4cd4-4257-a24d-c733101946f6",
                            "d642b5a2-5bd2-43c2-8cf7-d87c49166d66",
                            "d7f12ca3-d0ba-4b16-a5e2-b315d6b28a57"
                        )
                    )
                    with (tool.access()) {
                        assertThat(tool.access().storyEventListCells.map { it.nameLabel?.text?.orEmpty() }.take(5)).isEqualTo(
                            listOf(
                                "e4448d63-ad47-43ae-9911-12255ad5ace9",
                                "70856ee7-16d0-408c-a2a6-9fec24813924",
                                "8df48034-9b46-4a1b-9b90-6d466000b491",
                                "f6d436cb-5d37-4bf8-9273-29ee75854b1f",
                                "394b8867-b7b5-4c39-8fe7-7b0fa45bf78c"
                            )
                        )
                    }
                }
            }

        }

    }

    @Nested
    inner class `When Create Story Event Button is Clicked` {

        init {
            awaitToolInitialization()
            interact {
                tool.access().createStoryEventButton!!.fire()
            }
        }

        @Test
        fun `should open create story event dialog`() {
            createStoryEventProps!!
        }

    }

    @Nested
    inner class `When New Story Event is created` {

        private val newStoryEvent: StoryEvent
        private val storyEventCreated: StoryEventCreated

        init {
            StoryEvent.create(NonBlankString.create(("Frank dies"))!!, 0L, projectId).also {
                newStoryEvent = it.storyEvent
                storyEventCreated = (it as Successful).change
            }
            awaitToolInitialization()
        }

        @Test
        fun `should display new story event`() {
            runBlocking {
                storyEventCreatedNotifier.receiveStoryEventCreated(storyEventCreated)
            }
            interact {}
            tool.assertThis { hasStoryEvent(newStoryEvent) }
        }

    }

    @Nested
    inner class `Options Button is Only Available when Story Event is Selected` {

        @Test
        fun `options button should not be visible initially`() {
            assertThat(tool.access().optionsButton)
                .isNull()
        }

        @Nested
        inner class `When Populated` {

            init {
                response = ListAllStoryEvents.ResponseModel(List(5) {
                    StoryEventItem(StoryEvent.Id(), "name $it", it.toLong())
                })
            }

            @Test
            fun `options button should be visible but disabled`() {
                awaitToolInitialization()
                assertThat(tool.access().optionsButton)
                    .isNotNull
                    .isVisible
                    .isDisabled
            }

            @Test
            fun `options button should be enabled when item is selected`() {
                awaitToolInitialization()
                tool.drive {
                    storyEventList!!.selectionModel.select(storyEventItems.random())
                }
                assertThat(tool.access().optionsButton)
                    .isNotNull
                    .isVisible
                    .isEnabled
            }

        }

    }

    @Nested
    inner class `When Insert Story Event Option is Selected` {

        val selectedItem: StoryEventListItemViewModel

        init {
            response = ListAllStoryEvents.ResponseModel(List(5) {
                StoryEventItem(StoryEvent.Id(), "name $it", it.toLong())
            })
            awaitToolInitialization()

            selectedItem = tool.access().storyEventItems.random()
            tool.drive {
                storyEventList!!.selectionModel.select(selectedItem)
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["before", "after", "at-the-same-time-as"])
        fun `should open create story event dialog with relative story event`(placement: String) {
            tool.drive { optionsButton!!.insertNewStoryEventOption(placement)!!.fire() }

            val relativeRequest = createStoryEventProps!!.relativePlacement!!

            assertThat(relativeRequest.relativeStoryEventId).isEqualTo(selectedItem.id)
            when (placement) {
                "before" -> assertThat(relativeRequest.delta).isEqualTo(-1L)
                "after" -> assertThat(relativeRequest.delta).isEqualTo(1L)
                "at-the-same-time-as" -> assertThat(relativeRequest.delta).isEqualTo(0L)
            }
        }

    }

    @Nested
    inner class `When Rename Story Event Option is Selected` {

        val selectedItem: StoryEventListItemViewModel

        init {
            response = ListAllStoryEvents.ResponseModel(List(5) {
                StoryEventItem(StoryEvent.Id(), "name $it", it.toLong())
            })
            awaitToolInitialization()
            selectedItem = tool.access().storyEventItems.random()
            tool.drive {
                storyEventList!!.selectionModel.select(selectedItem)
            }
        }

        @Test
        fun `should create rename story event dialog with relative story event`() {
            tool.drive { optionsButton!!.renameOption!!.fire() }
            assertThat(renameStoryEventDialogProps!!.storyEventId).isEqualTo(selectedItem.id)
            assertThat(renameStoryEventDialogProps!!.currentName).isEqualTo(selectedItem.name)
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
            response = ListAllStoryEvents.ResponseModel(items.shuffled())
        }

        @Test
        fun `should rename corresponding story event cell`() {
            awaitToolInitialization()
            runBlocking {
                storyEventRenamedNotifier.receiveStoryEventRenamed(StoryEventRenamed(renamedStoryEventId, newName))
            }
            WaitForAsyncUtils.waitForFxEvents()

            with(tool.access()) {
                val nameLabel = storyEventListCells.find { it.item?.id == renamedStoryEventId }!!
                    .nameLabel!!
                assertThat(nameLabel).hasText(newName)
            }
        }

    }

    @Nested
    inner class `When Reschedule Story Event Option is Selected` {

        val selectedItem: StoryEventListItemViewModel

        init {
            response = ListAllStoryEvents.ResponseModel(List(5) {
                StoryEventItem(StoryEvent.Id(), "name $it", it.toLong())
            })
            awaitToolInitialization()
            selectedItem = tool.access().storyEventItems.random()
            tool.drive {
                storyEventList!!.selectionModel.select(selectedItem)
            }
        }

        @Test
        fun `reschedule story event option should not be available when multiple items are selected`() {
            tool.drive {
                storyEventItems.shuffled().take(2).forEach {
                    storyEventList!!.selectionModel.select(it)
                }
            }

            with (tool.access()) {
                assertThat(optionsButton!!.rescheduleOption!!.isDisable).isTrue()
            }
        }

        @Test
        fun `should create reschedule story event dialog with relative story event`() {
            tool.drive { optionsButton!!.rescheduleOption!!.fire() }

            with(rescheduleStoryEventDialogProps as RescheduleStoryEventDialog.Reschedule) {
                assertThat(storyEventId).isEqualTo(selectedItem.id)
                assertThat(currentTime).isEqualTo(selectedItem.time)
            }

        }

    }

    @Nested
    inner class `When Adjust Story Event Time Option is Selected` {

        val selectedItems: List<StoryEventListItemViewModel>

        init {
            response = ListAllStoryEvents.ResponseModel(List(5) {
                StoryEventItem(StoryEvent.Id(), "name $it", it.toLong())
            })
            awaitToolInitialization()
            selectedItems = tool.access().storyEventItems.shuffled().take(3)
            tool.drive {
                selectedItems.forEach { storyEventList!!.selectionModel.select(storyEventItems.indexOf(it)) }
            }
        }

        @Test
        fun `should create adjust story events time dialog with relative story event`() {
            tool.drive { optionsButton!!.adjustTimeOption!!.fire() }

            (rescheduleStoryEventDialogProps as RescheduleStoryEventDialog.AdjustTimes).run {
                assertThat(storyEventIds).containsExactlyInAnyOrderElementsOf(selectedItems.map { it.id })
            }
        }

    }

    @Nested
    inner class `When Story Events are Rescheduled` {

        private val rescheduledStoryEventIds = List(7) { StoryEvent.Id() }
        private val newTimes = rescheduledStoryEventIds.associate { it to (20L..40L).random() }

        private val items = List(5) {
            StoryEventItem(StoryEvent.Id(), "name $it", it.toLong())
        } + rescheduledStoryEventIds.mapIndexed { index, id ->
            StoryEventItem(id, "Some name $index", 10L + index)
        }

        init {
            response = ListAllStoryEvents.ResponseModel(items.shuffled())
        }

        @Test
        fun `should reschedule corresponding story event cell`() {
            awaitToolInitialization()
            runBlocking {
                storyEventRescheduledNotifier.receiveStoryEventsRescheduled(newTimes.map { StoryEventRescheduled(it.key, it.value, 0L) })
            }
            WaitForAsyncUtils.waitForFxEvents()

            with(tool.access()) {
                newTimes.forEach { (id, newTime) ->
                    val cell = storyEventListCells.find { it.item?.id == id }!!
                    assertThat(cell.timeLabel).hasText("$newTime")
                }
            }

        }

    }

}