package com.soyle.stories.character.list

import com.soyle.stories.character.delete.DeleteCharacterFlow
import com.soyle.stories.character.rename.RenameCharacterFlow
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.characterList.CharacterListItemViewModel
import com.soyle.stories.characterarc.planCharacterArcDialog.planCharacterArcDialog
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.Dialog
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.Separator
import javafx.scene.control.SeparatorMenuItem
import tornadofx.Scope
import tornadofx.action
import tornadofx.item
import tornadofx.separator
import java.util.*

internal fun characterOptions(scope: Scope, characterItem: CharacterItemViewModel): List<MenuItem> {
    return listOf(
        MenuItem("Rename").apply {
            id = "rename"
            action {
                scope.get<RenameCharacterFlow>().start(
                    characterItem.characterId.let(UUID::fromString).let(Character::Id),
                    characterItem.characterName
                )
            }
        },
        Menu("Additional Names").apply {
        },
        SeparatorMenuItem(),
        Menu("Create New...").apply {
            id = "create_new"
            item("Character Arc") {
                id = "create_new_arc"
                action {
                    planCharacterArcDialog(scope as ProjectScope, characterItem.characterId, null)
                }
            }
            item("Name Variant") {
                id = "create_new_name"
            }
        },
        MenuItem("Delete").apply {
            id = "delete"
            action {
                scope.get<DeleteCharacterFlow>().start(
                    characterItem.characterId.let(UUID::fromString).let(Character::Id),
                    characterItem.characterName
                )
            }
        }
    )
}