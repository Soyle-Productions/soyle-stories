package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.character.list.CharacterListView
import com.soyle.stories.character.profile.CharacterProfileView
import com.soyle.stories.desktop.view.character.list.CharacterListViewAccess.Companion.drive
import com.soyle.stories.desktop.view.character.profile.`Character Profile View Access`.Companion.access
import com.soyle.stories.desktop.view.character.profile.`Character Profile View Access`.Companion.drive
import com.soyle.stories.domain.character.Character
import javafx.event.ActionEvent

fun CharacterListView.givenCharacterProfileOpenedFor(character: Character): CharacterProfileView =
    getCharacterProfileFor(character) ?: openCharacterProfileFor(character).run {
        getCharacterProfileFor(character) ?: error("Character profile was not opened for ${character.name}")
    }

fun CharacterListView.getCharacterProfileFor(character: Character): CharacterProfileView?
{
    return drive {
        characterProfile.takeIf { it?.props?.value?.characterId == character.id }
    }
}

fun CharacterProfileView.givenCreatingCharacterNameVariant(): CharacterProfileView
{
    if (access().isCreatingCharacterNameVariant) return this
    drive {
        createCharacterNameVariantButton.fire()
    }
    if (! access().isCreatingCharacterNameVariant) error("Create Character name variant button did not open form")
    return this
}

fun CharacterProfileView.createNameVariant(altName: String)
{
    drive {
        with(createCharacterNameVariantForm!!) {
            requestFocus()
            text = altName
            fireEvent(ActionEvent())
        }
    }
}

fun CharacterProfileView.givenRenamingCharacterNameVariant(variant: String): CharacterProfileView
{
    if (access().isRenamingNameVariant(variant)) return this
    drive {
        altNameRenameButton(variant)!!.fire()
    }
    if (! access().isRenamingNameVariant(variant)) error("Not renaming variant as expected.")
    return this
}

fun CharacterProfileView.renameVariantTo(originalVariant: String, newVariant: String)
{
    drive {
        val renameField = altNameRenameField(originalVariant)?.takeIf { it.isVisible }!!
        renameField.text = newVariant
        renameField.fireEvent(ActionEvent())
    }
}

fun CharacterProfileView.removeVariant(variant: String)
{
    drive {
        altNameDeleteButton(variant)!!.fire()
    }
}