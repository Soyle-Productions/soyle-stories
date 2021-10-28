package com.soyle.stories.storyevent.timeline

import javafx.beans.binding.BooleanBinding
import javafx.beans.property.*
import tornadofx.booleanBinding

/**
 * Enforces the valid states the view can be in
 */
sealed class TimelineViewModel(
    private val stateProperty: ReadOnlyObjectWrapper<TimelineViewModel>,
    private val loadingProperty: BooleanBinding,
    private val loadedProperty: BooleanBinding
) {

    fun type(): ReadOnlyObjectProperty<TimelineViewModel> = stateProperty.readOnlyProperty
    fun loading(): BooleanBinding = loadingProperty
    fun loaded(): BooleanBinding = loadedProperty

    fun load() {
        Loading(stateProperty, loadingProperty, loadedProperty).also { stateProperty.set(it) }
    }

    fun failed() {
        Failed(stateProperty, loadingProperty, loadedProperty).also { stateProperty.set(it) }
    }

    fun loaded(unit: Unit) {
        Loaded(stateProperty, loadingProperty, loadedProperty).also { stateProperty.set(it) }
    }

    companion object {
        operator fun invoke(): TimelineViewModel {
            val stateProperty = ReadOnlyObjectWrapper<TimelineViewModel>()
            val loadingProperty = stateProperty.booleanBinding { it is Loading }
            val loadedProperty = stateProperty.booleanBinding { it is Loaded }
            val initialState = Loading(stateProperty, loadingProperty, loadedProperty)
            stateProperty.set(initialState)
            return initialState
        }
    }

    internal class Loading(
        stateProperty: ReadOnlyObjectWrapper<TimelineViewModel>,
        loadingProperty: BooleanBinding,
        loadedProperty: BooleanBinding
    ) : TimelineViewModel(stateProperty, loadingProperty, loadedProperty)

    internal class Failed(
        stateProperty: ReadOnlyObjectWrapper<TimelineViewModel>,
        loadingProperty: BooleanBinding,
        loadedProperty: BooleanBinding
    ) : TimelineViewModel(stateProperty, loadingProperty, loadedProperty)

    internal class Loaded(
        stateProperty: ReadOnlyObjectWrapper<TimelineViewModel>,
        loadingProperty: BooleanBinding,
        loadedProperty: BooleanBinding
    ) : TimelineViewModel(stateProperty, loadingProperty, loadedProperty)

}