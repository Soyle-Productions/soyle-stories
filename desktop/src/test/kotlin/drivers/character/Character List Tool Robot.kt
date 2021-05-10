package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.character.list.CharacterListView
import com.soyle.stories.character.rename.RenameCharacterForm
import com.soyle.stories.characterarc.characterList.CharacterTreeItemViewModel
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.character.list.CharacterListViewAccess.Companion.access
import com.soyle.stories.desktop.view.character.list.CharacterListViewAccess.Companion.drive
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.domain.character.Character
import com.soyle.stories.layout.config.fixed.CharacterList
import com.soyle.stories.project.WorkBench
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import tornadofx.FX

fun WorkBench.givenCharacterListToolHasBeenOpened(): CharacterListView =
    getCharacterListTool() ?: openCharacterListTool().let { getCharacterListToolOrError() }

fun WorkBench.getCharacterListToolOrError(): CharacterListView =
    getCharacterListTool() ?: throw NoSuchElementException("Theme List has not been opened")

fun WorkBench.getCharacterListTool(): CharacterListView?
{
    return (FX.getComponents(scope)[CharacterListView::class] as? CharacterListView)?.takeIf { it.currentStage?.isShowing == true }
}

fun WorkBench.openCharacterListTool()
{
    findMenuItemById("tools_characterlist")!!
        .apply { robot.interact { fire() } }
}

fun CharacterListView.renameCharacter(characterId: Character.Id): RenameCharacterForm?
{
    return with (access()) {
        val item = getCharacterItemOrError(characterId)
        drive {
            characterItemLayout!!.selectItem(item)
            val renameOptionItem = with(optionsButton!!) {
                show()
                renameOption!!
            }
            renameOptionItem.fire()
        }
        getOpenDialog<RenameCharacterForm>()
    }
}