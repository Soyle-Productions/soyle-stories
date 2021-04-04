package com.soyle.stories.common

import com.soyle.stories.di.resolve
import com.soyle.stories.gui.View
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue
import tornadofx.*
import kotlin.error
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

abstract class ProjectScopedModel<VM : Any> : Model<ProjectScope, VM>(ProjectScope::class) {
	override val applicationScope: ApplicationScope
		get() = scope.applicationScope
}

abstract class Model<S : Scope, VM : Any>(scopeClass: KClass<S>) : View<VM>, View.Nullable<VM>, Component(), ScopedInstance {

	override val scope: S = if (scopeClass.isInstance(super.scope))
		@Suppress("UNCHECKED_CAST")
		super.scope as S
	else error("Scope is not of type $scopeClass")

	private val itemProperty: ReadOnlyObjectWrapper<VM?> = ReadOnlyObjectWrapper(null)
	fun itemProperty(): ReadOnlyObjectProperty<VM?> = itemProperty.readOnlyProperty

	var item: VM?
		get() = itemProperty.get()
		set(value) {
			itemProperty.set(value)
			props.forEach { prop ->
				value?.let { prop.update(it) }
			}
		}


	abstract val applicationScope: ApplicationScope

	protected val threadTransformer by lazy {
		resolve<ThreadTransformer>(applicationScope)
	}

	private val invalidatedProperty = ReadOnlyBooleanWrapper(this, "invalidated", true)
	fun invalidatedProperty(): ReadOnlyBooleanProperty = invalidatedProperty.readOnlyProperty
	val invalidated: Boolean by invalidatedProperty()

	open fun viewModel(): VM? = item

	final override val viewModel: VM?
		get() = viewModel()

	override fun update(update: VM?.() -> VM) {
		threadTransformer.gui {
			item = viewModel().update()
		}
	}

	override fun updateOrInvalidated(update: VM.() -> VM) {
		threadTransformer.gui {
			val viewModel = viewModel() ?: return@gui invalidatedProperty.set(false)
			item = viewModel.update()
		}
	}

	private class BoundPropertyCannotBeUpdated(override val cause: Throwable) : Exception()

	inner class BoundProperty<R>(private val prop: WritableValue<R>, private val getter: (VM) -> R) {
		fun update(vm: VM) {
			val newValue = getter(vm)
			if (newValue != prop.value) {
				prop.value = newValue
			}
		}
	}
	private val props = mutableListOf<BoundProperty<*>>()

	@JvmName("bindInt")
	protected fun bind(prop: KProperty1<VM, Int>): SimpleIntegerProperty {
		return SimpleIntegerProperty().also {
			props.add(BoundProperty(it, prop))
		}
	}

	@JvmName("bindNullableInt")
	protected fun bind(prop: KProperty1<VM, Int?>): SimpleIntegerProperty {
		return SimpleIntegerProperty().also {
			props.add(BoundProperty(it, prop))
		}
	}

	@JvmName("bindLong")
	protected fun bind(prop: KProperty1<VM, Long>): SimpleLongProperty {
		return SimpleLongProperty().also {
			props.add(BoundProperty(it, prop))
		}
	}

	@JvmName("bindNullableLong")
	protected fun bind(prop: KProperty1<VM, Long?>): SimpleLongProperty {
		return SimpleLongProperty().also {
			props.add(BoundProperty(it, prop))
		}
	}

	@JvmName("bindDouble")
	protected fun bind(prop: KProperty1<VM, Double>): SimpleDoubleProperty {
		return SimpleDoubleProperty().also {
			props.add(BoundProperty(it, prop))
		}
	}

	@JvmName("bindNullableDouble")
	protected fun bind(prop: KProperty1<VM, Double?>): SimpleDoubleProperty {
		return SimpleDoubleProperty().also {
			props.add(BoundProperty(it, prop))
		}
	}

	@JvmName("bindFloat")
	protected fun bind(prop: KProperty1<VM, Float>): SimpleFloatProperty {
		return SimpleFloatProperty().also {
			props.add(BoundProperty(it, prop))
		}
	}

	@JvmName("bindNullableFloat")
	protected fun bind(prop: KProperty1<VM, Float?>): SimpleFloatProperty {
		return SimpleFloatProperty().also {
			props.add(BoundProperty(it, prop))
		}
	}

	@JvmName("bindString")
	protected fun bind(prop: KProperty1<VM, String>): SimpleStringProperty {
		return SimpleStringProperty().also {
			props.add(BoundProperty(it, prop))
		}
	}

	@JvmName("bindNullableString")
	protected fun bind(prop: KProperty1<VM, String?>): SimpleStringProperty {
		return SimpleStringProperty().also {
			props.add(BoundProperty(it, prop))
		}
	}

	@JvmName("bindBoolean")
	protected fun bind(prop: KProperty1<VM, Boolean>): SimpleBooleanProperty {
		return SimpleBooleanProperty().also {
			props.add(BoundProperty(it, prop))
		}
	}

	@JvmName("bindNullableBoolean")
	protected fun bind(prop: KProperty1<VM, Boolean?>): SimpleBooleanProperty {
		return SimpleBooleanProperty().also {
			props.add(BoundProperty(it, prop))
		}
	}

	@JvmName("bindList")
	protected fun <R> bind(prop: KProperty1<VM, List<R>>): SimpleListProperty<R> {
		return SimpleListProperty<R>(observableListOf()).also {
			props.add(BoundProperty(it) { vm ->
				prop.get(vm).toObservable()
			})
		}
	}

	@JvmName("bindNullableList")
	protected fun <R> bind(prop: KProperty1<VM, List<R>?>): SimpleListProperty<R> {
		return SimpleListProperty<R>(null).also {
			props.add(BoundProperty(it) { vm ->
				prop.get(vm)?.toObservable()
			})
		}
	}

	@JvmName("bindSet")
	protected fun <R> bind(prop: KProperty1<VM, Set<R>>): SimpleSetProperty<R> {
		return SimpleSetProperty<R>().also {
			props.add(BoundProperty(it) { vm ->
				prop.get(vm).toObservable()
			})
		}
	}

	@JvmName("bindNullableSet")
	protected fun <R> bind(prop: KProperty1<VM, Set<R>?>): SimpleSetProperty<R> {
		return SimpleSetProperty<R>().also {
			props.add(BoundProperty(it) { vm ->
				prop.get(vm)?.toObservable()
			})
		}
	}

	@JvmName("bindMap")
	protected fun <K, R> bind(prop: KProperty1<VM, Map<K, R>>): SimpleMapProperty<K, R> {
		return SimpleMapProperty<K, R>().also {
			props.add(BoundProperty(it) { vm ->
				prop.get(vm).toObservable()
			})
		}
	}

	@JvmName("bindNullableMap")
	protected fun <K, R> bind(prop: KProperty1<VM, Map<K, R>?>): SimpleMapProperty<K, R> {
		return SimpleMapProperty<K, R>().also {
			props.add(BoundProperty(it) { vm ->
				prop.get(vm)?.toObservable()
			})
		}
	}

	@JvmName("bindObject")
	protected fun <R : Any> bind(prop: KProperty1<VM, R>): SimpleObjectProperty<R> {
		return SimpleObjectProperty<R>().also {
			props.add(BoundProperty(it, prop))
		}
	}

	@JvmName("bindNullableObject")
	protected fun <R : Any> bind(prop: KProperty1<VM, R?>): SimpleObjectProperty<R> {
		return SimpleObjectProperty<R>().also {
			props.add(BoundProperty(it, prop))
		}
	}

	protected fun <R> bind(get: (VM?) -> R): ObservableValue<R> {
		return SimpleObjectProperty<R>().also {
			props.add(BoundProperty(it, get))
		}
	}
}

