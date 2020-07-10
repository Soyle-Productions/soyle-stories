package com.soyle.stories.common

import com.soyle.stories.di.resolve
import com.soyle.stories.gui.View
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import tornadofx.ItemViewModel
import tornadofx.Scope
import tornadofx.rebind
import kotlin.reflect.KClass

abstract class Model<S : Scope, VM : Any>(scopeClass: KClass<S>) : View.Nullable<VM>, ItemViewModel<VM>() {

	override val scope: S = if (scopeClass.isInstance(super.scope)) super.scope as S else error("Scope is not of type $scopeClass")

	abstract val applicationScope: ApplicationScope

	private val threadTransformer by lazy {
		resolve<ThreadTransformer>(applicationScope)
	}

	private val invalidatedProperty = ReadOnlyBooleanWrapper(this, "invalidated", true)
	fun invalidatedProperty(): ReadOnlyBooleanProperty = invalidatedProperty.readOnlyProperty
	val invalidated: Boolean by invalidatedProperty()

	open fun viewModel(): VM? = item

	override fun update(update: VM?.() -> VM) {
		threadTransformer.gui {
			rebind { item = viewModel().update() }
		}
	}

	override fun updateOrInvalidated(update: VM.() -> VM) {
		threadTransformer.gui {
			val viewModel = viewModel() ?: return@gui invalidatedProperty.set(false)
			rebind { item = viewModel.update() }
		}
	}
}

