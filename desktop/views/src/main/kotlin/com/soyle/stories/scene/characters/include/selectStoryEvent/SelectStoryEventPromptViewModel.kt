package com.soyle.stories.scene.characters.include.selectStoryEvent

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.SelectStoryEventPrompt
import com.soyle.stories.usecase.scene.storyevent.list.StoryEventInSceneItem
import com.soyle.stories.usecase.scene.storyevent.list.StoryEventsInScene
import com.soyle.stories.usecase.storyevent.StoryEventItem
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.CompletableDeferred
import tornadofx.*

class SelectStoryEventPromptViewModel : SelectStoryEventPrompt {

    private val _isNeeded = booleanProperty(false)
    fun isNeeded(): ReadOnlyBooleanProperty = _isNeeded

    private val _characterName = stringProperty("")
    fun characterName(): ReadOnlyStringProperty = _characterName

    private val _items = observableListOf<StoryEventInSceneItem>()
    val items: ObservableList<StoryEventInSceneItem> = FXCollections.unmodifiableObservableList(_items)

    private val _selection = mutableMapOf<StoryEventInSceneItem, BooleanProperty>()
    fun isSelected(item: StoryEventInSceneItem): BooleanProperty = _selection.getOrPut(item) { booleanProperty(false) }

    private val _shouldCreateNewEvent = booleanProperty(false)
    fun shouldCreateNewEvent(): BooleanProperty = _shouldCreateNewEvent
    var shouldCreateNewEvent: Boolean by _shouldCreateNewEvent

    val newEvent = NewEventViewModel()

    class NewEventViewModel {

        private val _name = objectProperty<NonBlankString?>(null)
        fun name(): ObjectProperty<NonBlankString?> = _name
        var name: NonBlankString? by _name

        private val _time = objectProperty<Long?>(null)
        fun time(): ObjectProperty<Long?> = _time
        var time: Long? by _time

    }

    private val _isSubmitting = booleanProperty(false)
    fun isSubmitting(): BooleanProperty = _isSubmitting
    var isSubmitting: Boolean by _isSubmitting

    private var onSubmit: (SelectStoryEventPrompt.Selection) -> Unit = {}
    private var onCancel: () -> Unit = {}

    fun submit() {
        val selection = SelectStoryEventPrompt.Selection(
            _selection.filter { it.value.value }.map {
                SelectStoryEventPrompt.StoryEventSelection(it.key.storyEventId)
            },
            if (_shouldCreateNewEvent.get()) {
                SelectStoryEventPrompt.CreateStoryEventSelection(
                    newEvent.name().get() ?: return,
                    newEvent.time().get()?.let(CreateStoryEvent.RequestModel.RequestedStoryEventTime::Absolute)
                )
            } else null
        )

        _isSubmitting.set(true)
        onSubmit(selection)
    }

    fun cancel() {
        _isNeeded.set(false)
        onCancel()
    }

    override suspend fun selectStoryEvent(
        character: String,
        storyEvents: StoryEventsInScene
    ): SelectStoryEventPrompt.Selection? {
        _selection.clear()
        _characterName.set(character)
        _items.setAll(storyEvents)
        val selection = CompletableDeferred<SelectStoryEventPrompt.Selection?>()

        onCancel = { selection.complete(null) }
        onSubmit = { selection.complete(it) }

        _isNeeded.set(true)
        _isSubmitting.set(false)
        return selection.await()
    }

    override suspend fun done() {
        _isNeeded.set(false)
    }

}