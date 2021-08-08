package com.soyle.stories.common

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.collections.ObservableList
import tornadofx.booleanBinding
import kotlin.reflect.KProperty

operator fun ReadOnlyBooleanProperty.getValue(ref: Any, property: KProperty<*>): Boolean = value

fun <T> ObservableList<T>.emptyProperty() = booleanBinding(this) { isEmpty() }