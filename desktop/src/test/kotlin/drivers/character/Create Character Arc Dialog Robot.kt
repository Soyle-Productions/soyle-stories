package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.character.list.CharacterListView
import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialog
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.character.arc.create.`Create Character Arc View Access`.Companion.drive
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.domain.character.Character
import javafx.event.ActionEvent

fun CharacterListView.givenCreateCharacterArcDialogHasBeenOpened(characterId: Character.Id): PlanCharacterArcDialog =
    getCreateCharacterArcDialogOpenedFor(characterId) ?: openCreateCharacterArcDialogFor(characterId).run {
        getCreateCharacterArcDialogOpenedFor(characterId) ?: error("Create character arc dialog was not opened for $characterId")
    }

fun getCreateCharacterArcDialogOpenedFor(characterId: Character.Id): PlanCharacterArcDialog? =
    robot.getOpenDialog<PlanCharacterArcDialog>()?.takeIf { it.characterId == characterId.uuid.toString() }

fun PlanCharacterArcDialog.createCharacterArcNamed(arcName: String)
{
    drive {
        nameInput.requestFocus()
        nameInput.text = arcName
        nameInput.fireEvent(ActionEvent())
    }
}