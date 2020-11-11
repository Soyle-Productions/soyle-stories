package com.soyle.stories.common

import javafx.beans.WeakListener
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import tornadofx.observableListOf
import java.lang.ref.WeakReference

interface CachedList<T> : ObservableList<T> {

    fun onAdded(handler: (addedItem: T) -> Unit)
    fun onRemoved(handler: (removedItem: T) -> Unit)
    fun onReplaced(handler: (removedItem: T, newItem: T) -> Unit)

}
private class CachedListImpl<T> : CachedList<T>, ObservableList<T> by observableListOf() {

    private var onAdded: ((T) -> Unit)? = null
    override fun onAdded(handler: (addedItem: T) -> Unit) {
        onAdded = handler
    }
    private var onRemoved: ((T) -> Unit)? = null
    override fun onRemoved(handler: (removedItem: T) -> Unit) {
        onRemoved = handler
    }
    private var onReplaced: ((T, T) -> Unit)? = null
    override fun onReplaced(handler: (removedItem: T, newItem: T) -> Unit) {
        onReplaced = handler
    }

    fun added(addedItem: T) {
        onAdded?.invoke(addedItem)
    }
    fun removed(removedItem: T) {
        onRemoved?.invoke(removedItem)
    }
    fun replaced(removedItem: T, newItem: T) {
        onReplaced?.run {
            invoke(removedItem, newItem)
            Unit
        } ?: run {
            removed(removedItem)
            added(newItem)
        }
    }
}

private class CachedListListener<SourceType, TargetType>(
    targetList: MutableList<TargetType>,
    val keyGenerator: ((SourceType) -> String)?,
    val converter: (SourceType) -> TargetType
) : ListChangeListener<SourceType>, WeakListener {

    private val targetRef: WeakReference<MutableList<TargetType>> = WeakReference(targetList)

    override fun onChanged(change: ListChangeListener.Change<out SourceType>) {
        val targetList = targetRef.get()
        if (targetList == null) {
            change.list.removeListener(this)
        } else {
            val sourceList = change.list
            buildNextList(targetList, sourceList)
        }
    }

    private val sourceMap = mutableMapOf<SourceType, Int>()
    private val cache = mutableMapOf<String, TargetType>()

    fun buildNextList(previousList: MutableList<TargetType>, sourceList: List<SourceType>) {
        val rebuild = sourceList.mapIndexed { index: Int, source: SourceType ->
            val key = keyGenerator?.invoke(source) ?: index.toString()
            val originalIndex = sourceMap[source]
            val targetAtIndex = previousList.getOrNull(index)

            if (originalIndex != index || targetAtIndex == null) {
                sourceMap[source] = index
                val cachedTarget = cache[key]
                val nextTarget = cachedTarget ?: converter(source)
                if (cachedTarget == null) cache[key] = nextTarget

                if (previousList is CachedListImpl) {
                    if (targetAtIndex == null && cachedTarget == null) previousList.added(nextTarget)
                    else if (cachedTarget != null && cachedTarget != nextTarget) previousList.replaced(cachedTarget, nextTarget)
                }

                nextTarget
            } else {
                targetAtIndex
            }
        }
        if (previousList != rebuild) {
            previousList.clear()
            previousList.addAll(rebuild)

            val sourcesToRemove = sourceMap.keys - sourceList.toSet()
            val keySet = sourceList.mapIndexed { i, it -> keyGenerator?.invoke(it) ?: i.toString() }.toSet()
            val removeKeys = cache.keys - keySet
            sourceMap.keys.removeIf { it in sourcesToRemove }
            val removedTargets = removeKeys.mapNotNull { cache[it] }
            cache.keys.removeIf { it !in keySet }
            if (previousList is CachedListImpl) {
                removedTargets.forEach { previousList.removed(it) }
            }
        }

    }

    override fun wasGarbageCollected() = targetRef.get() == null

    override fun hashCode() = targetRef.get().hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        val ourList = targetRef.get() ?: return false

        if (other is CachedListListener<*, *>) {
            val otherList = other.targetRef.get()
            return ourList === otherList
        }
        return false
    }
}

fun <SourceType, TargetType> ObservableList<SourceType>.mapObservableTo(
    targetList: ObservableList<TargetType>,
    keyGenerator: ((SourceType) -> String)? = null,
    mapper: (SourceType) -> TargetType
) : ListChangeListener<SourceType> {
    val listener = CachedListListener(targetList, keyGenerator, mapper)
    listener.buildNextList(targetList, this)
    removeListener(listener)
    addListener(listener)
    return listener
}

fun <SourceType, TargetType> ObservableList<SourceType>.mapObservable(
    keyGenerator: ((SourceType) -> String)? = null,
    mapper: (SourceType) -> TargetType
) : CachedList<TargetType>
{
    val targetList = CachedListImpl<TargetType>()
    mapObservableTo(targetList, keyGenerator, mapper)
    return targetList

}
