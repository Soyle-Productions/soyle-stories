package com.soyle.stories.storyevent.item

import com.soyle.stories.domain.storyevent.StoryEvent
import javafx.beans.Observable
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.BooleanExpression
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import javafx.scene.control.SelectionModel
import tornadofx.*

class StoryEventItemSelection(
    private val selectionMap: ObservableMap<StoryEvent.Id, StoryEventItemViewModel> = observableMapOf()
) : Observable by selectionMap {

    private val emptyProperty by lazy { booleanBinding(selectionMap) { size == 0 } }
    fun empty(): BooleanExpression = emptyProperty

    private val notEmptyProperty by lazy { empty().not() }
    fun notEmpty(): BooleanExpression = notEmptyProperty

    private val singleSelectionProperty: BooleanBinding by lazy { booleanBinding(selectionMap) { size == 1 } }
    fun hasSingleSelection(): BooleanExpression = singleSelectionProperty
    val hasSingleSelection: Boolean by hasSingleSelection()

    operator fun contains(item: StoryEventItemViewModel): Boolean = contains(item.storyEventId)
    operator fun contains(id: StoryEvent.Id): Boolean = selectionMap.containsKey(id)

    fun add(item: StoryEventItemViewModel) {
        selectionMap[item.storyEventId] = item
    }

    val selectedIds: Set<StoryEvent.Id>
        get() = selectionMap.keys

    val selectedItems: List<StoryEventItemViewModel>
        get() = selectionMap.values.toList()

    fun clear() {
        selectionMap.clear() }

}