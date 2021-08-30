package com.soyle.stories.storyevent.list

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import com.soyle.stories.storyevent.create.CreateStoryEventDialog
import com.soyle.stories.storyevent.create.StoryEventCreatedReceiver
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import com.soyle.stories.storyevent.rename.RenameStoryEventDialog
import com.soyle.stories.storyevent.rename.RenameStoryEventDialogView
import com.soyle.stories.storyevent.rename.StoryEventRenamedReceiver
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.sun.scenario.effect.impl.prism.PrEffectHelper.render
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.MenuButton
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage
import tornadofx.*
import java.util.*

class StoryEventListToolView(
    private val projectId: Project.Id,
    private val createStoryEventDialog: CreateStoryEventDialog,
    private val renameStoryEventDialog: RenameStoryEventDialog,
    private val listStoryEventsInProject: ListStoryEventsController,

    // events
    storyEventCreated: Notifier<StoryEventCreatedReceiver>,
    storyEventRenamed: Notifier<StoryEventRenamedReceiver>
) : View() {

    private sealed class State {

        open fun render(): List<Node> = emptyList()
        open fun addStoryEvent(storyEventItem: StoryEventListItemViewModel): State = this
        open fun replaceStoryEvent(
            storyEventId: StoryEvent.Id,
            replaceWith: StoryEventListItemViewModel.() -> StoryEventListItemViewModel
        ): State = this
    }

    private inner class Uninitialized : State()
    private inner class Loading : State()
    private inner class FailedToLoad : State() {

        override fun render(): List<Node> = listOf(retryButton)
    }

    private inner class Empty : State() {

        override fun render(): List<Node> = listOf(createStoryEventButton)
        override fun addStoryEvent(storyEventItem: StoryEventListItemViewModel): State =
            Populated(listOf(storyEventItem))
    }

    private inner class Populated(val items: List<StoryEventListItemViewModel>) : State() {

        override fun render(): List<Node> = listOf(createStoryEventButton, optionsButton, storyEventList(items))
        override fun addStoryEvent(storyEventItem: StoryEventListItemViewModel): State =
            Populated(items + storyEventItem)

        override fun replaceStoryEvent(
            storyEventId: StoryEvent.Id,
            replaceWith: StoryEventListItemViewModel.() -> StoryEventListItemViewModel
        ): State {
            if (items.any { it.id == storyEventId.uuid.toString() }) {
                return Populated(items.map {
                    if (it.id == storyEventId.uuid.toString()) it.replaceWith()
                    else it
                })
            }
            return this
        }
    }

    private val state = objectProperty<State>(Uninitialized())

    private val _root = VBox()
    override val root: Parent
        get() = _root

    private val createStoryEventButton = Button().apply {
        id = "create-story-event"
        action { openCreateStoryEventDialog() }
    }
    private val retryButton = Button().apply {
        id = "retry"
        action(::loadStoryEvents)
    }
    private val storyEventList = ListView<StoryEventListItemViewModel>().apply {
        cellFormat { text = it.name }
        vgrow = Priority.ALWAYS
    }

    private fun storyEventList(items: List<StoryEventListItemViewModel>): Node {
        storyEventList.items.setAll(items)
        return storyEventList
    }

    private val optionsButton = MenuButton().apply {
        enableWhen(storyEventList.selectionModel.selectedItemProperty().isNotNull)
        item("") {
            id = "insert-story-event-before"
            action { openCreateStoryEventDialog(-1L) }
        }
        item("") {
            id = "insert-story-event-at-the-same-time-as"
            action { openCreateStoryEventDialog(0L) }
        }
        item("") {
            id = "insert-story-event-after"
            action { openCreateStoryEventDialog(1L) }
        }
        item("") {
            id = "rename"
            action(::openRenameStoryEventDialog)
        }
    }

    private fun openCreateStoryEventDialog() {
        createStoryEventDialog(CreateStoryEventDialog.Props())
    }

    private fun openCreateStoryEventDialog(delta: Long) {
        val selectedId = storyEventList.selectedItem?.id?.let(UUID::fromString)?.let(StoryEvent::Id)!!
        createStoryEventDialog(CreateStoryEventDialog.Props(
            CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative(selectedId, delta)
        ))
    }

    private fun openRenameStoryEventDialog() {
        val selectedItem = storyEventList.selectedItem!!
        val selectedId = selectedItem.id.let(UUID::fromString).let(StoryEvent::Id)
        renameStoryEventDialog(RenameStoryEventDialog.Props(selectedId, selectedItem.name))
    }

    private fun loadStoryEvents() {
        state.set(Loading())

        listStoryEventsInProject.listStoryEventsInProject(projectId) {
            if (it.storyEventItems.isEmpty()) state.set(Empty())
            else state.set(Populated(it.storyEventItems.map(::StoryEventListItemViewModel)))
        }.invokeOnCompletion { failure ->
            if (failure != null) {
                if (state.value is Loading) state.set(FailedToLoad())
            }
        }
    }

    private fun render(state: State) {
        _root.children.setAll(state.render())
    }

    private val eventReceiver = EventReceiver()

    init {
        storyEventCreated.addListener(eventReceiver)
        storyEventRenamed.addListener(eventReceiver)
        state.onChangeWithCurrent {
            if (Platform.isFxApplicationThread()) render(it!!)
            else runLater { render(it!!) }
        }
        loadStoryEvents()
    }

    private class Cell : ListCell<StoryEventListItemViewModel>() {

        override fun updateItem(item: StoryEventListItemViewModel?, empty: Boolean) {
            super.updateItem(item, empty)
            text = item?.name ?: ""
        }
    }

    private inner class EventReceiver : StoryEventCreatedReceiver, StoryEventRenamedReceiver {

        override suspend fun receiveStoryEventCreated(event: StoryEventCreated) {
            val newItem =
                StoryEventListItemViewModel(event.storyEventId.uuid.toString(), event.time.toInt(), event.name)
            state.set(state.value.addStoryEvent(newItem))
        }

        override suspend fun receiveStoryEventRenamed(event: StoryEventRenamed) {
            state.set(state.value.replaceStoryEvent(event.storyEventId) { copy(name = event.newName) })
        }
    }

}