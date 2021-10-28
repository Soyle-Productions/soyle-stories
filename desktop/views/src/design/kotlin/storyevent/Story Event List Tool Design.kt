package com.soyle.stories.desktop.view.storyevent

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.adapter.storyevent.RenameStoryEventControllerDouble
import com.soyle.stories.desktop.adapter.storyevent.create.CreateStoryEventControllerDouble
import com.soyle.stories.desktop.adapter.storyevent.list.ListStoryEventsControllerDouble
import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.create.StoryEventCreatedNotifier
import com.soyle.stories.storyevent.list.*
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensNotifier
import com.soyle.stories.storyevent.rename.StoryEventRenamedNotifier
import com.soyle.stories.storyevent.time.StoryEventRescheduledNotifier
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ListCell
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tornadofx.fitToParentSize
import tornadofx.objectProperty
import tornadofx.observableListOf
import tornadofx.toObservable

class `Story Event List Tool Design` : DesignTest() {

    private val actions = object : StoryEventListViewActions {
        override fun loadStoryEvents() = Unit
        override fun createStoryEvent() = Unit
        override fun insertStoryEventBeforeSelectedItem() = Unit
        override fun insertStoryEventAtSameTimeAsSelectedItem() = Unit
        override fun insertStoryEventAfterSelectedItem() = Unit
        override fun renameSelectedItem() = Unit
        override fun rescheduleSelectedItem() = Unit
        override fun adjustTimesOfSelectedItems() = Unit
        override fun deleteSelectedItems() = Unit
        override fun viewSelectedItemInTimeline() = Unit
    }
    private val viewModel = objectProperty<StoryEventListViewModel>(null)
    private val cell = object : StoryEventListCell {
        override fun invoke(): ListCell<StoryEventListItemViewModel> {
            return object : ListCell<StoryEventListItemViewModel>() {
                override fun updateItem(item: StoryEventListItemViewModel?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty || item == null) {
                        text = null
                        graphic = null
                    } else {
                        graphic = StoryEventListItemView(item)
                    }
                }
            }
        }
    }
    private val view by lazy {
        StoryEventListToolView(actions, viewModel, cell)
    }
    override val node: Node by lazy {
        TabPane(Tab("Story Events", view).apply {

        })
    }

    @Test
    fun `Loading`() {
        viewModel.set(LoadingStoryEventListViewModel)
        verifyDesign {
        }
    }

    @Test
    fun `Failed`() {
        viewModel.set(FailedStoryEventListViewModel)
        verifyDesign {
        }
    }

    @Test
    fun `Empty`() {
        viewModel.set(EmptyStoryEventListViewModel)
        verifyDesign {
        }
    }

    @Test
    fun `Populated`() {

        viewModel.set(PopulatedStoryEventListViewModel(observableListOf(
            StoryEventListItemViewModel(StoryEvent.Id()).apply {
                nameProperty.set("Bob eats a shoe")
                timeProperty.set(2)
                prevItemHasSameTime.set(true)
            },
            StoryEventListItemViewModel(StoryEvent.Id()).apply {
                nameProperty.set("Alice learns to love")
                timeProperty.set(3)
            },
            StoryEventListItemViewModel(StoryEvent.Id()).apply {
                nameProperty.set("Frank moves out")
                timeProperty.set(6)
            },
            StoryEventListItemViewModel(StoryEvent.Id()).apply {
                nameProperty.set("Something else")
                timeProperty.set(6)
                prevItemHasSameTime.set(true)
            },
            StoryEventListItemViewModel(StoryEvent.Id()).apply {
                nameProperty.set("Another One")
                timeProperty.set(6)
                prevItemHasSameTime.set(true)
            },
            StoryEventListItemViewModel(StoryEvent.Id()).apply {
                nameProperty.set("Lori hits Bob for being weird")
                timeProperty.set(7)
            },
        )))
        verifyDesign {
        }
    }

}