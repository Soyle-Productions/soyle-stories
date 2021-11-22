package com.soyle.stories.scene.reorder

import com.soyle.stories.scene.PromptChoice
import com.soyle.stories.scene.delete.DeleteScenePromptView
import javafx.beans.binding.StringExpression
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.ButtonType
import tornadofx.booleanProperty
import tornadofx.objectProperty
import tornadofx.stringProperty

class ReorderScenePromptViewModel {

	fun name(): StringExpression = stringProperty()
	var name: String = ""

	fun result(): ObjectProperty<ButtonType> = object : SimpleObjectProperty<ButtonType>() {
		override fun set(newValue: ButtonType?) {
			super.set(newValue)
			when (newValue?.buttonData) {
				ReorderScenePromptView.Reorder -> onConfirm(PromptChoice.Confirm)
				ReorderScenePromptView.Ramifications -> onConfirm(PromptChoice.ShowRamifications)
				ReorderScenePromptView.Cancel -> onCancel()
				else -> error("Invalid selection")
			}
		}
	}
	fun doNotShowAgain(): BooleanProperty = booleanProperty()

	val showAgain: Boolean
		get() = doNotShowAgain().not().get()

	private var onConfirm: (PromptChoice) -> Unit = {}

	fun setOnConfirm(handler: (PromptChoice) -> Unit) {
		onConfirm = handler
	}

	private var onCancel: () -> Unit = {}

	fun setOnCancel(handler: () -> Unit) {
		onCancel = handler
	}

}