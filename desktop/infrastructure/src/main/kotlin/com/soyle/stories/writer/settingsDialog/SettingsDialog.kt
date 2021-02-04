package com.soyle.stories.writer.settingsDialog

import com.soyle.stories.di.resolveLater
import javafx.scene.Parent
import javafx.scene.control.ButtonBar
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class SettingsDialog : View() {

	private val viewListener by resolveLater<SettingsDialogViewListener>()
	private val model by resolveLater<SettingsDialogModel>()


	override val root: Parent = form {
		fieldset {
			textProperty.bind(model.dialogSectionLabel)
			vbox {
				bindChildren(model.dialogs) {
					field {
						checkbox(it.label) {
							id = it.dialogId
							isSelected = it.enabled
							selectedProperty().onChangeOnce { selected ->
								val dialogUpdate = model.dialogs.map { dialog ->
									if (dialog.dialogId == it.dialogId) {
										dialog.copy(enabled = selected ?: false)
									}
									else dialog
								}
								model.dialogUpdates.set(dialogUpdate.toObservable())
							}
						}
					}
				}
			}
		}
		buttonbar {
			button(text = "Save", type = ButtonBar.ButtonData.APPLY) {
				id = "save"
				enableWhen {
					model.dialogUpdates.selectBoolean {
						(model.dialogs.value != it).toProperty()
					}
				}
				action {
					viewListener.saveDialogs(model.dialogUpdates.map {
						it.dialogId to it.enabled
					})
					close()
				}
			}
			button(text = "Cancel", type = ButtonBar.ButtonData.CANCEL_CLOSE) {
				id = "cancel"
				requestFocus()
				action { close() }
			}
		}
	}

	fun show() {
		if (currentStage?.isShowing == true) return
		model.itemProperty().onChangeOnce {
			openModal(StageStyle.DECORATED, Modality.APPLICATION_MODAL)
		}
		viewListener.getValidState()
	}

	init {
		titleProperty.bind(model.title)
	}
}