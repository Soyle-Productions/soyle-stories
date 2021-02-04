package com.soyle.stories.characterarc.characterList

import com.soyle.stories.common.onChangeUntil
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import tornadofx.UIComponent
import tornadofx.confirm
import tornadofx.onChangeOnce

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