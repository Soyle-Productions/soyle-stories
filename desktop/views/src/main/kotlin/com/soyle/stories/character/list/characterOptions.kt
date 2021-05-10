package com.soyle.stories.character.list

import com.soyle.stories.character.delete.DeleteCharacterFlow
import com.soyle.stories.character.rename.RenameCharacterFlow
import com.soyle.stories.characterarc.characterList.CharacterListItemViewModel
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.Separator
import javafx.scene.control.SeparatorMenuItem
import tornadofx.Scope
import tornadofx.action
import tornadofx.item
import tornadofx.separator
import java.util.*

internal fun characterOptions(scope: Scope, characterItem: CharacterListItemViewModel): List<MenuItem> {
    return listOf(
        MenuItem("Rename").apply {
            id = "rename"
            action {
                scope.get<RenameCharacterFlow>().start(
                    characterItem.item.characterId.let(UUID::fromString).let(Character::Id),
                    characterItem.item.characterName
                )
            }
        },
        Menu("Additional Names").apply {
        },
        SeparatorMenuItem(),
        Menu("Create New...").apply {
            id = "create_new"
            item("Character Arc") {
                id = "create_new_name"
            }
            item("Name Variant") {
                id = "create_new_name"
            }
        },
        MenuItem("Delete").apply {
            id = "delete"
            action {
                scope.get<DeleteCharacterFlow>().start(
                    characterItem.item.characterId.let(UUID::fromString).let(Character::Id),
                    characterItem.item.characterName
                )
            }
        }
    )
}