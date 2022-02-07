package com.soyle.stories.common

import javafx.beans.InvalidationListener
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.control.CheckBox
import javafx.scene.control.ToggleButton
import tornadofx.booleanProperty
import tornadofx.onChange
import tornadofx.onChangeOnce
import tornadofx.toProperty
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

/**
 * Listens to changes to the [source] until [this] is GC'd
 */
fun <Scope: Any, T> Scope.scopedListener(source: ObservableValue<T>, listener: Scope.(T?) -> Unit)
{
    val thisStr = this.toString()
    val thisRef = WeakReference(this)
    listener(source.value)
    source.onChangeUntil({ thisRef.get() == null }) {
        val ref = thisRef.get()
        if (ref == null) {
            println("$thisStr was GC'd and thus did not receive the update of $it")
            return@onChangeUntil
        }
        ref.listener(it)
    }
}
/**
 * Listens to changes to the [source] until [this] is GC'd
 */
fun <Scope: Any, T> Scope.scopedListener(source: ObservableList<T>, listener: Scope.(List<T>?) -> Unit)
{
    val thisStr = this.toString()
    val thisRef = WeakReference(this)
    listener(source)
    source.onChangeUntil({ thisRef.get() == null }) {
        val ref = thisRef.get()
        if (ref == null) {
            println("$thisStr was GC'd and thus did not receive the update of $it")
            return@onChangeUntil
        }
        ref.listener(it)
    }
}

/**
 * listens to the source observable value until the receiver is GC'd.
 */
fun <T, R : Any> Any.boundProperty(source: ObservableValue<T>, selector: (T?) -> R?): Property<R?>
{
    val prop = SimpleObjectProperty<R>()
    scopedListener(source) { prop.value = selector(it) }
    return prop
}

/**
 * Allows the selection property of a selectable button to be bound to an observable boolean without causing bound
 * errors when the user clicks on the button.
 */
fun ToggleButton.bindSelection(property: ObservableValue<Boolean>) = selectedProperty().softBind(property) { it }

/**
 * Allows the selection property of a selectable button to be bound to an observable boolean without causing bound
 * errors when the user clicks on the button.
 */
fun CheckBox.bindSelection(property: ObservableValue<Boolean>) = selectedProperty().softBind(property) { it }

/**
 * As long as the receiver has not been GC'd, changes to the [source] will trigger an update using the supplied [converter]
 */
fun <T, R> Property<T>.softBind(source: ObservableValue<R>, converter: (R?) -> T)
{
    value = converter(source.value)
    val thisRef = WeakReference(this)
    source.onChangeOnce {
        thisRef.get()?.softBind(source, converter)
    }
}