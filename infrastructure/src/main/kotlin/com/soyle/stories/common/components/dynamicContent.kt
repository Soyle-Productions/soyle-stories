package com.soyle.stories.common.components

import com.soyle.stories.common.onChangeUntil
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.Parent
import tornadofx.bindChildren
import tornadofx.removeFromParent
import tornadofx.select

fun <T> EventTarget.bindAndCacheChildren(sourceList: ObservableValue<List<T>>, removeOnNull: Boolean = true, converter: (Property<T?>) -> Node) {

    fun createChild(index: Int) {
        val property = sourceList.select { SimpleObjectProperty(it.getOrNull(index)) }
        val node = converter(property)
        if (removeOnNull) {
            property.onChangeUntil({ it == null }) {
                if (it == null) node.removeFromParent()
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

    sourceList.value.indices.forEach { i ->
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