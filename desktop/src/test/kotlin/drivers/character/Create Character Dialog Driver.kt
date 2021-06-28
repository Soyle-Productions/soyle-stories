package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterForm
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.project.WorkBench
import javafx.event.ActionEvent
import javafx.scene.control.TextField
import tornadofx.uiComponent

fun WorkBench.givenCreateCharacterDialogHasBeenOpened(): CreateCharacterForm =
    getCreateCharacterDialog() ?: findMenuItemById("file_new_character")!!
        .apply { robot.interact { fire() } }
        .let { getCreateCharacterDialogOrError() }

fun getCreateCharacterDialogOrError(): CreateCharacterForm =
    getCreateCharacterDialog() ?: throw NoSuchElementException("Create Character Dialog is not open in project")

fun getCreateCharacterDialog(): CreateCharacterForm? =
    robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<CreateCharacterForm>() }
        .firstOrNull { it.currentStage?.isShowing == true }

fun CreateCharacterForm.createCharacterWithName(characterName: String)
{
    val nameInput = robot.from(this.root).lookup(".text-field").query<TextField>()
    robot.interact {
        nameInput.text = characterName
        nameInput.fireEvent(ActionEvent())
    }
}
