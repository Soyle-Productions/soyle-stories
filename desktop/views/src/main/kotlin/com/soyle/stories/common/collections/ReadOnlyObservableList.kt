package com.soyle.stories.common.collections

import javafx.beans.Observable
import javafx.beans.binding.BooleanBinding
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import tornadofx.booleanBinding

interface ReadOnlyObservableList<out T> : List<T>, Observable {
    /**
     * Add a listener to this observable list.
     * @param listener the listener for listening to the list changes
     */
    fun addListener(listener: ListChangeListener<in T?>)

    /**
     * Tries to remove a listener from this observable list. If the listener is not
     * attached to this list, nothing happens.
     * @param listener a listener to remove
     */
    fun removeListener(listener: ListChangeListener<in T?>)

    companion object {

        operator fun <T> invoke(backingList: ObservableList<T>): ReadOnlyObservableList<T> =
            object : ReadOnlyObservableList<T>, List<T> by backingList, Observable by backingList {
                override fun addListener(listener: ListChangeListener<in T?>) = backingList.addListener(listener)
                override fun removeListener(listener: ListChangeListener<in T?>)= backingList.addListener(listener)
            }
    }

}

fun ReadOnlyObservableList<*>.empty(): BooleanBinding = booleanBinding(this) { isEmpty() }
fun ReadOnlyObservableList<*>.notEmpty(): BooleanBinding = booleanBinding(this) { isNotEmpty() }