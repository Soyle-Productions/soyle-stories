package com.soyle.stories.character.list

import com.soyle.stories.character.delete.confirmDeleteCharacterPrompt
import com.soyle.stories.character.delete.ramifications.removeCharacterRamifications
import com.soyle.stories.character.nameVariant.create.CreateCharacterNameVariantFlow
import com.soyle.stories.character.removeCharacterFromStory.RemoveCharacterFromStoryController
import com.soyle.stories.character.rename.RenameCharacterFlow
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.planCharacterArcDialog.planCharacterArcDialog
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import tornadofx.Scope
import tornadofx.action
import tornadofx.item
import java.util.*

internal fun characterOptions(
    scope: ProjectScope,
    onViewProfile: (CharacterItemViewModel) -> Unit,
    characterItem: CharacterItemViewModel
): List<MenuItem> {
    return listOf(
        MenuItem("Rename").apply {
            id = "rename"
            action {
                scope.get<RenameCharacterFlow>().start(
                    characterItem.characterId,
                    NonBlankString.create(characterItem.characterName)!!
                )
            }
        },
        Menu("View Profile").apply {
            id = "profile"
            action {
                println("view profile $characterItem")
                onViewProfile(characterItem)
            }
        },
        SeparatorMenuItem(),
        Menu("Create New...").apply {
            id = "create_new"
            item("Character Arc") {
                id = "create_new_arc"
                action {
                    planCharacterArcDialog(scope as ProjectScope, characterItem.characterId.uuid.toString(), null)
                }
            }
            item("Name Variant") {
                id = "create_new_name"
                action {
                    scope.get<CreateCharacterNameVariantFlow>().start(
                        characterItem.characterId,
                    )
                }
            }
        },
        MenuItem("Delete").apply {
            id = "delete"
            action {
                val characterId = characterItem.characterId
                scope.get<RemoveCharacterFromStoryController>()
                    .removeCharacter(
                        characterId,
                        confirmDeleteCharacterPrompt(scope),
                        removeCharacterRamifications(characterId, scope)
                    )
            }
        }
    )
}