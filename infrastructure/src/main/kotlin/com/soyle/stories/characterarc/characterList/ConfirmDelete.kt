package com.soyle.stories.characterarc.characterList

import com.soyle.stories.common.onChangeUntil
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import tornadofx.UIComponent
import tornadofx.confirm
import tornadofx.onChangeOnce

fun UIComponent.confirmDeleteCharacter(characterId: String, characterName: String, characterListViewListener: CharacterListViewListener) {
	val confirmButton = ButtonType("Delete", ButtonBar.ButtonData.FINISH)
	val alert = Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this character?", confirmButton, ButtonType.CANCEL)
	alert.title = "Confirm"
	alert.headerText = "Delete $characterName?"
	alert.dialogPane.styleClass.add("deleteCharacter")
	currentStage?.also { owner ->
		owner.showingProperty().onChangeUntil({ it != true }) {
			if (it != true) alert.hide()
		}
		alert.initOwner(owner)
	}
	alert.resultProperty().onChangeOnce {
		if (it == confirmButton) characterListViewListener.removeCharacter(characterId)
		alert.close()
	}
	alert.show()
}

fun UIComponent.confirmDeleteCharacterArc(characterId: String, themeId: String, characterArcName: String, characterListViewListener: CharacterListViewListener) {
	confirm(
	  header = "Delete $characterArcName?",
	  content = "Are you sure you want to delete this character arc?",
	  confirmButton = ButtonType("Delete", ButtonBar.ButtonData.APPLY),
	  cancelButton = ButtonType.CANCEL,
	  owner = currentStage,
	  title = "Confirm"
	) {
		characterListViewListener.removeCharacterArc(characterId, themeId)
	}
}