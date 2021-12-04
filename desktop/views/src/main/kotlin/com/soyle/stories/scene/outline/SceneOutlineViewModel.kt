package com.soyle.stories.scene.outline

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.scene.outline.item.OutlinedStoryEventItem
import javafx.beans.binding.BooleanExpression
import javafx.beans.binding.IntegerExpression
import javafx.beans.binding.ObjectExpression
import javafx.beans.binding.StringExpression
import javafx.beans.property.*
import javafx.collections.FXCollections.unmodifiableObservableList
import javafx.collections.ObservableList
import javafx.scene.control.MenuItem
import tornadofx.integerBinding
import tornadofx.objectProperty
import tornadofx.observableListOf

class SceneOutlineViewModel {

    var sceneId: Scene.Id? = null
        private set

    private val loadingProperty = ReadOnlyBooleanWrapper(false)
    fun isLoading(): BooleanExpression = loadingProperty.readOnlyProperty
    val isLoading: Boolean get() = loadingProperty.get()

    private val failureMessageProperty = ReadOnlyObjectWrapper<String?>(null)
    fun failureMessage(): ObjectExpression<String?> = failureMessageProperty.readOnlyProperty

    private val sceneNameProperty = ReadOnlyStringWrapper("")
    fun sceneName(): StringExpression = sceneNameProperty.readOnlyProperty
    val sceneName: String get() = sceneName().get()

    private val mutableStoryEventItems = observableListOf<OutlinedStoryEventItem>()
    fun items(): ObservableList<OutlinedStoryEventItem> = unmodifiableObservableList(mutableStoryEventItems)

    private val itemCountProperty = integerBinding(mutableStoryEventItems) { mutableStoryEventItems.size }
    fun itemCount(): IntegerExpression = itemCountProperty
    val itemCount: Int get() = itemCount().get()

    fun setItems(items: List<OutlinedStoryEventItem>) {
        loadingProperty.set(false)
        mutableStoryEventItems.setAll(items)
    }

    private val availableItemsProperty = objectProperty<List<MenuItem>?>(null)
    fun availableItems(): ObjectProperty<List<MenuItem>?> = availableItemsProperty
    var availableItems: List<MenuItem>?
        get() = availableItemsProperty.get()
        set(value) {
            availableItemsProperty.set(value)
        }

    private var onRequestingStoryEventsToCover: () -> Unit = {}
    fun setOnRequestingStoryEventsToCover(handler: () -> Unit) {
        onRequestingStoryEventsToCover = handler
    }

    private val requestingStoryEventsToCoverProperty = object : SimpleBooleanProperty(false) {
        override fun set(newValue: Boolean) {
            super.set(newValue)
            if (newValue) onRequestingStoryEventsToCover()
        }
    }
    fun requestingStoryEventsToCover(): BooleanProperty = requestingStoryEventsToCoverProperty
    val isRequestingStoryEventsToCover: Boolean get() = requestingStoryEventsToCoverProperty.get()

    fun reset(sceneId: Scene.Id, sceneName: String) {
        this.sceneId = sceneId
        loadingProperty.set(true)
        sceneNameProperty.set(sceneName)
        mutableStoryEventItems.clear()
        failureMessageProperty.set(null)
    }

    fun failed(failure: Throwable) {
        loadingProperty.set(false)
        failureMessageProperty.set(failure.message)
    }

    internal fun removeStoryEvent(storyEventId: StoryEvent.Id) {

    }

}