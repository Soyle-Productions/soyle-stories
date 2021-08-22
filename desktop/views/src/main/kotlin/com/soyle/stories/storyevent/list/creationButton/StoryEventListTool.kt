package com.soyle.stories.storyevent.list.creationButton

import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.domain.project.Project
import com.soyle.stories.storyevent.create.CreateStoryEventForm
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import com.soyle.stories.storyevent.list.ListStoryEventsController
import com.soyle.stories.usecase.storyevent.StoryEventItem
import com.soyle.stories.usecase.storyevent.listAllStoryEvents.ListAllStoryEvents
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*

class StoryEventListTool(
    private val projectId: Project.Id,
    private val createStoryEventFormFactory: () -> CreateStoryEventForm,
    private val listStoryEventsInProject: ListStoryEventsController
) {

    private sealed class State {
        abstract fun render(): List<Node>
    }
    private inner class Uninitialized : State() {
        override fun render(): List<Node> = emptyList()
    }
    private inner class Loading : State() {
        override fun render(): List<Node> = emptyList()
    }
    private inner class FailedToLoad : State() {
        override fun render(): List<Node> = listOf(retryButton)
    }
    private inner class Empty : State() {
        override fun render(): List<Node> = listOf(createStoryEventButton)
    }
    private inner class Populated(val items: List<StoryEventItem>) : State() {
        override fun render(): List<Node> = listOf(createStoryEventButton, storyEventList(items))
    }

    private val state = objectProperty<State>(Uninitialized())

    private val _root = VBox()
    val root: Node
        get() = _root

    private val createStoryEventButton = Button().apply {
        id = "create-story-event"
        action(::openCreateStoryEventDialog)
    }
    private val retryButton = Button().apply {
        id = "retry"
        action(::loadStoryEvents)
    }
    private val storyEventList = ListView<StoryEventListItemViewModel>().apply {
        setCellFactory { Cell() }
        vgrow = Priority.ALWAYS
    }
    private fun storyEventList(items: List<StoryEventItem>): Node {
        val newItems = items.map(::StoryEventListItemViewModel)
        storyEventList.items.setAll(newItems)
        return storyEventList
    }

    private fun openCreateStoryEventDialog() {
        Stage().apply {
            scene = Scene(createStoryEventFormFactory().root as Parent)
            show()
        }
    }

    private fun loadStoryEvents() {
        state.set(Loading())

        listStoryEventsInProject.listStoryEventsInProject(projectId) {
            if (it.storyEventItems.isEmpty()) state.set(Empty())
            else state.set(Populated(it.storyEventItems))
        }.invokeOnCompletion { failure ->
            if (failure != null) {
                if ( state.value is Loading ) state.set(FailedToLoad())
            }
        }
    }

    init {
        state.onChangeWithCurrent {
            runLater {
                _root.children.setAll(it!!.render())
            }
        }
        loadStoryEvents()
    }

    private class Cell : ListCell<StoryEventListItemViewModel>() {
        override fun updateItem(item: StoryEventListItemViewModel?, empty: Boolean) {
            super.updateItem(item, empty)
            text = item?.name ?: ""
        }
    }

}