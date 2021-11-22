package com.soyle.stories.scene.outline

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.storyevent.StoryEventItem
import com.sun.javafx.collections.ObservableListWrapper
import javafx.beans.binding.BooleanExpression
import javafx.beans.binding.IntegerExpression
import javafx.beans.binding.ObjectExpression
import javafx.beans.binding.StringExpression
import javafx.beans.property.*
import javafx.collections.FXCollections.unmodifiableObservableList
import javafx.collections.ObservableList
import tornadofx.booleanBinding
import tornadofx.integerBinding
import tornadofx.observableListOf

class SceneOutlineViewModel {

    var sceneId: Scene.Id? = null
        private set

    private val loadingProperty = ReadOnlyBooleanWrapper(false)
    fun isLoading(): BooleanExpression = loadingProperty.readOnlyProperty

    private val failureMessageProperty = ReadOnlyObjectWrapper<String?>(null)
    fun failureMessage(): ObjectExpression<String?> = failureMessageProperty.readOnlyProperty

    private val sceneNameProperty = ReadOnlyStringWrapper("")
    fun sceneName(): StringExpression = sceneNameProperty.readOnlyProperty
    val sceneName: String get() = sceneName().get()

    private val mutableStoryEventItems = observableListOf<StoryEventItem>()
    fun items(): ObservableList<StoryEventItem> = unmodifiableObservableList(mutableStoryEventItems)

    private val itemCountProperty = integerBinding(mutableStoryEventItems) { mutableStoryEventItems.size }
    fun itemCount(): IntegerExpression = itemCountProperty
    val itemCount: Int get() = itemCount().get()

    fun setItems(items: List<StoryEventItem>) {
        loadingProperty.set(false)
        mutableStoryEventItems.setAll(items)
    }

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

}