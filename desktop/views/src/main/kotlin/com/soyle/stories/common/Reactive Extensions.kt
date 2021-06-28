package com.soyle.stories.common

import javafx.beans.InvalidationListener
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.CheckBox
import javafx.scene.control.ToggleButton
import tornadofx.booleanProperty
import tornadofx.onChangeOnce
import tornadofx.toProperty
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

/**
 * listens to the source observable value until the receiver is GC'd.
 */
fun <T : Any, R : Any> Any.boundProperty(source: ObservableValue<T?>, selector: T?.() -> R?): Property<R?>
{
    val thisRef = WeakReference(this)
    val prop = SimpleObjectProperty(source.value.selector())
    source.onChangeUntil({ thisRef.get() == null }) {
        if (thisRef.get() == null) return@onChangeUntil
        prop.value = it.selector()
    }
    return prop
}

/**
 * Allows the selection property of a selectable button to be bound to an observable boolean without causing bound
 * errors when the user clicks on the button.
 */
fun ToggleButton.bindSelection(property: ObservableValue<Boolean>)
{
    isSelected = property.value
    val thisRef = WeakReference(this)
    property.onChangeOnce {
        thisRef.get()?.bindSelection(property)
    }
}
/**
 * Allows the selection property of a selectable button to be bound to an observable boolean without causing bound
 * errors when the user clicks on the button.
 */
fun CheckBox.bindSelection(property: ObservableValue<Boolean>)
{
    isSelected = property.value
    val thisRef = WeakReference(this)
    property.onChangeOnce {
        thisRef.get()?.bindSelection(property)
    }
}