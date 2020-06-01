package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class CreateLocationDialog : Fragment("New Location") {

	private val model = find<CreateLocationDialogModel>()
	private val createLocationDialogViewListener: CreateLocationDialogViewListener = resolve()

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

	init {
		model.isOpen.onChangeUntil({ it != true }) {
			if (it != true) close()
		}
		createLocationDialogViewListener.getValidState()
	}

}

fun createLocationDialog(scope: ProjectScope): CreateLocationDialog = scope.get<CreateLocationDialog>().apply {
	openModal(StageStyle.UTILITY, Modality.APPLICATION_MODAL, escapeClosesWindow = true, owner = scope.get<WorkBench>().currentWindow)?.apply {
		centerOnScreen()
	}
}