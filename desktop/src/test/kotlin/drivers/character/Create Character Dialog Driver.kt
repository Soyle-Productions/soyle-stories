package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialog
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.project.WorkBench
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialog
import javafx.event.ActionEvent
import javafx.scene.control.TextField
import tornadofx.uiComponent

fun WorkBench.givenCreateCharacterDialogHasBeenOpened(): CreateCharacterDialog =
    getCreateCharacterDialog() ?: findMenuItemById("file_new_character")!!
        .apply { robot.interact { fire() } }
        .let { getCreateCharacterDialogOrError() }

fun getCreateCharacterDialogOrError(): CreateCharacterDialog =
    getCreateCharacterDialog() ?: throw NoSuchElementException("Create Character Dialog is not open in project")

fun getCreateCharacterDialog(): CreateCharacterDialog? =
    robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<CreateCharacterDialog>() }
        .firstOrNull { it.currentStage?.isShowing == true }

fun CreateCharacterDialog.createCharacterWithName(characterName: String)
{
    val nameInput = robot.from(this.root).lookup(".text-field").query<TextField>()
    robot.interact {
        nameInput.text = characterName
        nameInput.fireEvent(ActionEvent())
    }
}