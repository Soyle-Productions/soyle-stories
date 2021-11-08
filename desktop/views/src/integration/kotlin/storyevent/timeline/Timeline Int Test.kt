package storyevent.timeline

import com.soyle.stories.desktop.adapter.storyevent.RemoveStoryEventControllerDouble
import com.soyle.stories.desktop.adapter.storyevent.list.ListStoryEventsControllerDouble
import com.soyle.stories.desktop.view.runHeadless
import com.soyle.stories.desktop.view.storyevent.timeline.TimelineAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.timeline.TimelineAccess.Companion.drive
import com.soyle.stories.desktop.view.storyevent.timeline.TimelineComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.TimelineViewportAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.makeStoryPointLabel
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.timeline.*
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPort
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPortComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.usecase.storyevent.StoryEventItem
import io.mockk.verify
import javafx.geometry.HorizontalDirection
import javafx.geometry.VerticalDirection
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.ScrollEvent
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.javafx.awaitPulse
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import java.awt.MouseInfo

class `Timeline Int Test` {

    private val listStoryEventsController = ListStoryEventsControllerDouble()
    private val component = TimelineComponentDouble(
        dependencies = TimelineComponentDouble.Dependencies(
            listStoryEventsController = listStoryEventsController
        )
    )

    private val timeline = component.Timeline()
    init {
        FxToolkit.registerStage {
            Stage().apply {
                scene = Scene(timeline)
                width = 700.0
                height = 400.0
                show()
            }
        }
    }

    @Nested
    inner class `the user can scroll from time 0 up to 2^63` {

        @Test
        fun `try to scroll to the negatives`(): Unit = runBlocking {
            listStoryEventsController.`given story events have been loaded`()
            withContext(Dispatchers.JavaFx) { awaitPulse() }
            withContext(Dispatchers.JavaFx) { awaitPulse() }
            val viewPort = timeline.access()
                .viewport!!
            val grid = viewPort
                .access().grid
            FxRobot().apply {
                moveTo(grid)
                scroll(5, HorizontalDirection.LEFT)
            }
            assertThat(viewPort.offsetX).isEqualTo(Pixels(0.0))
        }

        @Test
        fun `try to scroll above max value`(): Unit = runBlocking {
            listStoryEventsController.`given story events have been loaded`()
            withContext(Dispatchers.JavaFx) { awaitPulse() }
            withContext(Dispatchers.JavaFx) { awaitPulse() }
            val viewPort = timeline.access()
                .viewport!!
            val robot = FxRobot()
            robot.interact {
                viewPort.scrollToTime(UnitOfTime(Long.MAX_VALUE))
            }
            val grid = viewPort
                .access().grid
            robot.apply {
                interact {
                    moveTo(grid)
                    scroll(5, HorizontalDirection.RIGHT)
                }
            }
            assertThat(viewPort.offsetX).isEqualTo(Pixels(Long.MAX_VALUE * 48.0))
        }

    }

    @Nested
    inner class `Users can Zoom in and out between 1px and 48px to represent a single unit of time` {

        @Test
        fun `Cannot zoom in passed 48 px`(): Unit = runBlocking {
            listStoryEventsController.`given story events have been loaded`()
            withContext(Dispatchers.JavaFx) { awaitPulse() }
            withContext(Dispatchers.JavaFx) { awaitPulse() }
            val viewPort = timeline.access()
                .viewport!!
            val grid = viewPort.access()
                .grid
            FxRobot().apply {
                interact {
                    moveTo(grid)
                    press(KeyCode.CONTROL)
                    scroll(5, VerticalDirection.UP)
                    release(KeyCode.CONTROL)
                }
            }
            assertThat(viewPort.scale).isEqualTo(Scale.at(48.0).getOrThrow())
        }

        @Test
        fun `Cannot zoom out passed 1 px`(): Unit = runBlocking {
            listStoryEventsController.`given story events have been loaded`()
            withContext(Dispatchers.JavaFx) { awaitPulse() }
            withContext(Dispatchers.JavaFx) { awaitPulse() }
            val viewPort = timeline.access()
                .viewport!!
            val robot = FxRobot()
            object : TimelineViewPort.Presenter(viewPort) {
                init {
                    robot.interact {
                        scaleProperty.set(Scale.maxZoomOut())
                    }
                }
            }
            val grid = viewPort.access()
                .grid
            robot.apply {
                interact {
                    moveTo(grid)
                    press(KeyCode.CONTROL)
                    scroll(5, VerticalDirection.DOWN)
                    release(KeyCode.CONTROL)
                }
            }
        }

    }

    @Nested
    inner class `Users can toggle between condensed and expanded view` {

        @Test
        fun `should initially be in expanded view`(): Unit = runBlocking {
            listStoryEventsController.`given story events have been loaded`(
                *Array(16) { StoryEventItem(StoryEvent.Id(), "Some name", (0 .. 100L).random()) }
            )
            withContext(Dispatchers.JavaFx) { awaitPulse() }
            withContext(Dispatchers.JavaFx) { awaitPulse() }
            val toggle = timeline.access().condensedToggle!!
            assertThat(toggle.isSelected).isFalse
            val labels = timeline.access().viewport!!
                .access().grid.labels
            assertThat(labels).allMatch { ! it.isCollapsed }
        }

        @Test
        fun `can toggle to collapse all the labels`(): Unit = runBlocking {
            listStoryEventsController.`given story events have been loaded`(
                *Array(16) { StoryEventItem(StoryEvent.Id(), "Some name", (0 .. 100L).random()) }
            )
            withContext(Dispatchers.JavaFx) { awaitPulse() }
            withContext(Dispatchers.JavaFx) { awaitPulse() }
            val toggle = timeline.access().condensedToggle!!
            FxRobot().interact { toggle.fire() }

            assertThat(toggle.isSelected).isTrue
            val labels = timeline.access().viewport!!
                .access().grid.labels
            assertThat(labels).allMatch { it.isCollapsed }
        }

    }

    @Nested
    inner class `Can delete selected story events` {

        init {
            runBlocking {
                listStoryEventsController.`given story events have been loaded`(
                    *Array(16) { StoryEventItem(StoryEvent.Id(), "Some name", (0 .. 100L).random()) }
                )
                withContext(Dispatchers.JavaFx) { awaitPulse() }
                withContext(Dispatchers.JavaFx) { awaitPulse() }
            }
        }

        val state = timeline.state as Timeline.State.Loaded

        val selectedItems = state.storyEventItems.shuffled().take(3)

        @ParameterizedTest
        @EnumSource(KeyCode::class, names = ["DELETE", "BACK_SPACE"])
        fun `can use keyboard`(keyCode: KeyCode): Unit = runBlocking {
            FxRobot().apply {
                interact {
                    clickOn(timeline.access().viewport!!)
                }
                interact {
                    selectedItems.forEach { state.selection.storyEvents.add(it) }
                    timeline.access().viewport!!.onKeyPressed.handle(
                        KeyEvent(this, timeline.access().viewport!!, KeyEvent.KEY_PRESSED, keyCode.char, "", keyCode, false, false, false, false)
                    )
                }
            }

            val spy = (component.viewPortDependencies.removeStoryEventController as RemoveStoryEventControllerDouble)
            assertThat(spy.requestedStoryEventIds.captured).containsExactlyInAnyOrder(*selectedItems.map { it.storyEventId }
                .toTypedArray())
        }

        @Test
        fun `can use options menu`(): Unit = runBlocking {
            timeline.drive {
                selectedItems.forEach { state.selection.storyEvents.add(it) }
                optionsButton!!.show()
                optionsButton!!.deleteStoryEventOption!!.fire()
            }

            val spy = (component.storyEventItemMenuDependencies.removeStoryEventController as RemoveStoryEventControllerDouble)
            assertThat(spy.requestedStoryEventIds.captured).containsExactlyInAnyOrder(*selectedItems.map { it.storyEventId }
                .toTypedArray())
        }

    }

    @Nested
    inner class `Users can drag story point labels to adjust their time` {

        init {
            runBlocking {
                listStoryEventsController.`given story events have been loaded`(
                    *Array(8) { StoryEventItem(StoryEvent.Id(), "Some name", (0 .. 10L).random()) }
                )
                withContext(Dispatchers.JavaFx) { awaitPulse() }
                withContext(Dispatchers.JavaFx) { awaitPulse() }
            }
        }

        @Nested
        inner class `Given single story point label has been selected` {

            val selectedLabel: StoryPointLabel = (timeline.state as Timeline.State.Loaded).run {
                storyEventItems.random().also(selection.storyEvents::add)
            }

            @Test
            fun `should move story point label to dragged location`() {
                FxRobot().apply {
                    interact {
                        moveTo(selectedLabel)
                        press(MouseButton.PRIMARY)
//                        drag(MouseButton.PRIMARY)
//                        moveTo(
//                            selectedLabel.localToScreen(100.0, 0.0)
//                        )
                        release(MouseButton.PRIMARY)
                    }
                }

                verify {
                    component.viewPortDependencies.adjustStoryEventsTimeController.adjustStoryEventsTime(
                        setOf(selectedLabel.storyEventId),
                        any()
                    )
                }
            }

        }

    }

    companion object {
        init {
//            runHeadless()
            FxToolkit.registerPrimaryStage()
        }
    }

}