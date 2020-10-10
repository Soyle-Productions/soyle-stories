package com.soyle.stories.common.components

import com.soyle.stories.common.onChangeUntil
import com.sun.javafx.collections.ListListenerHelper.addListener
import javafx.beans.WeakListener
import javafx.beans.binding.Bindings
import javafx.beans.property.IntegerProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.Parent
import tornadofx.*
import java.lang.ref.WeakReference

fun <T> ObservableList<T>.onEachAsProperty(removeOnNull: Boolean = true, converter: (Property<T?>) -> Node) {

    fun createChild(index: Int) {
        val property = SimpleObjectProperty(getOrNull(index))
        val listener = ListChangeListener<T> {
            property.set(getOrNull(index))
        }
        addListener(listener)
        val node = converter(property)
        if (removeOnNull) {
            property.onChangeUntil({ it == null }) {
                if (it == null) {
                    removeListener(listener)
                    node.removeFromParent()
                }
            }
        }
    }

    val sizeProperty = SimpleIntegerProperty(size)
    onChange { sizeProperty.set(it.list.size) }
    sizeProperty.addListener { _, oldSizeNum, newSizeNum ->
        val oldSize = oldSizeNum?.toInt() ?: 0
        val newSize = newSizeNum?.toInt() ?: 0
        if (newSize > oldSize) {
            repeat(newSize - oldSize) { i ->
                createChild(i + oldSize)
            }
        }
    }

    indices.forEach { i ->
        createChild(i)
    }

}

fun <T, R> ObservableList<T>.cacheEachAs(removeOnNull: Boolean = true, converter: (Property<T?>) -> R): CachedList<R> {

    val outputList = observableListOf<R>()

    val cachedList = object : CachedList<R>, ObservableList<R> by outputList {
        var itemRemoved: (r: R) -> Unit = {}
        override fun whenRemoved(block: (r: R) -> Unit) {
            itemRemoved = block
        }
    }

    fun createChild(index: Int) {
        val property = SimpleObjectProperty<T>(getOrNull(index))
        val listener = ListChangeListener<T> {
            property.set(getOrNull(index))
        }
        addListener(listener)
        val r = converter(property)
        outputList.add(r)
        if (removeOnNull) {
            property.onChangeUntil({ it == null }) {
                if (it == null) {
                    outputList.remove(r)
                    removeListener(listener)
                    cachedList.itemRemoved(r)
                }
            }
        }
    }

    val sizeProperty = SimpleIntegerProperty(size)
    onChange { sizeProperty.set(it.list.size) }
    sizeProperty.addListener { _, oldSizeNum, newSizeNum ->
        val oldSize = oldSizeNum?.toInt() ?: 0
        val newSize = newSizeNum?.toInt() ?: 0
        if (newSize > oldSize) {
            repeat(newSize - oldSize) { i ->
                createChild(i + oldSize)
            }
        }
    }

    indices.forEach { i ->
        createChild(i)
    }

    return cachedList

}
interface CachedList<R> : ObservableList<R> {
    fun whenRemoved(block: (r: R) -> Unit)
}


// basic mapping.  ObservableCollection, for each item, (T) -> R, no special memory management
fun <SourceType, TargetType> MutableList<TargetType>.bind(
    sourceList: ObservableList<SourceType>,
    terminateOn: ObservableValue<Boolean>,
    converter: (SourceType) -> TargetType
): ListConversionListener<SourceType, TargetType>
{
    val listener = bind(sourceList, converter)
    terminateOn.addListener(object : ChangeListener<Boolean> {
        override fun changed(observable: ObservableValue<out Boolean>?, oldValue: Boolean?, newValue: Boolean?) {
            if (newValue == true) {
                sourceList.removeListener(listener)
                terminateOn.removeListener(this)
            }
        }
    })
    return listener
}

fun <T, L : List<T>> bindAndCacheNodes(sourceList: ObservableValue<L>, converter: (Property<T?>) -> Node) {

    fun createChild(index: Int) {
        val property = SimpleObjectProperty(sourceList.value?.getOrNull(index))
        val listener = ChangeListener<L> { _, _, newList ->
            property.set(newList.getOrNull(index))
        }
        sourceList.addListener(listener)
        val node = converter(property)
        property.onChangeUntil({ it == null }) {
            if (it == null) {
                sourceList.removeListener(listener)
                node.removeFromParent()
            }
        }
    }

    sourceList.addListener { _, oldValue, newValue ->
        val oldSize = oldValue?.size ?: 0
        val newSize = newValue?.size ?: 0
        if (newSize > oldSize) {
            repeat(newSize - oldSize) { i ->
                createChild(i + oldSize)
            }
        }
    }

    sourceList.value?.indices?.forEach { i ->
        createChild(i)
    }

}

fun <K, T> EventTarget.associateChildrenTo(sourceMap: ObservableValue<Map<K, T>?>, removeOnNull: Boolean = true, converter: (Property<T?>) -> Node) {

    fun createChild(key: K) {
        val property = sourceMap.select { SimpleObjectProperty(it?.get(key)) }
        val node = converter(property)
        if (removeOnNull) {
            property.onChangeUntil({ it == null }) {
                if (it == null) node.removeFromParent()
            }
        }
    }

    sourceMap.addListener { _, oldValue, newValue ->
        val oldKeys = oldValue?.keys ?: emptySet()
        val newKeys = newValue?.keys ?: emptySet()
        (newKeys - oldKeys).forEach {
            createChild(it)
        }
    }

    sourceMap.value?.keys?.forEach {
        createChild(it)
    }

}