package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.resolve
import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutViewListener
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.*

class CreateLocationDialog : View("New Location") {

	private val model = find<CreateLocationDialogModel>()
	private val createLocationDialogViewListener: CreateLocationDialogViewListener = resolve()
	private val layoutViewListener: LayoutViewListener = resolve()

	val name = SimpleStringProperty("")
	val description = SimpleStringProperty("")

	override val root = form {
		fieldset {
			field("Name") {
				textfield {
					id = "name"
					name.bind(textProperty())
					requestFocus()
					onAction = EventHandler {
						it.consume()
						createLocationDialogViewListener.createLocation(name.value, description.value)
					}
				}
			}
			field("Description (Optional)") {
				textfield {
					description.bind(textProperty())
				}
			}
			field {
				text(model.errorMessage) {
					id = "errorMessage"
				}
			}
		}
		hbox {
			button("Create") {
				id = "createLocation"
				action {
					createLocationDialogViewListener.createLocation(name.value, description.value)
				}
			}
			button("Cancel") {
				id = "cancel"
				action {
					close()
				}
			}
		}
	}

	override fun onUndock() {
		FX.getComponents(scope).remove(CreateLocationDialog::class)
		layoutViewListener.closeDialog(Dialog.CreateLocation::class)
	}

	init {
		model.isOpen.onChangeUntil({ it != true }) {
			if (it != true) close()
		}
		createLocationDialogViewListener.getValidState()
	}
}

fun Component.createLocationDialog(owner: Stage?): CreateLocationDialog = find {
	openModal(StageStyle.UTILITY, Modality.APPLICATION_MODAL, escapeClosesWindow = true, owner = owner)?.apply {
		centerOnScreen()
	}
}