package com.soyle.stories.scene.delete

import com.soyle.stories.common.Confirmation
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptViewModel
import com.soyle.stories.scene.PromptChoice
import javafx.beans.binding.StringExpression
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.ButtonType
import kotlinx.coroutines.CompletableDeferred
import tornadofx.booleanProperty
import tornadofx.stringProperty

class DeleteScenePromptViewModel : ConfirmationPromptViewModel(), DeleteScenePrompt {

	private val _name = stringProperty()
	fun name(): StringExpression = _name
	var name: String = ""

	override suspend fun requestConfirmation(sceneName: String): Confirmation<PromptChoice>? {
		val deferred = CompletableDeferred<Confirmation<PromptChoice>?>()

		onConfirm = { deferred.complete(Confirmation(PromptChoice.Confirm, !doNotShowAgain)) }
		onCheck = { deferred.complete(Confirmation(PromptChoice.ShowRamifications, !doNotShowAgain)) }
		onCancel = { deferred.complete(null) }

		_name.set(sceneName)
		_isNeeded.set(true)

		return deferred.await().also { _isNeeded.set(false) }
	}

	override fun close() {
		_isNeeded.set(false)
	}

}