package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.character.list.CharacterListState
import com.soyle.stories.character.list.CharacterListView
import com.soyle.stories.character.rename.RenameCharacterForm
import com.soyle.stories.characterarc.characterList.CharacterArcItemViewModel
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.character.list.CharacterListViewAccess.Companion.access
import com.soyle.stories.desktop.view.character.list.CharacterListViewAccess.Companion.drive
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.project.WorkBench
import tornadofx.FX
import tornadofx.toProperty

fun WorkBench.givenCharacterListToolHasBeenOpened(): CharacterListView =
    getCharacterListTool() ?: openCharacterListTool().let { getCharacterListToolOrError() }

fun WorkBench.getCharacterListToolOrError(): CharacterListView =
    getCharacterListTool() ?: throw NoSuchElementException("Theme List has not been opened")

fun WorkBench.getCharacterListTool(): CharacterListView? {
    return (FX.getComponents(scope)[CharacterListView::class] as? CharacterListView)?.takeIf { it.currentStage?.isShowing == true }
}

fun WorkBench.openCharacterListTool() {
    findMenuItemById("tools_characterlist")!!
        .apply { robot.interact { fire() } }
}

fun CharacterListView.renameCharacter(characterId: Character.Id): RenameCharacterForm? {
    return with(access()) {
        val item = getCharacterItemOrError(characterId)
        drive {
            selectItem(item)
            val renameOptionItem = with(optionsButton!!) {
                show()
                renameOption!!
            }
            renameOptionItem.fire()
        }
        getOpenDialog<RenameCharacterForm>()
    }
}

fun CharacterListView.openCreateCharacterArcDialogFor(characterId: Character.Id) {
    drive {
        val item = getCharacterItemOrError(characterId)
        selectItem(item)
        val newCharacterArcOption = with(optionsButton!!) {
            show()
            newCharacterArcOption!!
        }
        newCharacterArcOption.fire()
    }
}

fun CharacterListView.selectItem(characterItemViewModel: CharacterItemViewModel) {
    val state = scope.get<CharacterListState>()
    state.selectedCharacterListItem.set(
        state.characters.find { it.character.value.characterId == characterItemViewModel.characterId }
    )
}

fun CharacterListView.selectItem(characterArcItemViewModel: CharacterArcItemViewModel) {
    val state = scope.get<CharacterListState>()
    state.selectedCharacterListItem.set(
        state.characters.asSequence().flatMap { it.arcs.asSequence() }.find {
            it.arc.value.characterId == characterArcItemViewModel.characterId && it.arc.value.themeId == characterArcItemViewModel.themeId
        }
    )
}

fun CharacterListView.deleteCharacterArc(characterId: Character.Id, themeId: Theme.Id) {
    drive {
        val item = getArcItemOrError(characterId, themeId)
        selectItem(item)
        val deleteOptionItem = with(optionsButton!!) {
            show()
            deleteOption!!
        }
        deleteOptionItem.fire()
    }
}

fun CharacterListView.openCharacterProfileFor(character: Character) {
    drive {
        val item = getCharacterItem(character.id)!!
        selectItem(item)
        val profileOptionItem = with(optionsButton!!) {
            show()
            profileOption!!
        }
        profileOptionItem.fire()
    }
}