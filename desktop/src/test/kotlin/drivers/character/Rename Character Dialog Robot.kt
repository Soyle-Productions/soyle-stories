package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.character.list.CharacterListView
import com.soyle.stories.character.rename.RenameCharacterForm
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.character.rename.RenameCharacterFormAccess.Companion.drive
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.domain.character.Character
import javafx.event.ActionEvent

fun CharacterListView.givenRenameCharacterDialogHasBeenOpened(characterId: Character.Id): RenameCharacterForm =
    robot.getOpenDialog<RenameCharacterForm>() ?: renameCharacter(characterId)!!

fun RenameCharacterForm.renameCharacterTo(newName: String)
{
    drive {
        characterNameInput.text = newName
        characterNameInput.fireEvent(ActionEvent())
    }
}