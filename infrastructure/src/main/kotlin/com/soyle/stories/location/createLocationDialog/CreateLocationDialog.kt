package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.common.launchTask
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.project.LayoutComponent
import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutViewListener
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.*

class CreateLocationDialog : Fragment("New Location") {

	private val model = find<CreateLocationDialogModel>()
	private val createLocationDialogViewListener: CreateLocationDialogViewListener = find<CreateLocationDialogComponent>().createLocationDialogViewListener
	private val layoutViewListener: LayoutViewListener = find<LayoutComponent>().layoutViewListener

	private val name = SimpleStringProperty("")
	private val description = SimpleStringProperty("")

	override val root = form {
		fieldset {
			field("Name") {
				textfield {
					name.bind(textProperty())
					requestFocus()
					onAction = EventHandler {
						it.consume()
						if (name.value.isEmpty())
						{
							return@EventHandler
						}
						launchTask {
							createLocationDialogViewListener.createLocation(name.value, description.value)
						}
					}
				}
			}
			field("Description (Optional)") {
				textfield {
					description.bind(textProperty())
				}
			}
			field {
				text(model.errorMessage)
			}
		}
		hbox {
			button("Create") {
				action {
					createLocationDialogViewListener.createLocation(name.value, description.value)
				}
			}
			button("Cancel") {
				action {
					close()
				}
			}
		}
	}

	override fun onUndock() {
		layoutViewListener.closeDialog(Dialog.CreateLocation::class)
	}

	init {
		model.isOpen.onChangeUntil({ it != true }) {
			if (it != true) close()
		}
	}
}

fun Component.createLocationDialog(owner: Stage?): CreateLocationDialog = find {
	openModal(StageStyle.UTILITY, Modality.APPLICATION_MODAL, escapeClosesWindow = true, owner = owner)?.apply {
		centerOnScreen()
	}
}