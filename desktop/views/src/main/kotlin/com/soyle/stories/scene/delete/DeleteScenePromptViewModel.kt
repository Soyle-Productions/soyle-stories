package com.soyle.stories.scene.delete

import com.soyle.stories.scene.PromptChoice
import javafx.beans.binding.StringExpression
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.ButtonType
import tornadofx.booleanProperty
import tornadofx.stringProperty

class DeleteScenePromptViewModel {

	fun name(): StringExpression = stringProperty()
	var name: String = ""

	private val resultProperty = object : SimpleObjectProperty<ButtonType>(null) {
		override fun set(newValue: ButtonType?) {
			super.set(newValue)
			when (newValue?.buttonData) {
				DeleteScenePromptView.Delete -> onConfirm(PromptChoice.Confirm)
				DeleteScenePromptView.Ramifications -> onConfirm(PromptChoice.ShowRamifications)
				DeleteScenePromptView.Cancel -> onCancel()
				else -> error("Invalid selection")
			}
		}
	}
	fun result(): ObjectProperty<ButtonType> = resultProperty
	fun doNotShowAgain(): BooleanProperty = booleanProperty()

	val showAgain: Boolean
		get() = doNotShowAgain().not().get()

	private var onConfirm : (PromptChoice) -> Unit = {}

	fun setOnConfirm(handler: (PromptChoice) -> Unit) {
		onConfirm = handler
	}

	private var onCancel : () -> Unit = {}

	fun setOnCancel(handler: () -> Unit) {
		onCancel = handler
	}

}