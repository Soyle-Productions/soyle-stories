package com.soyle.stories.character.list

import com.soyle.stories.character.delete.DeleteCharacterFlow
import com.soyle.stories.character.rename.RenameCharacterFlow
import com.soyle.stories.characterarc.characterList.CharacterArcItemViewModel
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.characterList.CharacterListViewListener
import com.soyle.stories.characterarc.planCharacterArcDialog.planCharacterArcDialog
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.project.ProjectScope
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import tornadofx.Scope
import tornadofx.action
import tornadofx.item
import java.util.*

internal fun characterArcOptions(scope: Scope, arcItem: CharacterArcItemViewModel): List<MenuItem> {
    return listOf(
        MenuItem("Delete").apply {
            id = "delete"
            action {
                scope.get<CharacterListViewListener>().removeCharacterArc(arcItem.characterId, arcItem.themeId)
            }
        }
    )
}