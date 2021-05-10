package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.character.delete.DeleteCharacterForm
import com.soyle.stories.character.list.CharacterListView
import com.soyle.stories.characterarc.deleteCharacterDialog.DeleteCharacterDialogView
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.character.deleteCharacterDialog.DeleteCharacterFormAccess.Companion.drive
import com.soyle.stories.desktop.view.character.list.CharacterListViewAccess.Companion.access
import com.soyle.stories.desktop.view.character.list.CharacterListViewAccess.Companion.drive
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.domain.character.Character
import com.soyle.stories.layout.config.fixed.CharacterList
import javafx.scene.control.TreeItem
import tornadofx.uiComponent

fun CharacterListView.givenDeleteCharacterDialogHasBeenOpened(characterId: Character.Id): DeleteCharacterForm =
    getDeleteCharacterDialog() ?: openDeleteCharacterDialog(characterId).let { getDeleteCharacterDialogOrError() }

fun CharacterListView.openDeleteCharacterDialog(characterId: Character.Id) {
    with (access()) {
        val item = getCharacterItemOrError(characterId)
        drive {
            characterItemLayout!!.selectItem(item)
            optionsButton!!.show()
            optionsButton!!.deleteOption!!.fire()
        }
    }
}

fun getDeleteCharacterDialogOrError(): DeleteCharacterForm =
    getDeleteCharacterDialog() ?: throw NoSuchElementException("Delete Character Dialog is not open in project")

fun getDeleteCharacterDialog(): DeleteCharacterForm? = robot.getOpenDialog()

fun DeleteCharacterForm.confirmDelete()
{
    drive {
        confirmButton.fire()
    }
}