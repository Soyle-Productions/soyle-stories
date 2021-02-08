package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.characterarc.characterList.CharacterList
import com.soyle.stories.characterarc.deleteCharacterDialog.DeleteCharacterDialogView
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.character.characterList.drive
import com.soyle.stories.desktop.view.character.characterList.driver
import com.soyle.stories.desktop.view.character.deleteCharacterDialog.DeleteCharacterDialogDriver
import com.soyle.stories.domain.character.Character
import javafx.scene.control.TreeItem
import tornadofx.uiComponent

fun CharacterList.givenDeleteCharacterDialogHasBeenOpened(characterId: Character.Id): DeleteCharacterDialogView =
    getDeleteCharacterDialog() ?: openDeleteCharacterDialog(characterId).let { getDeleteCharacterDialogOrError() }

fun CharacterList.openDeleteCharacterDialog(characterId: Character.Id) {
    with (driver()) {
        val item = getCharacterItemOrError(characterId)
        drive {
            tree.selectionModel.select(item as TreeItem<Any?>)
            clickOn(deleteButton)
        }
    }
}

fun getDeleteCharacterDialogOrError(): DeleteCharacterDialogView =
    getDeleteCharacterDialog() ?: throw NoSuchElementException("Delete Character Dialog is not open in project")

fun getDeleteCharacterDialog(): DeleteCharacterDialogView? =
    robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<DeleteCharacterDialogView>() }
        .firstOrNull { it.currentStage?.isShowing == true }

fun DeleteCharacterDialogView.confirmDelete()
{
    val driver = DeleteCharacterDialogDriver(this)
    driver.interact {
        driver.confirmButton.fire()
    }
}