package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterForm
import com.soyle.stories.desktop.config.drivers.character.getCreateCharacterDialogOrError
import com.soyle.stories.desktop.view.theme.characterComparison.`Character Card View Access`.Companion.access
import com.soyle.stories.desktop.view.theme.characterComparison.`Character Comparison View Access`.Companion.access
import com.soyle.stories.desktop.view.theme.characterComparison.`Character Comparison View Access`.Companion.drive
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.characterConflict.CharacterConflictScope
import com.soyle.stories.theme.characterValueComparison.CharacterValueComparison
import com.soyle.stories.theme.characterValueComparison.CharacterValueComparisonScope
import com.soyle.stories.theme.createOppositionValueDialog.CreateOppositionValueDialog
import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialog
import com.soyle.stories.theme.themeList.ThemeList
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.fail
import tornadofx.FX
import tornadofx.item

fun ThemeList.givenCharacterComparisonToolHasBeenOpenedFor(themeId: Theme.Id): CharacterValueComparison {
    return getOpenCharacterComparisonToolFor(themeId) ?: openCharacterComparison(themeId).run {
        getOpenCharacterComparisonToolFor(themeId) ?: error("character comparison tool not opened for $themeId")
    }
}

fun ThemeList.getOpenCharacterComparisonToolFor(themeId: Theme.Id): CharacterValueComparison? {
    val characterConflictScope = (scope as ProjectScope).toolScopes.asSequence().filterIsInstance<CharacterValueComparisonScope>()
        .find { it.type.themeId == themeId.uuid } ?: return null
    return (FX.getComponents(characterConflictScope)[CharacterValueComparison::class] as? CharacterValueComparison)
        ?.takeIf { it.currentStage?.isShowing == true }
}

fun CharacterValueComparison.givenCharacterHasBeenAdded(character: Character): CharacterValueComparison
{
    if (access().getCharacterCard(character.id) == null) addCharacter(character)
    assertNotNull(access().getCharacterCard(character.id))
    return this
}

fun CharacterValueComparison.addCharacter(character: Character)
{
    drive {
        addCharacterSelection.show()
        val availableCharacterItem = getAvailableCharacterToAdd(character.id)
            ?: fail("Character ${character.name} is not available to be added to theme")
        availableCharacterItem.fire()
    }
}

fun CharacterValueComparison.givenAvailableValuesHaveBeenLoadedFor(characterId: Character.Id): CharacterValueComparison
{
    if (! access().getCharacterCard(characterId)!!.access().addValueButton.isShowing) {
        loadAvailableValuesFor(characterId)
    }
    assertTrue(access().getCharacterCard(characterId)!!.access().addValueButton.isShowing)
    return this
}

fun CharacterValueComparison.loadAvailableValuesFor(characterId: Character.Id)
{
    drive {
        getCharacterCard(characterId)!!.access().addValueButton.show()
    }
}

fun CharacterValueComparison.givenCreateCharacterDialogHasBeenOpened(): CreateCharacterForm
{
    drive {
        addCharacterSelection.show()
        addCharacterSelection.createCharacterItem!!.fire()
    }
    return getCreateCharacterDialogOrError()
}

fun CharacterValueComparison.givenCreateOppositionValueDialogHasBeenOpenedFor(valueWebId: ValueWeb.Id): CreateOppositionValueDialog
{
    drive {
        characterCards.single { it.access().addValueButton.isShowing }
            .access {
                addValueButton.getCreateOppositionValueItem(valueWebId)!!.fire()
            }
    }
    return getCreateOppositionValueDialogOrError()
}

fun CharacterValueComparison.givenCreateValueWebDialogHasBeenOpened(): CreateValueWebDialog
{
    drive {
        characterCards.single { it.access().addValueButton.isShowing }
            .access {
                addValueButton.getCreateValueWebItem()!!.fire()
            }
    }
    return getCreateValueWebDialog() ?: fail("Create Value Web Dialog was not opened")
}

fun CharacterValueComparison.givenOppositionValueUsedForCharacter(characterId: Character.Id, oppositionValue: OppositionValue): CharacterValueComparison
{
    if (access().getCharacterCard(characterId)!!.access().getValue(oppositionValue.id) == null) {
        loadAvailableValuesFor(characterId)
        selectOppositionValue(oppositionValue)
    }
    return this
}

fun CharacterValueComparison.selectOppositionValue(oppositionValue: OppositionValue)
{
    drive {
        characterCards.single { it.access().addValueButton.isShowing }
            .access {
                addValueButton.getOppositionValueItem(oppositionValue.id)!!.fire()
            }
    }
}