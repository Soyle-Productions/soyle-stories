package com.soyle.stories.character.list

import com.soyle.stories.characterarc.characterList.CharacterArcItemViewModel
import com.soyle.stories.di.get
import javafx.scene.control.MenuItem
import tornadofx.Scope
import tornadofx.action

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