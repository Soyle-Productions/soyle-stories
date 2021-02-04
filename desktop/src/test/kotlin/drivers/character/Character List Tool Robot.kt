package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.characterarc.characterList.CharacterList
import com.soyle.stories.common.editingCell
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.character.characterList.drive
import com.soyle.stories.desktop.view.character.characterList.driver
import com.soyle.stories.entities.Character
import com.soyle.stories.project.WorkBench
import javafx.event.ActionEvent
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import tornadofx.FX

fun WorkBench.givenCharacterListToolHasBeenOpened(): CharacterList =
    getCharacterListTool() ?: openCharacterListTool().let { getCharacterListToolOrError() }

fun WorkBench.getCharacterListToolOrError(): CharacterList =
    getCharacterListTool() ?: throw NoSuchElementException("Theme List has not been opened")

fun WorkBench.getCharacterListTool(): CharacterList?
{
    return (FX.getComponents(scope)[CharacterList::class] as? CharacterList)?.takeIf { it.currentStage?.isShowing == true }
}

fun WorkBench.openCharacterListTool()
{
    findMenuItemById("tools_characterlist")!!
        .apply { robot.interact { fire() } }
}

fun CharacterList.renameCharacterTo(characterId: Character.Id, newName: String)
{
    with (driver()) {
        val item = getCharacterItemOrError(characterId)
        drive {
            tree.selectionModel.select(item as TreeItem<Any?>)
            val renameOptionItem = characterItemContextMenu!!.getRenameOption()
            renameOptionItem.fire()
            (tree.editingCell!!.graphic as TextField).run {
                text = newName
                fireEvent(ActionEvent())
            }
        }
    }
}