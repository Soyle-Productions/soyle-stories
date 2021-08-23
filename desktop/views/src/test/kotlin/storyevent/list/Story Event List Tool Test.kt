package com.soyle.stories.desktop.view.storyevent.list

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.cells
import com.soyle.stories.desktop.adapter.storyevent.create.CreateStoryEventControllerDouble
import com.soyle.stories.desktop.adapter.storyevent.list.ListStoryEventsControllerDouble
import com.soyle.stories.desktop.view.runHeadless
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.drive
import com.soyle.stories.desktop.view.storyevent.list.`Story Event List Tool Assertions`.Companion.assertThis
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.StoryEventUpdate
import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.create.CreateStoryEventForm
import com.soyle.stories.storyevent.create.StoryEventCreatedNotifier
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import com.soyle.stories.storyevent.list.ListStoryEventsController
import com.soyle.stories.storyevent.list.creationButton.StoryEventListTool
import com.soyle.stories.usecase.storyevent.StoryEventItem
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import javafx.application.Application
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.FileChooser
import javafx.stage.Stage
import kotlinx.coroutines.*
import org.assertj.core.internal.bytebuddy.implementation.bytecode.Throw
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import java.util.*
import java.util.concurrent.TimeUnit

@Timeout(10L, unit = TimeUnit.SECONDS)
class `Story Event List Tool Test` : FxRobot() {

    init {
        runHeadless()
    }

    private val primaryStage = FxToolkit.registerPrimaryStage()
    init {
        interact { primaryStage.show() }
    }

    @AfterEach
    fun `close opened dialogs`() {
        val openDialogs = listTargetWindows().asSequence()
            .filterNot { it == primaryStage }
            .filter { it.scene.root == createStoryEventForm.root }
            .filter { it.isShowing }
        interact { openDialogs.forEach { it.hide() } }
    }

    private val createStoryEventForm = CreateStoryEventForm(null, CreateStoryEventControllerDouble())
    private var createStoryEventFormRelativeRequest: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative? = null
    private val createStoryEventFormFactory = { relativeTo: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative? ->
        createStoryEventFormRelativeRequest = relativeTo
        createStoryEventForm
    }

    private var requestedProjectId: Project.Id? = null
    private var response: ListAllStoryEvents.ResponseModel = ListAllStoryEvents.ResponseModel(emptyList())
    private val listStoryEventsController = ListStoryEventsControllerDouble({ projectId, output ->
        requestedProjectId = projectId
        output.receiveListAllStoryEventsResponse(response)
    })

    private val storyEventCreatedNotifier = StoryEventCreatedNotifier()

    private val projectId = Project.Id()
    private val tool by lazy {
        interact {
            val handler = Thread.currentThread().uncaughtExceptionHandler
            Thread.currentThread().uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { thread, throwable ->
                val temp = listStoryEventsController
                val expected = temp.shouldFailWith
                if (throwable == expected) { /* fine */
                } else handler.uncaughtException(thread, throwable)
            }
        }
        StoryEventListTool(projectId, createStoryEventFormFactory, listStoryEventsController, storyEventCreatedNotifier)
    }

    private fun awaitToolInitialization() {
        tool
        interact {
            primaryStage.scene = Scene(tool.root as Parent)
        }
    }

    @Test
    fun `should not open create story event dialog immediately`() {
        awaitToolInitialization()
        listTargetWindows().asSequence()
            .filterNot { it == primaryStage }
            .filter { it.scene.root == createStoryEventForm.root }
            .none { it.isShowing }
            .let(::assertTrue)
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
                listStoryEventsController.failWhenCalled()
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
                        listStoryEventsController.failWhenCalled()
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
                                UUID.fromString("33a14d76-dab1-4277-87c8-7d46b13a178d"),
                                "e4448d63-ad47-43ae-9911-12255ad5ace9",
                                0
                            ),
                            StoryEventItem(
                                UUID.fromString("6c3d08f7-a46f-4d1d-9013-d8c45364d7f5"),
                                "70856ee7-16d0-408c-a2a6-9fec24813924",
                                0
                            ),
                            StoryEventItem(
                                UUID.fromString("254a75e5-4cd4-4257-a24d-c733101946f6"),
                                "8df48034-9b46-4a1b-9b90-6d466000b491",
                                0
                            ),
                            StoryEventItem(
                                UUID.fromString("d642b5a2-5bd2-43c2-8cf7-d87c49166d66"),
                                "f6d436cb-5d37-4bf8-9273-29ee75854b1f",
                                0
                            ),
                            StoryEventItem(
                                UUID.fromString("d7f12ca3-d0ba-4b16-a5e2-b315d6b28a57"),
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
                    assertThat(tool.access().storyEventItems.map { it.id }).isEqualTo(listOf(
                        "33a14d76-dab1-4277-87c8-7d46b13a178d",
                        "6c3d08f7-a46f-4d1d-9013-d8c45364d7f5",
                        "254a75e5-4cd4-4257-a24d-c733101946f6",
                        "d642b5a2-5bd2-43c2-8cf7-d87c49166d66",
                        "d7f12ca3-d0ba-4b16-a5e2-b315d6b28a57"
                    ))
                    assertThat(tool.access().storyEventListCells.map { it.text }.take(5)).isEqualTo(listOf(
                        "e4448d63-ad47-43ae-9911-12255ad5ace9",
                        "70856ee7-16d0-408c-a2a6-9fec24813924",
                        "8df48034-9b46-4a1b-9b90-6d466000b491",
                        "f6d436cb-5d37-4bf8-9273-29ee75854b1f",
                        "394b8867-b7b5-4c39-8fe7-7b0fa45bf78c"
                    ))
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
            listTargetWindows().asSequence()
                .filterNot { it == primaryStage }
                .filter { it.scene.root == createStoryEventForm.root }
                .single { it.isShowing }
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
            interact {}
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
                    StoryEventItem(UUID.randomUUID(), "name $it", it)
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
                StoryEventItem(UUID.randomUUID(), "name $it", it)
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
            listTargetWindows().asSequence()
                .filterNot { it == primaryStage }
                .filter { it.scene.root == createStoryEventForm.root }
                .single { it.isShowing }

            val relativeRequest = createStoryEventFormRelativeRequest!!

            assertThat(relativeRequest.relativeStoryEventId).isEqualTo(StoryEvent.Id(UUID.fromString(selectedItem.id)))
            when (placement) {
                "before" -> assertThat(relativeRequest.delta).isEqualTo(-1L)
                "after" -> assertThat(relativeRequest.delta).isEqualTo(1L)
                "at-the-same-time-as" -> assertThat(relativeRequest.delta).isEqualTo(0L)
            }
        }

    }

}