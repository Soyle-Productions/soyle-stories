package com.soyle.stories.storyevent.timeline

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.item.StoryEventItemSelection
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.sun.javafx.scene.control.skin.Utils
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.*
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import javafx.event.Event
import javafx.event.EventType
import javafx.scene.control.Control
import javafx.scene.control.ScrollToEvent
import javafx.scene.control.Skin
import tornadofx.*

class Timeline(
    makeActions: (Timeline) -> TimelineComponent.Actions,
    private val makeSkin: (Timeline) -> Skin<*>
) : Control() {

    companion object {
        val SCROLL_TO_LABEl = EventType<ScrollToEvent<StoryPointLabel>>(Event.ANY, "SCROLL_TO_LABEl");
    }

    private val stateProperty = ReadOnlyObjectWrapper<State>(State.Loading)
    fun state(): ReadOnlyObjectProperty<State> = stateProperty.readOnlyProperty
    val state by state()

    private val loadingProperty = stateProperty.booleanBinding { it is State.Loading }
    fun loading(): BooleanExpression = loadingProperty
    val isLoading by loading()

    private val failedProperty = stateProperty.booleanBinding { it is State.Failed }
    fun failed(): BooleanExpression = failedProperty
    val isFailed by failed()

    private val loadedProperty = stateProperty.booleanBinding { it is State.Loaded }
    fun loaded(): BooleanExpression = loadedProperty
    val isLoaded by loaded()

    private val condensedLabelsProperty = booleanProperty()
    fun condensedLabels() = condensedLabelsProperty
    var areLabelsCondensed by condensedLabelsProperty

    abstract class Presenter(protected val timeline: Timeline) {
        protected val stateProperty get() = timeline.stateProperty
    }

    sealed class State {
        object Loading : State()
        object Failed : State()
        class Loaded(
            private val storyEventItemsProperty: ObservableList<StoryPointLabel>,
            private val gui: TimelineComponent.GUI
        ) : State() {
            fun storyEventItems() = storyEventItemsProperty
            val storyEventItems: List<StoryPointLabel> get() = storyEventItems()

            private val storyEventAddedListener = ListChangeListener<StoryPointLabel> {
                while (it.next()) {
                    if (it.wasAdded()) it.addedSubList.forEach { it.selection().set(selection.storyEvents) }
                }
            }
            private val weakStoryEventAddedListener = WeakListChangeListener(storyEventAddedListener)

            val selection = TimelineSelectionModel()

            val storyPointLabelMenu = gui.StoryEventItemMenu(selection.storyEvents)

            init {
                storyEventItemsProperty.forEach { it.selection().set(selection.storyEvents) }
                storyEventItemsProperty.addListener(weakStoryEventAddedListener)
            }

            abstract class Presenter(protected val state: Loaded) {
                protected val storyEventItemsProperty get() = state.storyEventItemsProperty
            }
        }
    }

    override fun createDefaultSkin(): Skin<*> = makeSkin(this)

    private val actions = makeActions(this)

    fun focusOn(withStoryEventFocused: StoryEvent.Id) {
        val currentState = state
        if (currentState is State.Loaded) {
            actions.focusOn(withStoryEventFocused)
        } else {
            stateProperty.onChangeOnce {
                if (it is State.Loaded) actions.focusOn(withStoryEventFocused)
            }
        }
    }

    init {
        addClass(TimelineStyles.timeline)
        prefWidth = USE_COMPUTED_SIZE
        prefHeight = USE_COMPUTED_SIZE
        isFocusTraversable = true
    }

}

