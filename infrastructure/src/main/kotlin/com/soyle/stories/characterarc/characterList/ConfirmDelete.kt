package com.soyle.stories.characterarc.characterList

import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import tornadofx.UIComponent
import tornadofx.confirm

fun UIComponent.confirmDeleteCharacter(characterId: String, characterName: String, characterListViewListener: CharacterListViewListener) {
	confirm(
	  header = "Delete $characterName?",
	  content = "Are you sure you want to delete this character?",
	  confirmButton = ButtonType("Delete", ButtonBar.ButtonData.APPLY),
	  cancelButton = ButtonType.CANCEL,
	  owner = currentStage,
	  title = "Confirm"
	) {
		characterListViewListener.removeCharacter(characterId)
	}
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